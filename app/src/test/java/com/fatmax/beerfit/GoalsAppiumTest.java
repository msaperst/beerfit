package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GoalsAppiumTest extends AppiumTestBase {

    @Before
    public void navigateToGoals() {
        new Navigate(driver).toGoals();
    }

    @Test
    public void goalsTitleExists() {
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }

    @Test
    public void goalsHeaderExists() {
        assertElementTextEquals("To Earn A Beer…", By.id("viewGoalsSubtitle"));
    }

    @Test
    public void addGoalButtonExists() {
        assertElementDisplayed(By.id("addGoalButton"));
    }

    @Test
    public void noGoalsInTable() {
        List<WebElement> tableRows = driver.findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' goals", "Actually found '" + tableRows.size() + "'");
    }
}