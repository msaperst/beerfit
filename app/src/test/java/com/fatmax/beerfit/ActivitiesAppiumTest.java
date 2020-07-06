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
        new Navigate(driver).toActivities();
    }

    @Test
    public void activityTitleExists() {
        assertElementTextEquals("BeerFit Activities", By.className("android.widget.TextView"));
    }

    @Test
    public void activityActivityTableExists() {
        assertElementDisplayed(By.id("activityBodyTable"));
    }

    @Test
    public void noActivitiesInTable() {
        List<WebElement> tableRows = driver.findElement(By.id("activityBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' activities", "Actually found '" + tableRows.size() + "'");
    }
}
