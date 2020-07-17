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
        List<WebElement> tableRows = driver.findElement(By.id("activitiesTable")).findElements(By.className("android.widget.TableRow"));
        List<WebElement> tableCell = tableRows.get(0).findElements(By.className("android.widget.TextView"));
        tableCell.get(1).click();
    }

    @Test
    public void editActivityTitleExists() {
        assertElementTextEquals("Edit Your Activity", By.id("android:id/alertTitle"));
    }

    @Test
    public void editActivityViewContentExists() {
        assertElementTextEquals("On", driver.findElement(By.id("on")));
        assertElementTextEquals("2020-02-15", By.id("activityDate"));
        assertElementTextEquals("at", driver.findElement(By.id("at")));
        assertElementTextEquals("23:59", By.id("activityTime"));
        assertElementTextEquals("Ran", driver.findElement(By.id("activityExercise")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("for", driver.findElement(By.id("_for_")));
        assertElementTextEquals("5.0", By.id("activityAmount"));
        assertElementTextEquals("kilometer", driver.findElement(By.id("activityMeasurement")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void checkEditActivityButtons() {
        List<WebElement> editActivityButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(editActivityButtons.size(), 2, "Expected to find '2' edit activity buttons", "Actually found '" + editActivityButtons.size() + "'");
    }

    @Test
    public void checkEditActivityUpdateButton() {
        assertElementTextEquals("UPDATE", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void checkEditActivityDeleteButton() {
        assertElementTextEquals("DELETE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
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
        driver.findElement(By.id("android:id/button2")).click();
        // verify we're back on view activities page
        assertElementTextEquals("BeerFit Activities", By.className("android.widget.TextView"));
    }

    @Test
    public void editActivityNoChanges() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button2")).click();
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
        driver.findElement(By.id("activityAmount")).clear();
        driver.findElement(By.id("activityAmount")).sendKeys("30");
        driver.findElement(By.id("activityMeasurement")).click();
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
        driver.findElement(By.id("android:id/button2")).click();
        //verify the activity is changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        resultSet.next();
        // beer changes to none, as no activities are set
        assertActivity(resultSet, 15, "2020-02-16 13:30", 4, 6, 30, 0);
    }
}
