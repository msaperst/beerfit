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

import static com.fatmax.beerfit.MetricsSeededAppiumTest.CYCLED_FOR_1_0_KILOMETER;
import static com.fatmax.beerfit.MetricsSeededAppiumTest.LIFTED_FOR_30_MINUTES;
import static com.fatmax.beerfit.MetricsSeededAppiumTest.PLAYED_SOCCER_FOR_30_MINUTES;
import static com.fatmax.beerfit.MetricsSeededAppiumTest.RAN_FOR_5_KILOMETERS;
import static com.fatmax.beerfit.MetricsSeededAppiumTest.WALKED_FOR_5_KILOMETERS;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class ActivitiesSeededAppiumTest extends AppiumTestBase {

    @Before
    public void seedAndNavigateToActivities() {
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,\"2020-01-01 00:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(2,\"2020-01-02 01:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(3,\"2020-01-03 02:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(4,\"2020-01-04 03:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(5,\"2020-01-05 04:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(6,\"2020-01-06 05:00\",2,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(7,\"2020-01-07 06:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(8,\"2020-01-08 07:00\",3,2,1,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(9,\"2020-01-09 08:00\",0,0,1,-1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(10,\"2020-01-10 09:00\",0,0,1,-1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(11,\"2020-02-11 10:00\",4,1,30,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(12,\"2020-02-12 11:00\",5,1,30,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(13,\"2020-02-13 12:00\",0,0,2,-2);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(14,\"2020-02-14 13:00\",1,2,5,1);");
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(15,\"2020-02-15 23:59\",2,2,5,1);");
        new Navigate(driver).toActivities();
    }

    private WebElement getTime(List<WebElement> tableRows, int number) {
        List<WebElement> textViews = tableRows.get(number).findElements(By.className("android.widget.TextView"));
        return textViews.get(0);
    }

    private WebElement getActivity(List<WebElement> tableRows, int rowNumber) {
        List<WebElement> textViews = tableRows.get(rowNumber).findElements(By.className("android.widget.TextView"));
        return textViews.get(1);
    }

    @Test
    public void addedActivityDisplayedProperly() {
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertElementTextEquals("Sat, Feb 15 2020, 23:59", getTime(tableRows, 0));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getActivity(tableRows, 0));
    }

    @Test
    public void addedActivitiesDisplayed() {
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 14, "Expected to find '14' activities", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Sat, Feb 15 2020, 23:59", getTime(tableRows, 0));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getActivity(tableRows, 0));
        assertElementTextEquals("Fri, Feb 14 2020, 13:00", getTime(tableRows, 1));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 1));
        assertElementTextEquals("Thu, Feb 13 2020, 12:00", getTime(tableRows, 2));
        assertElementTextEquals("Drank 2 beers", getActivity(tableRows, 2));
        assertElementTextEquals("Wed, Feb 12 2020, 11:00", getTime(tableRows, 3));
        assertElementTextEquals(PLAYED_SOCCER_FOR_30_MINUTES, getActivity(tableRows, 3));
        assertElementTextEquals("Tue, Feb 11 2020, 10:00", getTime(tableRows, 4));
        assertElementTextEquals(LIFTED_FOR_30_MINUTES, getActivity(tableRows, 4));
        assertElementTextEquals("Fri, Jan 10 2020, 09:00", getTime(tableRows, 5));
        assertElementTextEquals("Drank 1 beer", getActivity(tableRows, 5));
        assertElementTextEquals("Thu, Jan 9 2020, 08:00", getTime(tableRows, 6));
        assertElementTextEquals("Drank 1 beer", getActivity(tableRows, 6));
        assertElementTextEquals("Wed, Jan 8 2020, 07:00", getTime(tableRows, 7));
        assertElementTextEquals(CYCLED_FOR_1_0_KILOMETER, getActivity(tableRows, 7));
        assertElementTextEquals("Tue, Jan 7 2020, 06:00", getTime(tableRows, 8));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 8));
        assertElementTextEquals("Mon, Jan 6 2020, 05:00", getTime(tableRows, 9));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getActivity(tableRows, 9));
        assertElementTextEquals("Sun, Jan 5 2020, 04:00", getTime(tableRows, 10));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getActivity(tableRows, 10));
        assertElementTextEquals("Sat, Jan 4 2020, 03:00", getTime(tableRows, 11));
        assertElementTextEquals(RAN_FOR_5_KILOMETERS, getActivity(tableRows, 11));
        assertElementTextEquals("Fri, Jan 3 2020, 02:00", getTime(tableRows, 12));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 12));
        assertElementTextEquals("Thu, Jan 2 2020, 01:00", getTime(tableRows, 13));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 13));
    }

    @Test
    public void addedActivitiesScrolledDisplayed() {
        TouchAction action = new TouchAction((AndroidDriver) driver.getDriver());
        action.press(PointOption.point(100, 500)).moveTo(PointOption.point(100, 100)).release().perform();
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 14, "Expected to find '14' activities", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Fri, Jan 3 2020, 02:00", getTime(tableRows, 11));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 11));
        assertElementTextEquals("Thu, Jan 2 2020, 01:00", getTime(tableRows, 12));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 12));
        assertElementTextEquals("Wed, Jan 1 2020, 00:00", getTime(tableRows, 13));
        assertElementTextEquals(WALKED_FOR_5_KILOMETERS, getActivity(tableRows, 13));
    }

    @Test
    public void dontDeleteActivity() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button2")).click();
        //verify the activity is still there
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        resultSet.next();
        assertActivity(resultSet, 15, "2020-02-15 23:59", 2, 2, 5, 1);
    }

    @Test
    public void deleteActivity() throws SQLException, IOException, ClassNotFoundException {
        driver.findElement(By.AccessibilityId("Delete Activity")).click();
        driver.findElement(By.id("android:id/button1")).click();
        //verify the activity is gone
        ResultSet resultSet = queryDB("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = '15';");
        assertEquals(false, resultSet.next(), "Expected no results", "");
    }
}
