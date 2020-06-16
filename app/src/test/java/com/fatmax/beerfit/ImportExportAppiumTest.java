package com.fatmax.beerfit;

import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.android.AndroidDriver;

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
    public void verifyDefaultActivitiesExportTest() throws IOException {
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
    public void verifyDefaultActivityLogExportTest() throws IOException {
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
    public void verifyDefaultMeasurementsExportTest() throws IOException {
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

    // TODO - check added goals provides data
    //      - check added activities provides data
    //      - all the imports
}
