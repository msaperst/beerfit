package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.fatmax.beerfit.MetricsSeededAppiumTest.LIFTED_FOR_30_MINUTES;
import static com.fatmax.beerfit.MetricsSeededAppiumTest.RAN_FOR_5_KILOMETERS;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;

public class ActivitiesEditAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToActivities() {
        modifyDB("INSERT INTO " + ACTIVITY_LOG_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        new Navigate(driver).toActivities();
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
    }

    private WebElement getFirstActivityTime(List<WebElement> tableRows) {
        List<WebElement> textViews = tableRows.get(0).findElements(By.className("android.widget.TextView"));
        return textViews.get(0);
    }

    private WebElement getFirstActivityActivity(List<WebElement> tableRows) {
        List<WebElement> textViews = tableRows.get(0).findElements(By.className("android.widget.TextView"));
        return textViews.get(1);
    }

    @Test
    public void editActivityGoBack() {
        driver.findElement(By.AccessibilityId("Navigate up")).click();
        //verify the activity is not changed
        new Navigate(driver).toActivities();
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertElementTextEquals("Sat, Feb 15 2020, 23:59", getFirstActivityTime(tableRows));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getFirstActivityActivity(tableRows));
    }

    @Test
    public void editActivityTitle() {
        assertElementTextEquals("Edit Your Activity", By.className("android.widget.TextView"));
    }

    @Test
    public void editActivityTime() {
        assertElementTextEquals("Update Time", By.id("activityDateTimeHeader"));
    }

    @Test
    public void editActivityButton() {
        assertElementTextEquals("UPDATE ACTIVITY", By.id("submitActivity"));
    }

    @Test
    public void editActivityAllData() {
        assertElementTextEquals("Ran", driver.findElement(By.id("activitySelection")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("2020-02-15", By.id("activityDate"));
        assertElementTextEquals("23:59", By.id("activityTime"));
        assertElementTextEquals("5", By.id("activityDurationInput"));
        assertElementTextEquals("kilometers", driver.findElement(By.id("activityDurationUnits")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void editActivityDatePicker() {
        driver.findElement(By.id("activityDate")).click();
        assertElementTextEquals("2020", By.id("android:id/date_picker_header_year"));
        assertElementTextEquals("Sat, Feb 15", By.id("android:id/date_picker_header_date"));
    }

    @Test
    public void editActivityTimePicker() {
        driver.findElement(By.id("activityTime")).click();
        assertElementTextEquals("11", By.id("android:id/hours"));
        assertElementTextEquals("59", By.id("android:id/minutes"));
        String amState = driver.findElement(By.id("android:id/am_label")).getAttribute("checked");
        String pmState = driver.findElement(By.id("android:id/pm_label")).getAttribute("checked");
        assertEquals(amState, "false", "Expected AM to not be checked", "AM checked state is " + amState);
        assertEquals(pmState, "true", "Expected PM to be checked", "PM checked state is " + amState);
    }

    @Test
    public void editGoalNoChanges() {
        driver.findElement(By.id("submitActivity")).click();
        //verify the goal is not changed
        new Navigate(driver).toActivities();
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertElementTextEquals("Sat, Feb 15 2020, 23:59", getFirstActivityTime(tableRows));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getFirstActivityActivity(tableRows));
    }

    @Test
    public void editGoalChange() {
        driver.findElement(By.id("activitySelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(4).click();
        driver.findElement(By.id("activityDurationInput")).clear();
        driver.findElement(By.id("activityDurationInput")).sendKeys("30");
        driver.findElement(By.id("activityDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(1).click();
        //set the date
        driver.findElement(By.id("activityDate")).click();
        driver.findElement(By.AccessibilityId("16 February 2020")).click();
        driver.findElement(By.id("android:id/button1")).click();
        //set the time
        driver.findElement(By.id("activityTime")).click();
        driver.findElement(By.AccessibilityId("1")).click();
        driver.findElement(By.AccessibilityId("30")).click();
        driver.findElement(By.id("android:id/pm_label")).click();
        driver.findElement(By.id("android:id/button1")).click();
        // submit it
        driver.findElement(By.id("submitActivity")).click();
        //verify the goal is changed
        new Navigate(driver).toActivities();
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertElementTextEquals("Sun, Feb 16 2020, 13:30", getFirstActivityTime(tableRows));
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, getFirstActivityActivity(tableRows));
    }

    //TODO - edit beer page
}
