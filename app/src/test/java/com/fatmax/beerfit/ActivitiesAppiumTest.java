package com.fatmax.beerfit;

import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Test;

import java.util.List;

public class ActivitiesAppiumTest extends AppiumTestBase {

    @Override
    public String getStartingActivity() {
        return "ActivitiesActivity";
    }

    @Test
    public void activityTitleExists() {
        assertElementTextEquals("BeerFit Activities", By.className("android.widget.TextView"));
    }

    @Test
    public void activityActivityTableExists() {
        assertElementDisplayed(By.id("activitiesTable"));
    }

    @Test
    public void noActivitiesInTable() {
        List<WebElement> tableRows = driver.findElement(By.id("activitiesTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' activities", "Actually found '" + tableRows.size() + "'");
    }
}
