package com.fatmax.beerfit;

import com.testpros.fast.AndroidDriver;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;
import com.testpros.fast.reporter.Step;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

@RunWith(MyRunner.class)
public class AppiumTestBase {

    @Rule
    public TestName name = new TestName();

    File sqliteDatabase = new File("beerfit");
    File app = new File("build/outputs/apk/debug/app-debug.apk");
    static File testResults = new File("build/reports/tests");
    String beerfitDatabase = "/data/data/com.fatmax.beerfit/databases/beerfit";
    String htmlTemplate = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n" +
            "\"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "<style>.PASS { color:green; } .FAIL { color:red; } table { border:2px solid darkgrey; border-collapse:collapse; } td,th { border:1px solid grey; }</style>\n" +
            "<title>$testCaseName</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>$testCaseName</h1>\n" +
            "<h2 class='$testCaseStatus'><span>$testCaseStatus</span> <span>$testCaseTime<span></h2>\n" +
            "<table>\n" +
            "<tr><th>Step</th><th>Action</th><th>Expected</th><th>Actual</th><th>Screenshot</th><th>Status</th><th>Time (ms)</th></tr>\n" +
            "$rows" +
            "</table>\n" +
            "</body>\n" +
            "</html>";
    AndroidDriver driver;
    AppiumDriverLocalService service = AppiumDriverLocalService.buildService(
            new AppiumServiceBuilder().usingAnyFreePort().withArgument(GeneralServerFlag.RELAXED_SECURITY));
    WebDriverWait wait;
    long waitTime = 5;
    long pollTime = 50;

    @Before
    public void setupDriver() throws IOException {
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

    @After
    public void tearDownDriver() throws IOException {
        driver.quit();
        service.stop();
        sqliteDatabase.delete();
        // write out my report
        StringBuilder steps = new StringBuilder();
        for (Step step : driver.getReporter().getSteps()) {
            steps.append("<tr>");
            steps.append("<td>").append(step.getNumber()).append("</td>");
            steps.append("<td>").append(step.getAction()).append("</td>");
            steps.append("<td>").append(step.getExpected()).append("</td>");
            steps.append("<td>").append(step.getActual()).append("</td>");
            if (step.getScreenshot() != null) {
                //TODO - toggle images
                steps.append("<td>").append("<img height='200' src='data:image/png;base64,").append(step.getScreenshot()).append("'/>").append("</td>");
            } else {
                steps.append("<td></td>");
            }
            steps.append("<td class='").append(step.getStatus()).append("'>").append(step.getStatus()).append("</td>");
            steps.append("<td>").append(step.getTime()).append("</td>");
            steps.append("</tr>");
        }
        String report = htmlTemplate.replace("$testCaseName", name.getMethodName())
                .replace("$testCaseStatus", driver.getReporter().getStatus().toString())
                .replace("$testCaseTime", driver.getReporter().getRunTime() + " ms")
                .replace("$rows", steps.toString());
        File reportFile = new File(testResults, name.getMethodName() + ".html");
        FileUtils.writeStringToFile(reportFile, report, Charset.defaultCharset());
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
        return conn.createStatement().executeQuery(query);
    }

    void assertGoals(ResultSet resultSet, int id, int activity, int measurement, int amount) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for activity in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getInt("activity"), activity, "Expected to find activity of '" + activity + "' for activity in DB",
                "Actually found activity of '" + resultSet.getInt("activity") + "'");
        assertEquals(resultSet.getInt("measurement"), measurement, "Expected to find measurement of '" + measurement + "' for activity in DB",
                "Actually found measurement of '" + resultSet.getInt("measurement") + "'");
        assertEquals(resultSet.getInt("amount"), amount, "Expected to find amount of '" + amount + "' for activity in DB",
                "Actually found amount of '" + resultSet.getInt("amount") + "'");
    }

    void assertActivityLog(ResultSet resultSet, int id, String dateTime, int activity, int measurement, int amount, int beers) throws SQLException {
        assertEquals(resultSet.getInt("id"), id, "Expected to find id of '" + id + "' for activity in DB",
                "Actually found id of '" + resultSet.getInt("id") + "'");
        assertEquals(resultSet.getString("time"), dateTime, "Expected to find time of '" +
                dateTime + "' for activity in DB", "Actually found id of '" + resultSet.getString("time") + "'");
        assertEquals(resultSet.getInt("activity"), activity, "Expected to find activity of '" + activity + "' for activity in DB",
                "Actually found activity of '" + resultSet.getInt("activity") + "'");
        assertEquals(resultSet.getInt("measurement"), measurement, "Expected to find measurement of '" + measurement + "' for activity in DB",
                "Actually found measurement of '" + resultSet.getInt("measurement") + "'");
        assertEquals(resultSet.getInt("amount"), amount, "Expected to find amount of '" + amount + "' for activity in DB",
                "Actually found amount of '" + resultSet.getInt("amount") + "'");
        assertEquals(resultSet.getInt("beers"), beers, "Expected to find beers of '" + beers + "' for beers in DB",
                "Actually found beers of '" + resultSet.getInt("beers") + "'");
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
        assertEquals(actual, expected, "Expected element '" + element + "' to have text '" + expected + "'",
                "Element '" + element + "' has text '" + actual + "'");
    }

    void assertElementDisplayed(WebElement element) {
        assertEquals(true, element.isDisplayed(), "Expected element '" + element.getAttribute("resourceId") + "' to be displayed",
                "Element '" + element.getAttribute("resourceId") + "' is visible");
    }

    void assertElementDisplayed(By element) {
        assertEquals(true, driver.findElement(element).isDisplayed(), "Expected element '" + element + "' to be displayed",
                "Element '" + element + "' is visible");
    }
}
