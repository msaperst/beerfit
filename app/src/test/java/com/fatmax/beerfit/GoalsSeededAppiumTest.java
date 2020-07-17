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

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

public class GoalsSeededAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToGoals() {
        modifyDB("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        modifyDB("INSERT INTO " + GOALS_TABLE + " VALUES(2,2,1,1);");
        new Navigate(driver).toGoals();
    }

    @Test
    public void addedGoalsDisplayed() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 2, "Expected to find '2' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Walk for 5.0 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("Run for 1.0 minute", tableRows.get(1).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void dontDeleteGoal() throws SQLException, IOException, ClassNotFoundException {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        TouchAction action = new TouchAction((AndroidDriver) driver.getDriver());
        action.press(PointOption.point(100, 100)).release().perform();
        //verify the goal is still there
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 1, 1, 2, 5);
    }

    @Test
    public void checkDeleteGoalTitle() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Delete Goal", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkDeleteGoalContent() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Do you really want to delete the goal 'Walk for 5.0 kilometers'?", By.id("android:id/message"));
    }

    @Test
    public void checkDeleteGoalIcon() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementDisplayed(By.id("android:id/icon"));
    }

    @Test
    public void checkDeleteGoalButtons() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        List<WebElement> measurementButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(measurementButtons.size(), 2, "Expected to find '2' delete measurement buttons", "Actually found '" + measurementButtons.size() + "'");
    }

    @Test
    public void checkDeleteGoalButtonCancel() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void checkDeleteGoalButtonOk() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkDeleteGoalCancelCancels() throws SQLException, IOException, ClassNotFoundException {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 1, 1, 2, 5);
        resultSet.next();
        assertGoal(resultSet, 2, 2, 1, 1);
        assertEquals(false, resultSet.next(), "Expected to find '2' goals", "There are more goals present");
    }

    @Test
    public void checkDeleteGoalOkDeletes() throws SQLException, IOException, ClassNotFoundException {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 2, 2, 1, 1);
        assertEquals(false, resultSet.next(), "Expected to find '1' goal", "There are more goals present");
    }

    @Test
    public void viewGoalTitle() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        assertElementTextEquals("To Earn A Beer…", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkViewGoalButtons() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        List<WebElement> addGoalButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(addGoalButtons.size(), 2, "Expected to find '2' modify goal buttons", "Actually found '" + addGoalButtons.size() + "'");
    }

    @Test
    public void checkViewGoalDeleteGoalButton() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        assertElementTextEquals("DELETE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkViewGoalUpdateGoalButton() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        assertElementTextEquals("UPDATE", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void viewGoalAllData() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        assertElementTextEquals("Walk", driver.findElement(By.id("goalExercise")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("5.0", By.id("goalAmount"));
        assertElementTextEquals("kilometer", driver.findElement(By.id("goalMeasurement")).findElement(By.className("android.widget.TextView")));
    }

    @Test
    public void viewGoalNoChanges() throws SQLException, IOException, ClassNotFoundException {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the goal is not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 1, 1, 2, 5);
    }

    @Test
    public void viewGoalUpdate() throws SQLException, IOException, ClassNotFoundException {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("goalExercise")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(2).click();
        driver.findElement(By.id("goalAmount")).clear();
        driver.findElement(By.id("goalAmount")).sendKeys("30");
        driver.findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(1).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the goal is changed
        ResultSet resultSet = queryDB("SELECT * FROM " + GOALS_TABLE);
        resultSet.next();
        assertGoal(resultSet, 1, 2, 6, 30);
    }

    @Test
    public void viewGoalEmptyUpdate() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        tableRows.get(0).click();
        driver.findElement(By.id("goalExercise")).click();
        List<WebElement> activityList = driver.findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(0).click();
        driver.findElement(By.id("goalAmount")).clear();
        driver.findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = driver.findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the goal errors are shown
        assertElementTextEquals("You need to indicate some exercise", driver.findElement(By.id("goalExercise")).findElement(By.className("android.widget.TextView")));
        assertElementTextEquals("", By.id("goalAmount"));
        assertElementTextEquals("", driver.findElement(By.id("goalMeasurement")).findElement(By.className("android.widget.TextView")));
    }
}
