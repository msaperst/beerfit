package com.fatmax.beerfit;

import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.fatmax.beerfit.AddActivityActivity.DATE_FORMAT;
import static com.fatmax.beerfit.AddActivityActivity.TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;

public class AddActivityActivityAppiumTest extends AppiumTestBase {

    @BeforeEach
    public void navigateToAddActivity() {
        driver.findElement(By.id("earnedABeer")).click();
    }

    @Test
    public void titleExists() {
        assertElementTextEquals("Add An Activity", By.className("android.widget.TextView"));
    }

    @Test
    public void activityHeaderExists() {
        assertElementTextEquals("Select Activity", By.id("activitySelectionHeader"));
    }

    @Test
    public void timeHeaderExists() {
        assertElementTextEquals("Select Time", By.id("activityDateTimeHeader"));
    }

    @Test
    public void durationHeaderExists() {
        assertElementTextEquals("Enter Duration", By.id("activityDurationHeader"));
    }

    @Test
    public void addActivityButton() {
        assertElementTextEquals("ADD ACTIVITY", By.id("submitActivity"));
    }

    @Test
    public void backGoesToMain() {
        driver.findElement(By.AccessibilityId("Navigate up")).click();
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
    }

    @Test
    public void addEmptyActivity() {
        driver.findElement(By.id("submitActivity")).click();
        assertElementTextEquals("You need to indicate some activity", driver.findElement(By.id("activitySelection")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("", By.id("activityDurationInput"));
        assertElementTextEquals("", driver.findElement(By.id("activityDurationUnits")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void allActivitiesExist() {
        driver.findElement(By.id("activitySelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(activityList.size(), 6, "Expected to find '6' activities", "Actually found '" + activityList.size() + "' activities");
        assertElementTextEquals("", activityList.get(0));
        assertElementTextEquals("Walked", activityList.get(1));
        assertElementTextEquals("Ran", activityList.get(2));
        assertElementTextEquals("Cycled", activityList.get(3));
        assertElementTextEquals("Lifted", activityList.get(4));
        assertElementTextEquals("Played Soccer", activityList.get(5));
    }

    @Test
    public void currentDateDisplayed() {
        Calendar calendar = Calendar.getInstance();
        assertElementTextEquals(DATE_FORMAT.format(calendar.getTime()), By.id("activityDate"));
    }

    @Test
    public void canChangeDate() {
        driver.findElement(By.id("activityDate")).click();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        driver.findElement(By.AccessibilityId(new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(calendar.getTime()))).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals(DATE_FORMAT.format(calendar.getTime()), By.id("activityDate"));
    }

    @Test
    public void currentTimeDisplayed() {
        Calendar calendar = Calendar.getInstance();
        assertElementTextEquals(TIME_FORMAT.format(calendar.getTime()), By.id("activityTime"));
    }

    @Test
    public void canChangeTimeAM() {
        driver.findElement(By.id("activityTime")).click();
        driver.findElement(By.AccessibilityId("1")).click();
        driver.findElement(By.AccessibilityId("30")).click();
        driver.findElement(By.id("android:id/am_label")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("01:30", By.id("activityTime"));
    }

    @Test
    public void canChangeTimePM() {
        driver.findElement(By.id("activityTime")).click();
        driver.findElement(By.AccessibilityId("1")).click();
        driver.findElement(By.AccessibilityId("30")).click();
        driver.findElement(By.id("android:id/pm_label")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("13:30", By.id("activityTime"));
    }

    @Test
    public void allDurationsExist() {
        driver.findElement(By.id("activityDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(durationList.size(), 3, "Expected to find '3' durations", "Actually found '" + durationList.size() + "' durations");
        assertElementTextEquals("", durationList.get(0));
        assertElementTextEquals("minutes", durationList.get(1));
        assertElementTextEquals("kilometers", durationList.get(2));
    }

    @Test
    public void defaultSubmissionPossible() throws IOException, ClassNotFoundException, SQLException {
        driver.findElement(By.id("activitySelection")).click();
        Calendar calendar = Calendar.getInstance();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("activityDurationInput")).sendKeys("10");
        driver.findElement(By.id("activityDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        driver.findElement(By.id("submitActivity")).click();
        // verify we're back on main page
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
        //verify the data is in there
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(calendar.getTime());
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";");
        resultSet.next();
        assertActivityLog(resultSet, 1, dateTime, 1, 2, 10, 0);
    }

    @Test
    public void newDateSubmissionPossibleAM() throws IOException, SQLException, ClassNotFoundException {
        driver.findElement(By.id("activitySelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("activityDurationInput")).sendKeys("10");
        driver.findElement(By.id("activityDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        //set the date
        driver.findElement(By.id("activityDate")).click();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        driver.findElement(By.AccessibilityId(new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(calendar.getTime()))).click();
        driver.findElement(By.id("android:id/button1")).click();
        //set the time
        driver.findElement(By.id("activityTime")).click();
        driver.findElement(By.AccessibilityId("1")).click();
        driver.findElement(By.AccessibilityId("30")).click();
        driver.findElement(By.id("android:id/am_label")).click();
        driver.findElement(By.id("android:id/button1")).click();
        // submit it
        driver.findElement(By.id("submitActivity")).click();
        // verify we're back on main page
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
        //verify the data is in there
        String dateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()) + " 01:30";
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";");
        resultSet.next();
        assertActivityLog(resultSet, 1, dateTime, 1, 2, 10, 0);

        // TODO - add a goal to this so that beer isn't empty
    }

    @Test
    public void newDateSubmissionPossiblePM() throws IOException, SQLException, ClassNotFoundException {
        driver.findElement(By.id("activitySelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("activityDurationInput")).sendKeys("10");
        driver.findElement(By.id("activityDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        //set the date
        driver.findElement(By.id("activityDate")).click();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        driver.findElement(By.AccessibilityId(new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(calendar.getTime()))).click();
        driver.findElement(By.id("android:id/button1")).click();
        //set the time
        driver.findElement(By.id("activityTime")).click();
        driver.findElement(By.AccessibilityId("1")).click();
        driver.findElement(By.AccessibilityId("30")).click();
        driver.findElement(By.id("android:id/pm_label")).click();
        driver.findElement(By.id("android:id/button1")).click();
        // submit it
        driver.findElement(By.id("submitActivity")).click();
        // verify we're back on main page
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
        //verify the data is in there
        String dateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()) + " 13:30";
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";");
        resultSet.next();
        assertActivityLog(resultSet, 1, dateTime, 1, 2, 10, 0);

        // TODO - add a goal to this so that beer isn't empty
    }
}
