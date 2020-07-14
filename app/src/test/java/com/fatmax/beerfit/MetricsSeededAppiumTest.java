package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class MetricsSeededAppiumTest extends AppiumTestBase {

    public static final By METRICS_BODY_TABLE = By.id("metricsBodyTable");
    public static final By TABLE_ROW = By.className("android.widget.TableRow");
    public static final By TEXT_VIEW = By.className("android.widget.TextView");
    public static final String WALKED_FOR_5_KILOMETERS = "Walked for 5.0 kilometers";
    public static final String RAN_FOR_5_KILOMETERS = "Ran for 5.0 kilometers";
    public static final String PLAYED_SOCCER_FOR_30_MINUTES = "Played Soccer for 30.0 minutes";
    public static final String LIFTED_FOR_30_MINUTES = "Lifted for 30.0 minutes";
    public static final String CYCLED_FOR_1_0_KILOMETER = "Cycled for 1.0 kilometer";

    @Before
    public void navigateToAddActivity() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,\"2020-01-01 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(2,\"2020-01-02 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(3,\"2020-01-03 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(4,\"2020-01-04 00:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(5,\"2020-01-05 00:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(6,\"2020-01-06 00:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(7,\"2020-01-07 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(8,\"2020-01-08 00:00\",3,2,1,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(9,\"2020-01-09 00:00\",0,0,1,-1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(10,\"2020-01-10 00:00\",0,0,1,-1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(11,\"2020-02-11 00:00\",4,1,30,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(12,\"2020-02-12 00:00\",5,1,30,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(13,\"2020-02-13 00:00\",0,0,2,-2);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(14,\"2020-02-14 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 00:00\",2,2,5,1);");
        new Navigate(driver).toMetrics();
    }

    @Test
    public void yearlyMetricsDisplayed() {
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertEquals(tableRows.size(), 6, "Expected to find '6' data rows", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("2020 (4 drank / 12 earned beers)", tableRows.get(0).findElement(TEXT_VIEW));
    }

    @Test
    public void yearlyMetricsAccurate() {
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, tableRows.get(1).findElement(TEXT_VIEW));
        assertElementTextEquals("Ran for 20.0 kilometers", tableRows.get(2).findElement(TEXT_VIEW));
        assertElementTextEquals("Walked for 25.0 kilometers", tableRows.get(3).findElement(TEXT_VIEW));
        assertElementTextEquals(PLAYED_SOCCER_FOR_30_MINUTES, tableRows.get(4).findElement(TEXT_VIEW));
        assertElementTextEquals(CYCLED_FOR_1_0_KILOMETER, tableRows.get(5).findElement(TEXT_VIEW));
    }

    @Test
    public void monthlyMetricsDisplayed() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertEquals(tableRows.size(), 9, "Expected to find '9' data rows", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("February 2020 (2 drank / 4 earned beers)", tableRows.get(0).findElement(TEXT_VIEW));
        assertElementTextEquals("January 2020 (2 drank / 8 earned beers)", tableRows.get(5).findElement(TEXT_VIEW));
    }

    @Test
    public void februaryMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(1).findElement(TEXT_VIEW));
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, tableRows.get(2).findElement(TEXT_VIEW));
        assertElementTextEquals(PLAYED_SOCCER_FOR_30_MINUTES, tableRows.get(3).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(4).findElement(TEXT_VIEW));
    }

    @Test
    public void januaryMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals("Ran for 15.0 kilometers", tableRows.get(6).findElement(TEXT_VIEW));
        assertElementTextEquals("Walked for 20.0 kilometers", tableRows.get(7).findElement(TEXT_VIEW));
        assertElementTextEquals(CYCLED_FOR_1_0_KILOMETER, tableRows.get(8).findElement(TEXT_VIEW));
    }

    @Test
    public void weeklyMetricsDisplayed() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertEquals(tableRows.size(), 12, "Expected to find '12' data rows", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Week 3, February 2020 (2 drank / 4 earned beers)", tableRows.get(0).findElement(TEXT_VIEW));
        assertElementTextEquals("Week 2, January 2020 (2 drank / 3 earned beers)", tableRows.get(5).findElement(TEXT_VIEW));
        assertElementTextEquals("Week 1, January 2020 (0 drank / 5 earned beers)", tableRows.get(9).findElement(TEXT_VIEW));
    }

    @Test
    public void week3FebruaryMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(1).findElement(TEXT_VIEW));
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, tableRows.get(2).findElement(TEXT_VIEW));
        assertElementTextEquals(PLAYED_SOCCER_FOR_30_MINUTES, tableRows.get(3).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(4).findElement(TEXT_VIEW));
    }

    @Test
    public void week2JanuaryMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(6).findElement(TEXT_VIEW));
        assertElementTextEquals(CYCLED_FOR_1_0_KILOMETER, tableRows.get(7).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(8).findElement(TEXT_VIEW));
    }

    @Test
    public void week1JanuaryMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals("Walked for 15.0 kilometers", tableRows.get(10).findElement(TEXT_VIEW));
        assertElementTextEquals("Ran for 10.0 kilometers", tableRows.get(11).findElement(TEXT_VIEW));
    }

    @Test
    public void dailyMetricsDisplayed() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertEquals(tableRows.size(), 23, "Expected to find '23' data rows", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Saturday, February 15 2020 (0 drank / 1 earned beers)", tableRows.get(0).findElement(TEXT_VIEW));
        assertElementTextEquals("Friday, February 14 2020 (0 drank / 1 earned beers)", tableRows.get(2).findElement(TEXT_VIEW));
        assertElementTextEquals("Thursday, February 13 2020 (2 drank / 0 earned beers)", tableRows.get(4).findElement(TEXT_VIEW));
        assertElementTextEquals("Wednesday, February 12 2020 (0 drank / 1 earned beers)", tableRows.get(5).findElement(TEXT_VIEW));
        assertElementTextEquals("Tuesday, February 11 2020 (0 drank / 1 earned beers)", tableRows.get(7).findElement(TEXT_VIEW));
        assertElementTextEquals("Friday, January 10 2020 (1 drank / 0 earned beers)", tableRows.get(9).findElement(TEXT_VIEW));
        assertElementTextEquals("Thursday, January 9 2020 (1 drank / 0 earned beers)", tableRows.get(10).findElement(TEXT_VIEW));
        assertElementTextEquals("Wednesday, January 8 2020 (0 drank / 1 earned beers)", tableRows.get(11).findElement(TEXT_VIEW));
        assertElementTextEquals("Tuesday, January 7 2020 (0 drank / 1 earned beers)", tableRows.get(13).findElement(TEXT_VIEW));
        assertElementTextEquals("Monday, January 6 2020 (0 drank / 1 earned beers)", tableRows.get(15).findElement(TEXT_VIEW));
        assertElementTextEquals("Sunday, January 5 2020 (0 drank / 1 earned beers)", tableRows.get(17).findElement(TEXT_VIEW));
        assertElementTextEquals("Saturday, January 4 2020 (0 drank / 1 earned beers)", tableRows.get(19).findElement(TEXT_VIEW));
        assertElementTextEquals("Friday, January 3 2020 (0 drank / 1 earned beers)", tableRows.get(21).findElement(TEXT_VIEW));
    }

    @Test
    public void dailyMetricsDisplayedScrolled() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        TouchAction action = new TouchAction((AndroidDriver) driver.getDriver());
        action.press(PointOption.point(100, 220)).moveTo(PointOption.point(100, 0)).release().perform();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertEquals(tableRows.size(), 24, "Expected to find '24' data rows", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Friday, January 3 2020 (0 drank / 1 earned beers)", tableRows.get(18).findElement(TEXT_VIEW));
        assertElementTextEquals("Thursday, January 2 2020 (0 drank / 1 earned beers)", tableRows.get(20).findElement(TEXT_VIEW));
        assertElementTextEquals("Wednesday, January 1 2020 (0 drank / 1 earned beers)", tableRows.get(22).findElement(TEXT_VIEW));
    }

    @Test
    public void dailyMetricsAccurate() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(1).findElement(TEXT_VIEW));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(3).findElement(TEXT_VIEW));
        assertElementTextEquals(PLAYED_SOCCER_FOR_30_MINUTES, tableRows.get(6).findElement(TEXT_VIEW));
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, tableRows.get(8).findElement(TEXT_VIEW));
        assertElementTextEquals(CYCLED_FOR_1_0_KILOMETER, tableRows.get(12).findElement(TEXT_VIEW));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(14).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(16).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(18).findElement(TEXT_VIEW));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, tableRows.get(20).findElement(TEXT_VIEW));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(22).findElement(TEXT_VIEW));
    }

    @Test
    public void dailyMetricsAccurateScrolled() {
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW).get(0).click();
        TouchAction action = new TouchAction((AndroidDriver) driver.getDriver());
        action.press(PointOption.point(100, 220)).moveTo(PointOption.point(100, 0)).release().perform();
        List<WebElement> tableRows = driver.findElement(METRICS_BODY_TABLE).findElements(TABLE_ROW);
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(19).findElement(TEXT_VIEW));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(21).findElement(TEXT_VIEW));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, tableRows.get(23).findElement(TEXT_VIEW));
    }

    // TODO - verify graph...
}
