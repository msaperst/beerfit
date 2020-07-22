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

public class ActivitiesEditBeerAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToActivities() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(9,\"2020-01-09 08:00\",0,0,1,-1);");
        new Navigate(driver).toActivities();
        List<WebElement> tableRows = driver.findElement(By.id("activitiesTable")).findElements(By.className("android.widget.TableRow"));
        List<WebElement> tableCell = tableRows.get(0).findElements(By.className("android.widget.TextView"));
        tableCell.get(1).click();
    }

    @Test
    public void editBeerTitleExists() {
        assertElementTextEquals("Edit Your Activity", By.id("android:id/alertTitle"));
    }

    @Test
    public void editBeerViewContentExists() {
        assertElementTextEquals("On", driver.findElement(By.id("on")));
        assertElementTextEquals("2020-01-09", By.id("activityDate"));
        assertElementTextEquals("at", driver.findElement(By.id("at")));
        assertElementTextEquals("08:00", By.id("activityTime"));
        assertElementTextEquals("Drank Beer", driver.findElement(By.id("activityExercise")));
        assertElementHidden(By.id("_for_"));
        assertElementTextEquals("1", By.id("activityAmount"));
        assertElementTextEquals("beer", driver.findElement(By.id("activityMeasurement")));
    }

    @Test
    public void checkEditBeerButtons() {
        List<WebElement> editBeerButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(editBeerButtons.size(), 2, "Expected to find '2' edit goal buttons", "Actually found '" + editBeerButtons.size() + "'");
    }

    @Test
    public void checkEditBeerUpdateButton() {
        assertElementTextEquals("UPDATE", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void checkEditBeerDeleteButton() {
        assertElementTextEquals("DELETE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
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
        driver.findElement(By.id("android:id/button2")).click();
        //verify the activity is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '9';");
        resultSet.next();
        assertActivity(resultSet, 9, "2020-01-09 08:00", 0, 0, 1, -1);
    }

    @Test
    public void editBeerChange() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("activityAmount")).clear();
        driver.findElement(By.id("activityAmount")).sendKeys("2");
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
        driver.findElement(By.id("android:id/button2")).click();
        //verify the activity is changed
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '9';");
        resultSet.next();
        // beer changes to none, as no activities are set
        assertActivity(resultSet, 9, "2020-01-16 13:30", 0, 0, 2, -2);
    }
}
