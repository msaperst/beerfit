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
        new Navigate(driver).toNewGoal();
    }

    @Test
    public void goalTitleExists() {
        assertElementTextEquals("To Earn A Beer…", By.id("android:id/alertTitle"));
    }

    @Test
    public void goalViewContentExists() {
        assertElementDisplayed(By.id("goalExercise"));
        assertElementTextEquals("for", By.id("_for_"));
        assertElementDisplayed(By.id("goalAmount"));
        assertElementDisplayed(By.id("goalMeasurement"));
    }

    @Test
    public void checkAddGoalButtons() {
        List<WebElement> addGoalButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(addGoalButtons.size(), 1, "Expected to find '1' add goal button", "Actually found '" + addGoalButtons.size() + "'");
    }

    @Test
    public void checkAddGoalAddGoalButton() {
        assertElementTextEquals("ADD NEW", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void addEmptyGoal() {
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("You need to indicate some exercise", driver.findElement(By.id("goalExercise")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("", By.id("goalAmount"));
        assertElementTextEquals("", driver.findElement(By.id("goalMeasurement")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void allGoalActivitiesExist() {
        driver.findElement(By.id("goalExercise")).click();
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
        driver.findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(durationList.size(), 8, "Expected to find '8' durations", "Actually found '" + durationList.size() + "' durations");
        assertElementTextEquals("", durationList.get(0));
        assertElementTextEquals("class", durationList.get(1));
        assertElementTextEquals("repetition", durationList.get(2));
        assertElementTextEquals("mile", durationList.get(3));
        assertElementTextEquals("kilometer", durationList.get(4));
        assertElementTextEquals("hour", durationList.get(5));
        assertElementTextEquals("minute", durationList.get(6));
        assertElementTextEquals("second", durationList.get(7));
    }

    @Test
    public void addingGoalGoesToGoalsPage() {
        driver.findElement(By.id("goalExercise")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("goalAmount")).sendKeys("10");
        driver.findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        // verify we're back on main page
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }

    @Test
    public void defaultGoalSubmissionPossible() throws IOException, ClassNotFoundException, SQLException {
        driver.findElement(By.id("goalExercise")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(1).click();
        driver.findElement(By.id("goalAmount")).sendKeys("10");
        driver.findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(4).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the data is in there
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 1, 1, 2, 10);
    }
}
