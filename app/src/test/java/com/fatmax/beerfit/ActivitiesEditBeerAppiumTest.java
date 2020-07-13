package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class ActivitiesEditBeerAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToActivities() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(9,\"2020-01-09 08:00\",0,0,1,-1);");
        new Navigate(driver).toActivities();
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
    }

    @Test
    public void editBeerGoBack() throws SQLException, IOException, ClassNotFoundException {
        new Navigate(driver).goBack();
        //verify the activity is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '9';");
        resultSet.next();
        assertActivity(resultSet, 9, "2020-01-09 08:00", 0, 0, 1, -1);
    }

    @Test
    public void editBeerTitle() {
        assertElementTextEquals("Edit Your Activity", By.className("android.widget.TextView"));
    }

    @Test
    public void editBeerActivityHeader() {
        assertElementTextEquals("Activity", By.id("activityExerciseHeader"));
    }

    @Test
    public void editBeerTime() {
        assertElementTextEquals("Update Time", By.id("activityDateTimeHeader"));
    }

    @Test
    public void editBeerButton() {
        assertElementTextEquals("UPDATE ACTIVITY", By.id("submitActivity"));
    }

    @Test
    public void editBeerAllData() {
        assertElementTextEquals("Drank Beer", driver.findElement(By.id("activityExercise")));
        assertElementTextEquals("2020-01-09", By.id("activityDate"));
        assertElementTextEquals("08:00", By.id("activityTime"));
        assertElementTextEquals("1.0", By.id("activityDurationInput"));
        assertElementTextEquals("beers", driver.findElement(By.id("activityDurationUnits")));
    }

    @Test
    public void editBeerDatePicker() {
        driver.findElement(By.id("activityDate")).click();
        assertElementTextEquals("2020", By.id("android:id/date_picker_header_year"));
        assertElementTextEquals("Thu, Jan 9", By.id("android:id/date_picker_header_date"));
    }

    @Test
    public void editBeerTimePicker() {
        driver.findElement(By.id("activityTime")).click();
        assertElementTextEquals("8", By.id("android:id/hours"));
        assertElementTextEquals("00", By.id("android:id/minutes"));
        String amState = driver.findElement(By.id("android:id/am_label")).getAttribute("checked");
        String pmState = driver.findElement(By.id("android:id/pm_label")).getAttribute("checked");
        assertEquals(amState, "true", "Expected AM to be checked", "AM checked state is " + amState);
        assertEquals(pmState, "false", "Expected PM to not be checked", "PM checked state is " + amState);
    }

    @Test
    public void editBeerNoChanges() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("submitActivity")).click();
        //verify the activity is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '9';");
        resultSet.next();
        assertActivity(resultSet, 9, "2020-01-09 08:00", 0, 0, 1, -1);
    }

    @Test
    public void editBeerChange() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("activityDurationInput")).clear();
        driver.findElement(By.id("activityDurationInput")).sendKeys("2");
        //set the date
        driver.findElement(By.id("activityDate")).click();
        driver.findElement(By.AccessibilityId("16 January 2020")).click();
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
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '9';");
        resultSet.next();
        // beer changes to none, as no activities are set
        assertActivity(resultSet, 9, "2020-01-16 13:30", 0, 0, 2, -2);
    }
}
