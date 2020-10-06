package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MetricsAppiumTest extends AppiumTestBase {

    @Override
    public String getStartingActivity() {
        return "MetricsActivity";
    }

    @Test
    public void metricsTitleExists() {
        assertElementTextEquals("BeerFit Metrics", By.className("android.widget.TextView"));
    }

    @Test
    public void metricsTableExists() {
        assertElementDisplayed(By.id("metricsBodyTable"));
    }

    @Test
    public void metricsGraphExists() {
        assertElementDisplayed(By.id("metricsGraph"));
    }

    @Test
    public void metricsTableEmpty() {
        List<WebElement> tableRows = driver.findElement(By.id("metricsBodyTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' row", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("No Data Present", tableRows.get(0).findElement(By.className("android.widget.TextView")));
    }
}
