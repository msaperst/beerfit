package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.android.AndroidDriver;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

public class ImportExportAppiumTest extends AppiumTestBase {

    @Before
    public void wipeOutData() {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "rm -rf /sdcard/BeerFit");
        ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args);
    }

    @Test
    public void checkMenuExistsTest() {
        assertElementDisplayed(By.AccessibilityId("More options"));
    }

    @Test
    public void checkOptionsTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        assertEquals(menuOptions.size(), 2, "Expected to find '2' menu items", "Actually found '" + menuOptions.size() + "' menu items");
        assertElementTextEquals("Export Data", menuOptions.get(0));
        assertElementTextEquals("Import Data", menuOptions.get(1));
    }

    @Test
    public void checkPermissionsRequestExportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        assertElementDisplayed(By.id("com.android.permissioncontroller:id/permission_allow_button"));
        assertElementDisplayed(By.id("com.android.permissioncontroller:id/permission_deny_button"));
    }

    @Test
    public void checkPermissionsRequestImportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        assertElementDisplayed(By.id("com.android.permissioncontroller:id/permission_allow_button"));
        assertElementDisplayed(By.id("com.android.permissioncontroller:id/permission_deny_button"));
    }

    //TODO - request again - ensure no element for allow/deny

    @Test
    public void checkPermissionsDenyTest() {
        new Navigate(driver).clickOnExport();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_deny_button")).click();
        // assert files are not present
        Map<String, Object> args = new HashMap<>();
        args.put("command", "[ ! -d sdcard/BeerFit ] && echo 'false'");
        String output = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(output, "false\n", "Expected folder '/sdcard/BeerFit' not to exist",
                "No BeerFit folder exists");
    }

    @Test
    public void checkPermissionsAllowTest() {
        new Navigate(driver).export();
        // assert files are present
        Map<String, Object> args = new HashMap<>();
        args.put("command", "[ -d sdcard/BeerFit ] && echo 'true'");
        String directory = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(directory, "true\n", "Expected folder '/sdcard/BeerFit' to exist",
                "BeerFit folder exists");
        args.clear();
        args.put("command", "ls sdcard/BeerFit");
        String files = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(files, "Activities.csv\n" +
                        "Exercises.csv\n" +
                        "Goals.csv\n" +
                        "Measurements.csv\n", "Expected 4 database files to be present",
                "Files are: <br/>\n" + files);
    }

    @Test
    public void verifyDefaultExercisesExportTest() {
        new Navigate(driver).export();
        byte[] activities = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/BeerFit/Exercises.csv");
        assertEquals(new String(activities), "\"id\",\"past\",\"current\",\"color\"\n" +
                        "\"1\",\"Walked\",\"Walk\",\"-16711936\"\n" +
                        "\"2\",\"Ran\",\"Run\",\"-16776961\"\n" +
                        "\"3\",\"Cycled\",\"Cycle\",\"-65536\"\n" +
                        "\"4\",\"Lifted\",\"Lift\",\"-65281\"\n" +
                        "\"5\",\"Played Soccer\",\"Play Soccer\",\"-12303292\"\n",
                "Expected Base Activities Table", "Got: <br/>\n" + new String(activities));
    }

    @Test
    public void verifyDefaultActivitiesExportTest() {
        new Navigate(driver).export();
        byte[] activities = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/BeerFit/Activities.csv");
        assertEquals(new String(activities), "\"id\",\"time\",\"exercise\",\"measurement\",\"amount\",\"beers\"\n",
                "Expected Base Activities Table", "Got: <br/>\n" + new String(activities));
    }

    @Test
    public void verifyDefaultGoalsExportTest() throws IOException {
        new Navigate(driver).export();
        byte[] goals = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/BeerFit/Goals.csv");
        assertEquals(new String(goals), "\"id\",\"exercise\",\"measurement\",\"amount\"\n",
                "Expected Base Goals Table", "Got: <br/>\n" + new String(goals));
    }

    @Test
    public void verifyDefaultMeasurementsExportTest() {
        new Navigate(driver).export();
        byte[] measurements = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/BeerFit/Measurements.csv");
        assertEquals(new String(measurements), "\"id\",\"type\",\"unit\",\"conversion\"\n" +
                        "\"1\",\"time\",\"minute\",\"60\"\n" +
                        "\"2\",\"distance\",\"kilometer\",\"1\"\n" +
                "\"3\",\"time\",\"second\",\"3600\"\n" +
                "\"4\",\"time\",\"hour\",\"1\"\n" +
                "\"5\",\"distance\",\"mile\",\"0.621371\"\n" +
                "\"6\",,\"class\",\"-1\"\n" +
                "\"7\",,\"repetition\",\"-1\"\n",
                "Expected Base Measurements Table", "Got: <br/>\n" + new String(measurements));
    }

    @Test
    public void verifyAddedBeerActivitiesExportTest() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        driver.findElement(By.id("drankABeer")).click();
        new Navigate(driver).export();

        byte[] activities = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/BeerFit/Activities.csv");
        assertEquals(new String(activities), "\"id\",\"time\",\"exercise\",\"measurement\",\"amount\",\"beers\"\n" +
                "\"1\",\"" + date + "\",\"0\",\"0\",\"1\",\"-1\"\n", "Expected Base Activities Table: <br/>\n" +
                "\"id\",\"time\",\"exercise\",\"measurement\",\"amount\",\"beers\"\n" +
                "\"1\",\"" + date + "\",\"0\",\"0\",\"1\",\"-1\"\n", "Got: <br/>\n" + new String(activities));
    }

    @Test
    public void verifyImportTitle() {
        new Navigate(driver).mport();
        assertElementTextEquals("Select Table to Import", By.id("android:id/alertTitle"));
    }

    @Test
    public void verifyNothingToImportTest() {
        new Navigate(driver).mport();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 0, "Expected to find '0' import options", "Actually found '" + imports.size() + "'");
    }

    @Test
    public void verifyFullImportTest() {
        // write out some import files
        String goals = "\"id\",\"exercise\",\"measurement\",\"amount\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Goals.csv", Base64.encodeBase64(goals.getBytes()));
        String exercises = "\"id\",\"past\",\"current\",\"color\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Exercises.csv", Base64.encodeBase64(exercises.getBytes()));
        String activities = "\"id\",\"time\",\"exercise\",\"measurement\",\"amount\",\"beers\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Activities.csv", Base64.encodeBase64(activities.getBytes()));
        String measurements = "\"id\",\"type\",\"unit\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Measurements.csv", Base64.encodeBase64(measurements.getBytes()));
        // check the ability to import
        new Navigate(driver).mport();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 4, "Expected to find '4' import options", "Actually found '" + imports.size() + "'");
    }

    @Test
    public void verifyBadTitleImportTest() {
        // write out some import files
        String goals = "\"id\",\"exercise\",\"measurement\",\"amount\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Goals1.csv", Base64.encodeBase64(goals.getBytes()));
        // check the ability to import
        new Navigate(driver).mport();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 0, "Expected to find '0' import options", "Actually found '" + imports.size() + "'");
    }

    // TODO - bad header
    //      - bad data

    @Test
    public void verifyImportGoalsTest() throws SQLException, IOException, ClassNotFoundException {
        // write out some import files
        String goals = "\"id\",\"exercise\",\"measurement\",\"amount\"\n" +
                "\"4\",\"4\",\"1\",\"30\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Goals.csv", Base64.encodeBase64(goals.getBytes()));
        // check the ability to import
        Navigate nav = new Navigate(driver);
        nav.mport(0);
        // verify data was imported
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoal(resultSet, 4, 4, 1, 30);
    }

    @Test
    public void verifyImportActivitiesTest() throws SQLException, IOException, ClassNotFoundException {
        // write out some import files
        String activities = "\"id\",\"time\",\"exercise\",\"measurement\",\"amount\",\"beers\"\n" +
                "\"1\",\"2020-04-10 12:46:00\",\"2\",\"2\",\"10\",\"2\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/BeerFit/Activities.csv", Base64.encodeBase64(activities.getBytes()));
        // check the ability to import
        Navigate nav = new Navigate(driver);
        nav.mport(0);
        // verify data was imported
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + ";");
        resultSet.next();
        assertActivity(resultSet, 1, "2020-04-10 12:46:00", 2, 2, 10, 2);
        assertElementTextEquals("2 Beers Left", By.id("beersLeft"));
    }
}
