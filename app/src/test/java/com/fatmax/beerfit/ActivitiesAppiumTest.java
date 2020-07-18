package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ActivitiesAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToActivity() {
        new Navigate(drivers.get()).toActivities();
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
        List<WebElement> tableRows = drivers.get().findElement(By.id("activitiesTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' activities", "Actually found '" + tableRows.size() + "'");
    }
}
