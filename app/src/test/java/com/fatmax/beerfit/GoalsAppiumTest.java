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
        new Navigate(drivers.get()).toGoals();
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
    public void noGoalsInTable() {
        List<WebElement> tableRows = drivers.get().findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 0, "Expected to find '0' goals", "Actually found '" + tableRows.size() + "'");
    }

    @Test
    public void checkOptionsMenuExistsTest() {
        assertElementDisplayed(By.AccessibilityId("More options"));
    }

    @Test
    public void checkOptionsMenuValuesTest() {
        drivers.get().findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = drivers.get().findElements(By.className("android.widget.TextView"));
        assertEquals(menuOptions.size(), 3, "Expected to find '3' menu items", "Actually found '" + menuOptions.size() + "' menu items");
        assertElementTextEquals("Add A Goal", menuOptions.get(0));
        assertElementTextEquals("Edit Exercises", menuOptions.get(1));
        assertElementTextEquals("Edit Measurements", menuOptions.get(2));
    }
}
