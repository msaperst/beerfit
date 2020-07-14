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
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class MeasurementModifyAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToGoals() {
        new Navigate(driver).toGoals();
        driver.findElement(By.AccessibilityId("More options")).click();
        driver.findElements(By.className("android.widget.TextView")).get(1).click();
    }

    @Test
    public void checkModifyMeasurementTitle() {
        assertElementTextEquals("Select Measurement to Modify", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkModifyMeasurementOptions() {
        List<WebElement> measurementOptions = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(measurementOptions.size(), 7, "Expected to find '7' measurement items", "Actually found '" + measurementOptions.size() + "'");
        assertElementTextEquals("class", measurementOptions.get(0));
        assertElementTextEquals("repetition", measurementOptions.get(1));
        assertElementTextEquals("mile", measurementOptions.get(2));
        assertElementTextEquals("kilometer", measurementOptions.get(3));
        assertElementTextEquals("hour", measurementOptions.get(4));
        assertElementTextEquals("minute", measurementOptions.get(5));
        assertElementTextEquals("second", measurementOptions.get(6));

    }

    @Test
    public void checkModifyMeasurementButtons() {
        List<WebElement> measurementButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(measurementButtons.size(), 3, "Expected to find '3' edit measurement buttons", "Actually found '" + measurementButtons.size() + "'");
    }

    @Test
    public void checkModifyMeasurementAddNewButton() {
        assertElementTextEquals("ADD NEW", By.id("android:id/button3"));
        assertElementEnabled(By.id("android:id/button3"));
    }

    @Test
    public void checkModifyMeasurementEditButton() {
        assertElementTextEquals("EDIT", By.id("android:id/button2"));
        assertElementDisabled(By.id("android:id/button2"));
    }

    @Test
    public void checkModifyMeasurementDeleteButton() {
        assertElementTextEquals("DELETE", By.id("android:id/button1"));
        assertElementDisabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledOne() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        assertElementEnabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledTwo() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        assertElementEnabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledThree() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(2).click();
        assertElementDisabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledFour() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(3).click();
        assertElementDisabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledFive() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(4).click();
        assertElementDisabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledSix() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(5).click();
        assertElementDisabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkModifyMeasurementButtonsEnabledSeven() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(6).click();
        assertElementDisabled(By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkAddMeasurementTitle() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("Add New Measurement", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkAddMeasurementDefaultValues() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("Measurement Name", By.id("editMeasurementName"));
    }

    @Test
    public void checkAddMeasurementButtons() {
        driver.findElement(By.id("android:id/button3")).click();
        assertElementTextEquals("CANCEL", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
        assertElementTextEquals("SAVE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkAddMeasurementCancelDoesNothing() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkAddMeasurementSaveErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Measurement", By.id("android:id/alertTitle"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkAddMeasurementSaveDuplicateErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editMeasurementName")).sendKeys("kilometer");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Add New Measurement", By.id("android:id/alertTitle"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkAddMeasurementSave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.id("android:id/button3")).click();
        driver.findElement(By.id("editMeasurementName")).sendKeys("Jazzy");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        assertMeasurement(resultSet, 8, null, "Jazzy", -1);
        assertEquals(false, resultSet.next(), "Expected to find '8' measurements", "There are more measurements present");
    }

    @Test
    public void checkEditMeasurementTitle() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("Edit Measurement", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkEditMeasurementContent() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("class", By.id("editMeasurementName"));
    }

    @Test
    public void checkEditMeasurementButtons() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("CANCEL", By.id("android:id/button2"));
        assertElementEnabled(By.id("android:id/button2"));
        assertElementTextEquals("SAVE", By.id("android:id/button1"));
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkEditMeasurementCancelDoesNothing() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkEditMeasurementReSave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(1).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkEditMeasurementModifySave() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editMeasurementName")).sendKeys("Jazzy");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "Jazzy", -1);
        resultSet.next();
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkEditMeasurementSaveErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editMeasurementName")).clear();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Measurement", By.id("android:id/alertTitle"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkEditMeasurementSaveDuplicateErrors() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button2")).click();
        driver.findElement(By.id("editMeasurementName")).sendKeys("hour");
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Edit Measurement", By.id("android:id/alertTitle"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkDeleteMeasurementTitle() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Delete Measurement", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkDeleteMeasurementContent() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Do you really want to delete the measurement 'class'?", By.id("android:id/message"));
    }

    @Test
    public void checkDeleteMeasurementIcon() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementDisplayed(By.id("android:id/icon"));
    }

    @Test
    public void checkDeleteMeasurementButtons() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        List<WebElement> measurementButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(measurementButtons.size(), 2, "Expected to find '2' delete measurement buttons", "Actually found '" + measurementButtons.size() + "'");
    }

    @Test
    public void checkDeleteMeasurementButtonCancel() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button2"));
    }

    @Test
    public void checkDeleteMeasurementButtonOk() {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementEnabled(By.id("android:id/button1"));
    }

    @Test
    public void checkDeleteMeasurementCancelCancels() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button2")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }

    @Test
    public void checkDeleteMeasurementOkDeletes() throws SQLException, IOException, ClassNotFoundException {
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '6' measurements", "There are more measurements present");
    }

    @Test
    public void checkDeleteMeasurementUnsafeTitle() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,6,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Delete Measurement", By.id("android:id/alertTitle"));
    }

    @Test
    public void checkDeleteMeasurementUnsafeContent() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,6,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementTextEquals("Unable to delete this measurement, as you have goals and/or activities utilizing this measurement!", By.id("android:id/message"));
    }

    @Test
    public void checkDeleteMeasurementUnsafeIcon() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,6,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        assertElementDisplayed(By.id("android:id/icon"));
    }

    @Test
    public void checkDeleteMeasurementUnsafeButtons() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,6,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        List<WebElement> measurementButtons = driver.findElements(By.className("android.widget.Button"));
        assertEquals(measurementButtons.size(), 0, "Expected to find no delete measurement buttons", "Actually found '" + measurementButtons.size() + "'");
    }

    @Test
    public void checkDeleteMeasurementUnsafeDoesntDelete() throws SQLException, IOException, ClassNotFoundException {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,6,5,1);");
        driver.findElements(By.className("android.widget.CheckedTextView")).get(0).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the measurements are not changed
        ResultSet resultSet = queryDB("SELECT * FROM " + MEASUREMENTS_TABLE);
        resultSet.next();
        assertMeasurement(resultSet, 1, "time", "minute", 60);
        resultSet.next();
        assertMeasurement(resultSet, 2, "distance", "kilometer", 1);
        resultSet.next();
        assertMeasurement(resultSet, 3, "time", "second", 3600);
        resultSet.next();
        assertMeasurement(resultSet, 4, "time", "hour", 1);
        resultSet.next();
        assertMeasurement(resultSet, 5, "distance", "mile", 0.6213712);
        resultSet.next();
        assertMeasurement(resultSet, 6, null, "class", -1);
        resultSet.next();
        assertMeasurement(resultSet, 7, null, "repetition", -1);
        assertEquals(false, resultSet.next(), "Expected to find '7' measurements", "There are more measurements present");
    }
}
