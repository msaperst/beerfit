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

public class GoalsAddAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToAddGoals() {
        new Navigate(driver).toGoals();
        driver.findElement(By.id("addGoalButton")).click();
    }

    @Test
    public void goalTitleExists() {
        assertElementTextEquals("Add A Goal", By.className("android.widget.TextView"));
    }

    @Test
    public void goalHeaderExists() {
        assertElementTextEquals("To Earn A Beer…", By.id("viewGoalsSubtitle"));
    }

    @Test
    public void goalSubHeaderExists() {
        assertElementTextEquals("Select Exercise", By.id("goalSelectionHeader"));
    }

    @Test
    public void goalDurationHeaderExists() {
        assertElementTextEquals("Enter Duration", By.id("goalDurationHeader"));
    }

    @Test
    public void addGoalButton() {
        assertElementTextEquals("ADD GOAL", By.id("submitGoal"));
    }

    @Test
    public void backGoesToGoals() {
        new Navigate(driver).goBack();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }

    @Test
    public void addEmptyGoal() {
        driver.findElement(By.id("submitGoal")).click();
        assertElementTextEquals("You need to indicate some exercise", driver.findElement(By.id("goalSelection")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("", By.id("goalDurationInput"));
        assertElementTextEquals("", driver.findElement(By.id("goalDurationUnits")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void allGoalActivitiesExist() {
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
    public void allGoalDurationsExist() {
        driver.findElement(By.id("goalDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(durationList.size(), 8, "Expected to find '8' durations", "Actually found '" + durationList.size() + "' durations");
        assertElementTextEquals("", durationList.get(0));
        assertElementTextEquals("miles", durationList.get(1));
        assertElementTextEquals("kilometers", durationList.get(2));
        assertElementTextEquals("hours", durationList.get(3));
        assertElementTextEquals("minutes", durationList.get(4));
        assertElementTextEquals("seconds", durationList.get(5));
        assertElementTextEquals("classes", durationList.get(6));
        assertElementTextEquals("repetitions", durationList.get(7));
    }

    //TODO - starts as plural, goes to singular if 1 is the value, back to plural when not 1 (e.g. 10)

    @Test
    public void addingGoalGoesToGoalsPage() {
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
    }

    @Test
    public void defaultGoalSubmissionPossible() throws IOException, ClassNotFoundException, SQLException {
        driver.findElement(By.id("goalSelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("goalDurationInput")).sendKeys("10");
        driver.findElement(By.id("goalDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        driver.findElement(By.id("submitGoal")).click();
        //verify the data is in there
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoal(resultSet, 1, 1, 2, 10);
    }
}
