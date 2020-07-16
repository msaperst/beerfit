package com.fatmax.beerfit;

import android.graphics.Color;

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
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;

public class ExerciseModifyAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToGoals() {
        new Navigate(driver).toEditExercise();
    }

    @Test
    public void checkModifyExerciseTitle() {
        assertElementTextEquals("Select Exercise to Modify", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkModifyExerciseOptions() {
        List<WebElement> exerciseOptions = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(exerciseOptions.size(), 5, "Expected to find '5' exercise items", "Actually found '" + exerciseOptions.size() + "'");
        assertElementTextEquals("Walk", exerciseOptions.get(0));
        assertElementTextEquals("Run", exerciseOptions.get(1));
        assertElementTextEquals("Cycle", exerciseOptions.get(2));
        assertElementTextEquals("Lift", exerciseOptions.get(3));
        assertElementTextEquals("Play Soccer", exerciseOptions.get(4));
    }

    @Test
    public void checkModifyExerciseButtons() {
        List<WebElement> exerciseButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(exerciseButtons.size(), 3, "Expected to find '3' edit exercise button", "Actually found '" + exerciseButtons.size() + "'");
    }

    @Test
    public void checkModifyExerciseAddNewButton() {
        assertElementTextEquals("ADD NEW", By.id("android:id/button3"));
        assertElementEnabled(By.id("android:id/button3"));
    }

    @Test
    public void checkModifyExerciseEditButton() {
        assertElementTextEquals("EDIT", By.id("android:id/button2"));
        assertElementDisabled(By.id("android:id/button2"));
    }

    @Test
    public void checkModifyExerciseDeleteButton() {
        assertElementTextEquals("DELETE", By.id("android:id/button1"));
        assertElementDisabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyExerciseButtonsEnabled() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        assertElementEnabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkAddExerciseTitle() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkAddExerciseDefaultValues() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("Exercise Description (Run)", By.id("editExerciseName"));
        assertElementTextEquals("Past Tense Exercise Description (Ran)", By.id("editExercisePastName"));
        assertElementTextEquals("Exercise Color", By.id("editExerciseColor"));
        //TODO - can't check the actual color
    }

    @Test
    public void checkAddExerciseButtons() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("CANCEL", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
        assertElementTextEquals("SAVE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkAddExerciseCancelDoesNothing() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void ableToChooseColor() {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseColor")).click();
        driver.findElements(By.className("android.view.View")).get(0).click();
        driver.findElements(By.className("android.view.View")).get(1).click();
        driver.findElements(By.className("android.view.View")).get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        //TODO - need to manually verify this, can't be automated
    }

    @Test
    public void checkAddExerciseSaveNoPastErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveNoCurrentErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveDuplicatePastErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Walked");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveDuplicateCurrentErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Walk");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveDuplicateColorErrors() throws SQLException, IOException, ClassNotFoundException {
        modifyDB("INSERT INTO " + EXERCISES_TABLE + " VALUES(6,\"Wulked\",\"Wulk\",-2139062144);");
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("editExerciseColor")).click();
        driver.findElements(By.className("android.view.View")).get(0).click();
        driver.findElements(By.className("android.view.View")).get(1).click();
        driver.findElements(By.className("android.view.View")).get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Another Exercise Already Has This Color", By.id("editExerciseColor"));
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        resultSet.next();
        assertExercise(resultSet, 6, "Wulked", "Wulk", -2139062144);
        assertEquals(false, resultSet.next(), "Expected to find '6' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSaveDuplicateErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Walk");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Walked");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkAddExerciseSave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("editExerciseColor")).click();
        driver.findElements(By.className("android.view.View")).get(0).click();
        driver.findElements(By.className("android.view.View")).get(1).click();
        driver.findElements(By.className("android.view.View")).get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        assertExercise(resultSet, 6, "Cardioed", "Cardio", -2139062144);
        assertEquals(false, resultSet.next(), "Expected to find '6' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseTitle() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkEditExerciseContent() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("Walk", By.id("editExerciseName"));
        assertElementTextEquals("Walked", By.id("editExercisePastName"));
        assertElementTextEquals("Exercise Color", By.id("editExerciseColor"));
        //TODO - can't check the actual color
    }

    @Test
    public void checkEditExerciseButtons() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("CANCEL", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
        assertElementTextEquals("SAVE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkEditExerciseCancelDoesNothing() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseReSave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseModifySave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("editExerciseColor")).click();
        driver.findElements(By.className("android.view.View")).get(0).click();
        driver.findElements(By.className("android.view.View")).get(1).click();
        driver.findElements(By.className("android.view.View")).get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Cardioed", "Cardio", -2139062144);
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveNoPastErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).clear();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveNoCurrentErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).clear();
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).clear();
        driver.findElement(By.id("editExercisePastName")).clear();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveDuplicatePastErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Ran");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveDuplicateCurrentErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Run");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveDuplicateColorErrors() throws SQLException, IOException, ClassNotFoundException {
        modifyDB("INSERT INTO " + EXERCISES_TABLE + " VALUES(6,\"Wulked\",\"Wulk\",-2139062144);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Cardio");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Cardioed");
        driver.findElement(By.id("editExerciseColor")).click();
        driver.findElements(By.className("android.view.View")).get(0).click();
        driver.findElements(By.className("android.view.View")).get(1).click();
        driver.findElements(By.className("android.view.View")).get(2).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Another Exercise Already Has This Color", By.id("editExerciseColor"));
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        resultSet.next();
        assertExercise(resultSet, 6, "Wulked", "Wulk", -2139062144);
        assertEquals(false, resultSet.next(), "Expected to find '6' exercises", "There are more exercises present");
    }

    @Test
    public void checkEditExerciseSaveDuplicateErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editExerciseName")).sendKeys("Run");
        driver.findElement(By.id("editExercisePastName")).sendKeys("Ran");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Exercise", By.id("android:id/alertTitle"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkDeleteExerciseTitle() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Delete Exercise", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkDeleteExerciseContent() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Do you really want to delete the exercise 'Walk'?", By.id("android:id/message"));
    }

    @Test
    public void checkDeleteExerciseIcon() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementDisplayed(By.id("android:id/icon"));
    }

    @Test
    public void checkDeleteExerciseButtons() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        List<WebElement> exerciseButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(exerciseButtons.size(), 2, "Expected to find '2' delete exercise buttons", "Actually found '" + exerciseButtons.size() + "'");
    }

    @Test
    public void checkDeleteExerciseButtonCancel() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void checkDeleteExerciseButtonOk() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkDeleteExerciseCancelCancels() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }

    @Test
    public void checkDeleteExerciseOkDeletes() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '4' exercises", "There are more exercises present");
    }

    @Test
    public void checkDeleteExerciseUnsafeTitle() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Delete Exercise", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkDeleteExerciseUnsafeContent() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Unable to delete this exercise, as you have goals and/or activities utilizing this exercise!", By.id("android:id/message"));
    }

    @Test
    public void checkDeleteExerciseUnsafeIcon() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementDisplayed(By.id("android:id/icon"));
    }

    @Test
    public void checkDeleteExerciseUnsafeButtons() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button1")).click();
        List<WebElement> exerciseButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(exerciseButtons.size(), 0, "Expected to find no delete exercise buttons", "Actually found '" + exerciseButtons.size() + "'");
    }

    @Test
    public void checkDeleteExerciseUnsafeDoesntDelete() throws SQLException, IOException, ClassNotFoundException {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the exercises are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + EXERCISES_TABLE);
        resultSet.next();
        assertExercise(resultSet, 1, "Walked", "Walk", Color.GREEN);
        resultSet.next();
        assertExercise(resultSet, 2, "Ran", "Run", Color.BLUE);
        resultSet.next();
        assertExercise(resultSet, 3, "Cycled", "Cycle", Color.RED);
        resultSet.next();
        assertExercise(resultSet, 4, "Lifted", "Lift", Color.MAGENTA);
        resultSet.next();
        assertExercise(resultSet, 5, "Played Soccer", "Play Soccer", Color.DKGRAY);
        assertEquals(false, resultSet.next(), "Expected to find '5' exercises", "There are more exercises present");
    }
}
