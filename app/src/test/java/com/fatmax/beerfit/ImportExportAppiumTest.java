package com.fatmax.beerfit;

import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class ImportExportAppiumTest extends AppiumTestBase {

    @Before
    public void wipeOutData() {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "rm -rf /sdcard/Beerfit");
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
    public void checkPermissionsRequestTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        assertElementDisplayed(By.id("com.android.permissioncontroller:id/permission_allow_button"));
    }

    @Test
    public void checkPermissionsDenyTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_deny_button")).click();
        // assert files are not present
        Map<String, Object> args = new HashMap<>();
        args.put("command", "[ ! -d sdcard/Beerfit ] && echo 'false'");
        String output = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(output, "false\n", "Expected folder '/sdcard/Beerfit' not to exist",
                "No Beerfit folder exists");
    }

    @Test
    public void checkPermissionsAllowTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        // assert files are present
        Map<String, Object> args = new HashMap<>();
        args.put("command", "[ -d sdcard/Beerfit ] && echo 'true'");
        String directory = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(directory, "true\n", "Expected folder '/sdcard/Beerfit' to exist",
                "Beerfit folder exists");
        args.clear();
        args.put("command", "ls sdcard/Beerfit");
        String files = ((AndroidDriver) driver.getDriver()).executeScript("mobile: shell", args).toString();
        assertEquals(files, "Activities.csv\n" +
                        "ActivityLog.csv\n" +
                        "Goals.csv\n" +
                        "Measurements.csv\n", "Expected 4 database files to be present",
                "Files are: <br/>\n" + files);
    }

    @Test
    public void verifyDefaultActivitiesExportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        byte[] activities = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/Beerfit/Activities.csv");
        assertEquals(new String(activities), "\"id\",\"past\",\"current\",\"color\"\n" +
                        "\"1\",\"Walked\",\"Walk\",\"-16711936\"\n" +
                        "\"2\",\"Ran\",\"Run\",\"-16776961\"\n" +
                        "\"3\",\"Cycled\",\"Cycle\",\"-65536\"\n" +
                        "\"4\",\"Lifted\",\"Lift\",\"-65281\"\n" +
                        "\"5\",\"Played Soccer\",\"Play Soccer\",\"-12303292\"\n",
                "Expected Base Activities Table", "Got: <br/>\n" + new String(activities));
    }

    @Test
    public void verifyDefaultActivityLogExportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        byte[] activityLog = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/Beerfit/ActivityLog.csv");
        assertEquals(new String(activityLog), "\"id\",\"time\",\"activity\",\"measurement\",\"amount\",\"beers\"\n",
                "Expected Base ActivityLog Table", "Got: <br/>\n" + new String(activityLog));
    }

    @Test
    public void verifyDefaultGoalsExportTest() throws IOException {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        byte[] goals = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/Beerfit/Goals.csv");
        assertEquals(new String(goals), "\"id\",\"activity\",\"measurement\",\"amount\"\n",
                "Expected Base Goals Table", "Got: <br/>\n" + new String(goals));
    }

    @Test
    public void verifyDefaultMeasurementsExportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        byte[] measurements = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/Beerfit/Measurements.csv");
        assertEquals(new String(measurements), "\"id\",\"type\",\"unit\"\n" +
                        "\"1\",\"time\",\"minutes\"\n" +
                        "\"2\",\"distance\",\"kilometers\"\n",
                "Expected Base Measurements Table", "Got: <br/>\n" + new String(measurements));
    }

    @Test
    public void verifyAddedBeerActivityLogExportTest() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        driver.findElement(By.id("drankABeer")).click();
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        byte[] activityLog = ((AndroidDriver) driver.getDriver()).pullFile("/sdcard/Beerfit/ActivityLog.csv");
        String x = new String(activityLog);
        assertEquals(new String(activityLog), "\"id\",\"time\",\"activity\",\"measurement\",\"amount\",\"beers\"\n" +
                        "\"1\",\"" + date + "\",\"0\",\"0\",\"1\",\"-1\"\n",
                "Expected Base ActivityLog Table", "Got: <br/>\n" + new String(activityLog));
    }

    @Test
    public void verifyNothingToImportTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        assertElementDisplayed(By.id("android:id/alertTitle"));
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 0, "Expected to find '0' import options", "Actually found '" + imports.size() + "'");
    }

    @Test
    public void verifyFullImportTest() {
        // write out some import files
        String goals = "\"id\",\"activity\",\"measurement\",\"amount\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/Goals.csv", Base64.encodeBase64(goals.getBytes()));
        String activities = "\"id\",\"past\",\"current\",\"color\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/Activities.csv", Base64.encodeBase64(activities.getBytes()));
        String activityLog = "\"id\",\"time\",\"activity\",\"measurement\",\"amount\",\"beers\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/ActivityLog.csv", Base64.encodeBase64(activityLog.getBytes()));
        String measurements = "\"id\",\"type\",\"unit\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/Measurements.csv", Base64.encodeBase64(measurements.getBytes()));
        // check the ability to import
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 4, "Expected to find '4' import options", "Actually found '" + imports.size() + "'");
    }

    @Test
    public void verifyBadTitleImportTest() {
        // write out some import files
        String goals = "\"id\",\"activity\",\"measurement\",\"amount\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/Goals1.csv", Base64.encodeBase64(goals.getBytes()));
        // check the ability to import
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        assertEquals(imports.size(), 0, "Expected to find '0' import options", "Actually found '" + imports.size() + "'");
    }

    // TODO - bad header
    //      - bad data

    @Test
    public void verifyImportGoalsTest() {
        // write out some import files
        String goals = "\"id\",\"activity\",\"measurement\",\"amount\"\n" +
                "\"4\",\"4\",\"1\",\"30\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/Goals.csv", Base64.encodeBase64(goals.getBytes()));
        // check the ability to import
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        imports.get(0).click();
        // verify data was imported
        driver.findElement(By.id("viewGoals")).click();
        WebElement table = driver.findElement(By.id("goalsTable"));
        List<WebElement> tableRows = table.findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' table rows", "Actually found '" + tableRows.size() + "'");
        List<WebElement> texts = table.findElements(By.className("android.widget.TextView"));
        assertElementTextEquals("Lift for 30 minutes", texts.get(0));
    }

    @Test
    public void verifyImportActivityLogTest() {
        // write out some import files
        String activityLog = "\"id\",\"time\",\"activity\",\"measurement\",\"amount\",\"beers\"\n" +
                "\"1\",\"2020-04-10 12:46:00\",\"2\",\"2\",\"10\",\"2\"";
        ((AndroidDriver) driver.getDriver()).pushFile("/sdcard/Beerfit/ActivityLog.csv", Base64.encodeBase64(activityLog.getBytes()));
        // check the ability to import
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        imports.get(0).click();
        // verify data was imported
        driver.findElement(By.id("viewActivities")).click();
        WebElement table = driver.findElement(By.id("activityBodyTable"));
        List<WebElement> tableRows = table.findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' table rows", "Actually found '" + tableRows.size() + "'");
        List<WebElement> texts = table.findElements(By.className("android.widget.TextView"));
        assertElementTextEquals("Fri, Apr 10 2020, 12:46", texts.get(0));
        assertElementTextEquals("Ran for 10.0 kilometers", texts.get(1));
        // TODO - there is a bug, want this to be redrawn (no page switching needed)
        ((AndroidDriver) driver.getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
        assertElementTextEquals("2", By.id("beersLeft"));
    }
}
