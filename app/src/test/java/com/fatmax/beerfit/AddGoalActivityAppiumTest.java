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

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

public class AddGoalActivityAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToAddActivity() {
        new Navigate(driver).toGoals();
        driver.findElement(By.id("addGoalButton")).click();
    }

    @Test
    public void titleExists() {
        assertElementTextEquals("Add A Goal", By.className("android.widget.TextView"));
    }

    @Test
    public void goalHeaderExists() {
        assertElementTextEquals("To Earn A Beer…", By.id("viewGoalsSubtitle"));
    }

    @Test
    public void goalSubHeaderExists() {
        assertElementTextEquals("Select Activity", By.id("goalSelectionHeader"));
    }

    @Test
    public void durationHeaderExists() {
        assertElementTextEquals("Enter Duration", By.id("goalDurationHeader"));
    }

    @Test
    public void addGoalButton() {
        assertElementTextEquals("ADD GOAL", By.id("submitGoal"));
    }

    @Test
    public void backGoesToGoals() {
        driver.findElement(By.AccessibilityId("Navigate up")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }

    @Test
    public void addEmptyActivity() {
        driver.findElement(By.id("submitGoal")).click();
        assertElementTextEquals("You need to indicate some activity", driver.findElement(By.id("goalSelection")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("", By.id("goalDurationInput"));
        assertElementTextEquals("", driver.findElement(By.id("goalDurationUnits")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void allActivitiesExist() {
        driver.findElement(By.id("goalSelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(activityList.size(), 6, "Expected to find '6' activities", "Actually found '" + activityList.size() + "' activities");
        assertElementTextEquals("", activityList.get(0));
        assertElementTextEquals("Walk", activityList.get(1));
        assertElementTextEquals("Run", activityList.get(2));
        assertElementTextEquals("Cycle", activityList.get(3));
        assertElementTextEquals("Lift", activityList.get(4));
        assertElementTextEquals("Play Soccer", activityList.get(5));
    }

    @Test
    public void allDurationsExist() {
        driver.findElement(By.id("goalDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(durationList.size(), 3, "Expected to find '3' durations", "Actually found '" + durationList.size() + "' durations");
        assertElementTextEquals("", durationList.get(0));
        assertElementTextEquals("minutes", durationList.get(1));
        assertElementTextEquals("kilometers", durationList.get(2));
    }

    @Test
    public void defaultSubmissionPossible() throws IOException, ClassNotFoundException, SQLException {
        driver.findElement(By.id("goalSelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("goalDurationInput")).sendKeys("10");
        driver.findElement(By.id("goalDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        driver.findElement(By.id("submitGoal")).click();
        // verify we're back on main page
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 10 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
        //verify the data is in there
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoals(resultSet, 1, 1, 2, 10);
    }
}
