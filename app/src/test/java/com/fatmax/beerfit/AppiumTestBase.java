package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Report;
import com.testpros.fast.AndroidDriver;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;
import com.testpros.fast.reporter.FailedStepException;
import com.testpros.fast.reporter.Reporter;
import com.testpros.fast.reporter.Step;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import static com.fatmax.beerfit.objects.Report.convertStackTrace;
import static com.fatmax.beerfit.objects.Report.testResults;

public class AppiumTestBase {

    private static final Logger log = LoggerFactory.getLogger(AppiumTestBase.class);
    static Map<String, Reporter> testsExecuted = new HashMap<>();
    static Step.Status overallStatus = Step.Status.PASS;
    static long startTime = new Date().getTime();
    @Rule
    public TestName name = new TestName();
    File sqliteDatabase = new File("beerfit");
    File app = new File("build/outputs/apk/debug/app-debug.apk");
    String beerfitDatabase = "/data/data/com.fatmax.beerfit/databases/beerfit";
    AndroidDriver driver;
    AppiumDriverLocalService service;
    @Rule(order = Integer.MIN_VALUE)
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            //if you failed, and the reporter didn't catch it, get the failure, and stuff it into the report
            if (!(e instanceof FailedStepException)) {
                Step step = new Step("", "Didn't expected any errors to be thrown, however, one was.");
                step.setFailed(e.getClass() + ": " + e.getMessage() + "<br/>\n" + convertStackTrace(e));
                driver.getReporter().addStep(step);
            }
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            //TODO - deal with you later
        }

        @Override
        protected void finished(Description description) {
            driver.quit();
            service.stop();
            sqliteDatabase.delete();

            Report.addTestCase(testsExecuted, driver, name.getMethodName());
            overallStatus = Report.updateOverallStatus(overallStatus, driver);
            try {
                Report.writeTestReport(driver, name.getMethodName());
            } catch (IOException e) {
                log.error("Unable to create test report");
            }
        }
    };
    WebDriverWait wait;
    long waitTime = 5;
    long pollTime = 50;

    @AfterClass
    public static void allDone() throws IOException {
        Report.writeOverallReport(testsExecuted, overallStatus, startTime);
    }

    @Before
    public void setupDriver() throws IOException {
        service = AppiumDriverLocalService.buildService(
                new AppiumServiceBuilder().usingAnyFreePort()
                        .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                        .withLogFile(new File(testResults, name.getMethodName() + ".appium.log"))
                        .withArgument(GeneralServerFlag.LOG_LEVEL, "error:debug"));
        service.start();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 60);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        capabilities.setCapability("appPackage", "com.fatmax.beerfit");
        capabilities.setCapability("appActivity", "MainActivity");
        if (app.exists()) {
            capabilities.setCapability("app", app.getCanonicalPath());
        }
        driver = new AndroidDriver<>(service, capabilities);
        wait = new WebDriverWait(driver, waitTime, pollTime);
    }

    void modifyDB(String statement) {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "sqlite3 " + beerfitDatabase + " '" + statement + "'");
        ((io.appium.java_client.android.AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args);
    }

    ResultSet queryDB(String query) throws IOException, ClassNotFoundException, SQLException {
        byte[] sqldatabase = ((io.appium.java_client.android.AndroidDriver) driver.getDriver()).pullFile(beerfitDatabase);
        FileUtils.writeByteArrayToFile(sqliteDatabase, sqldatabase);
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:beerfit";
        Connection conn = DriverManager.getConnection(url);
        return conn.prepareStatement(query).executeQuery();
    }

    void assertGoal(ResultSet resultSet, int id, int exercise, int measurement, int amount) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for goal in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getInt("exercise"), exercise, "Expected to find exercise of '" + exercise + "' for goal in DB",
                "Actually found exercise of '" + resultSet.getInt("exercise") + "'");
        assertEquals(resultSet.getInt("measurement"), measurement, "Expected to find measurement of '" + measurement + "' for goal in DB",
                "Actually found measurement of '" + resultSet.getInt("measurement") + "'");
        assertEquals(resultSet.getInt("amount"), amount, "Expected to find amount of '" + amount + "' for goal in DB",
                "Actually found amount of '" + resultSet.getInt("amount") + "'");
    }

    void assertActivity(ResultSet resultSet, int id, String dateTime, int exercise, int measurement, double amount, double beers) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for activity in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getString("time"), dateTime, "Expected to find time of '" +
                dateTime + "' for activity in DB", "Actually found time of '" + resultSet.getString("time") + "'");
        assertEquals(resultSet.getInt("exercise"), exercise, "Expected to find exercise of '" + exercise + "' for activity in DB",
                "Actually found exercise of '" + resultSet.getInt("exercise") + "'");
        assertEquals(resultSet.getInt("measurement"), measurement, "Expected to find measurement of '" + measurement + "' for activity in DB",
                "Actually found measurement of '" + resultSet.getInt("measurement") + "'");
        assertEquals(resultSet.getDouble("amount"), amount, "Expected to find amount of '" + amount + "' for activity in DB",
                "Actually found amount of '" + resultSet.getDouble("amount") + "'");
        assertEquals(resultSet.getDouble("beers"), beers, "Expected to find beers of '" + beers + "' for activity in DB",
                "Actually found beers of '" + resultSet.getDouble("beers") + "'");
    }

    void assertExercise(ResultSet resultSet, int id, String past, String current, int color) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for exercise in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getString("past"), past, "Expected to find past of '" +
                past + "' for exercise in DB", "Actually found past of '" + resultSet.getString("past") + "'");
        assertEquals(resultSet.getString("current"), current, "Expected to find current of '" + current + "' for exercise in DB",
                "Actually found current of '" + resultSet.getString("current") + "'");
        assertEquals(resultSet.getInt("color"), color, "Expected to find color of '" + color + "' for exercise in DB",
                "Actually found color of '" + resultSet.getInt("color") + "'");
    }

    void assertMeasurement(ResultSet resultSet, int id, String type, String unit, double conversion) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for measurement in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getString("type"), type, "Expected to find type of '" +
                type + "' for measurement in DB", "Actually found type of '" + resultSet.getString("type") + "'");
        assertEquals(resultSet.getString("unit"), unit, "Expected to find unit of '" + unit + "' for measurement in DB",
                "Actually found unit of '" + resultSet.getString("unit") + "'");
        assertEquals(resultSet.getDouble("conversion"), conversion, "Expected to find conversion of '" + conversion + "' for measurement in DB",
                "Actually found conversion of '" + resultSet.getDouble("conversion") + "'");
    }

    void assertEquals(Object actual, Object expected, String expectedString, String actualString) {
        Step step = new Step("", expectedString);
        try {
            org.junit.Assert.assertEquals(expected, actual);
            step.setPassed();
        } catch (AssertionError e) {
            step.setFailed();
            throw e;
        } finally {
            step.setActual(actualString);
            driver.getReporter().addStep(step);
        }
    }

    void assertElementTextEquals(String expected, WebElement element) {
        String actual = element.getText();
        assertEquals(actual, expected, "Expected element '" + element.getAttribute("resourceId") + "' to have text '" + expected + "'",
                "Element '" + element.getAttribute("resourceId") + "' has text '" + actual + "'");
    }

    void assertElementTextEquals(String expected, By element) {
        String actual = driver.findElement(element).getText();
        assertEquals(actual, expected, "Expected element '" + element.getBy() + "' to have text '" + expected + "'",
                "Element '" + element.getBy() + "' has text '" + actual + "'");
    }

    void assertElementDisplayed(WebElement element) {
        assertEquals(true, element.isDisplayed(), "Expected element '" + element.getAttribute("resourceId") + "' to be displayed",
                "Element '" + element.getAttribute("resourceId") + "' visibility is set to '" + element.isDisplayed() + "'");
    }

    void assertElementDisplayed(By element) {
        assertEquals(true, driver.findElement(element).isDisplayed(), "Expected element '" + element.getBy() + "' to be displayed",
                "Element '" + element.getBy() + "' visibility is set to '" + driver.findElement(element).isDisplayed() + "'");
    }

    void assertElementEnabled(WebElement element) {
        assertEquals(true, element.isEnabled(), "Expected element '" + element.getAttribute("resourceId") + "' to be enabled",
                "Element '" + element.getAttribute("resourceId") + "' enablement is set to '" + element.isEnabled() + "'");
    }

    void assertElementEnabled(By element) {
        assertEquals(true, driver.findElement(element).isEnabled(), "Expected element '" + element.getBy() + "' to be enabled",
                "Element '" + element.getBy() + "' enablement is set to '" + driver.findElement(element).isEnabled() + "'");
    }

    void assertElementDisabled(WebElement element) {
        assertEquals(false, element.isEnabled(), "Expected element '" + element.getAttribute("resourceId") + "' to be disabled",
                "Element '" + element.getAttribute("resourceId") + "' enablement is set to '" + element.isEnabled() + "'");
    }

    void assertElementDisabled(By element) {
        assertEquals(false, driver.findElement(element).isEnabled(), "Expected element '" + element.getBy() + "' to be disabled",
                "Element '" + element.getBy() + "' enablement is set to '" + driver.findElement(element).isEnabled() + "'");
    }
}
