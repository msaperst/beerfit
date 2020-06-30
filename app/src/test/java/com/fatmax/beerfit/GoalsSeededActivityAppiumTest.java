package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

public class GoalsSeededActivityAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToAddActivity() {
        modifyDB("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        new Navigate(driver).toGoals();
    }


    @Test
    public void addedGoalsDisplayed() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 5 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
    }

    @Test
    public void dontDeleteGoal() {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the goal is still there
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
    }

    @Test
    public void deleteGoal() {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the goal is gone
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' goals", "Actually found '" + tableRows.size() + "'");
    }

    @Test
    public void editGoalGoBack() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        driver.findElement(By.AccessibilityId("Navigate up")).click();
        //verify the goal is not changed
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 5 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
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
    public void editGoalNoChanges() {
        driver.findElement(By.AccessibilityId("Edit Activity")).click();
        driver.findElement(By.id("submitGoal")).click();
        //verify the goal is not changed
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 5 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
    }

    @Test
    public void editGoalChange() {
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
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Run for 30 minutes", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Edit Activity")));
        assertElementDisplayed(tableRows.get(0).findElement(By.AccessibilityId("Delete Activity")));
    }
}
