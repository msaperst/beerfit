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

    @Test
    public void checkOptionsMenuExistsTest() {
        assertElementDisplayed(By.AccessibilityId("More options"));
    }

    @Test
    public void checkOptionsMenuValuesTest() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        assertEquals(menuOptions.size(), 2, "Expected to find '2' menu items", "Actually found '" + menuOptions.size() + "' menu items");
        assertElementTextEquals("Edit Exercises", menuOptions.get(0));
        assertElementTextEquals("Edit Measurements", menuOptions.get(1));
    }
}
