package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class ActivitiesEditActivityAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToActivities() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        new Navigate(driver).toActivities();
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
    }

    @Test
    public void editActivityGoBack() throws SQLException, IOException, ClassNotFoundException {
        new Navigate(driver).goBack();
        //verify the activity is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        resultSet.next();
        assertActivity(resultSet, 15, "2020-02-15 23:59", 2, 2, 5, 1);
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
        assertElementTextEquals("Ran", driver.findElement(By.id("activityExercise")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("2020-02-15", By.id("activityDate"));
        assertElementTextEquals("23:59", By.id("activityTime"));
        assertElementTextEquals("5.0", By.id("activityDurationInput"));
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
    public void editActivityGoesToActivityPage() {
        driver.findElement(By.id("submitActivity")).click();
        // verify we're back on view activities page
        assertElementTextEquals("BeerFit Activities", By.className("android.widget.TextView"));
    }

    @Test
    public void editActivityNoChanges() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("submitActivity")).click();
        //verify the activity is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        resultSet.next();
        // beer changes to none, as no activities are set
        assertActivity(resultSet, 15, "2020-02-15 23:59", 2, 2, 5, 0);
    }

    @Test
    public void editActivityChange() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("activityExercise")).click();
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
        //verify the activity is changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        resultSet.next();
        // beer changes to none, as no activities are set
        assertActivity(resultSet, 15, "2020-02-16 13:30", 4, 1, 30, 0);
    }
}
