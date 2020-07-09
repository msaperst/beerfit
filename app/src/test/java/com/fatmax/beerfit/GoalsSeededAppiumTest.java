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

public class GoalsSeededAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToGoals() {
        modifyDB("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        new Navigate(driver).toGoals();
    }


    @Test
    public void addedGoalsDisplayed() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 5.0 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
    }

    @Test
    public void dontDeleteGoal() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the goal is still there
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoals(resultSet, 1, 1, 2, 5);
    }

    @Test
    public void deleteGoal() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the goal is gone
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        assertEquals(false, resultSet.next(), "Expected no results", "");
    }

    @Test
    public void editGoalGoesToGoalPage() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        driver.findElement(By.id("submitGoal")).click();
        // verify we're back on view activities page
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }

    @Test
    public void editGoalGoBack() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        new Navigate(driver).goBack();
        //verify the goal is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoals(resultSet, 1, 1, 2, 5);
    }

    @Test
    public void editGoalTitle() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        assertElementTextEquals("Edit Your Goal", By.className("android.widget.TextView"));
    }

    @Test
    public void editGoalButton() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        assertElementTextEquals("UPDATE GOAL", By.id("submitGoal"));
    }

    @Test
    public void editGoalAllData() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        assertElementTextEquals("Walk", driver.findElement(By.id("goalSelection")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("5.0", By.id("goalDurationInput"));
        assertElementTextEquals("kilometers", driver.findElement(By.id("goalDurationUnits")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void editGoalNoChanges() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        driver.findElement(By.id("submitGoal")).click();
        //verify the goal is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoals(resultSet, 1, 1, 2, 5);
    }

    @Test
    public void editGoalChange() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        driver.findElement(By.id("goalSelection")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(2).click();
        driver.findElement(By.id("goalDurationInput")).clear();
        driver.findElement(By.id("goalDurationInput")).sendKeys("30");
        driver.findElement(By.id("goalDurationUnits")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(1).click();
        driver.findElement(By.id("submitGoal")).click();
        //verify the goal is changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE + ";");
        resultSet.next();
        assertGoals(resultSet, 1, 2, 1, 30);
    }
}
