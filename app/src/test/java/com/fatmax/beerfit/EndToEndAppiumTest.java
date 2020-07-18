package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Test;

import java.util.List;

public class EndToEndAppiumTest extends AppiumTestBase {

    public static final By BEERS_LEFT = By.id("beersLeft");

    @Test
    public void addGoalAddActivityDrinkBeer() {
        // add a goal, go for a run, then drink a beer
        // ensure beers on main page is properly displayed
        Navigate navigate = new Navigate(drivers.get());
        // first, we add the goal of 5 km run for one beer
        navigate.toNewGoal();
        drivers.get().findElement(By.id("goalExercise")).click();
        List<WebElement> activityList = drivers.get().findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(2).click();
        drivers.get().findElement(By.id("goalAmount")).sendKeys("5");
        drivers.get().findElement(By.id("goalMeasurement")).click();
        List<WebElement> durationList = drivers.get().findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(4).click();
        drivers.get().findElement(By.id("android:id/button1")).click();
        //verify the goal is displayed there
        List<WebElement> tableRows = drivers.get().findElement(By.id("goalsTable")).findElements(By.className("android.widget.TableRow"));
        assertEquals(tableRows.size(), 1, "Expected to find '1' goals", "Actually found '" + tableRows.size() + "'");
        assertElementTextEquals("Run for 5.0 kilometers", tableRows.get(0).findElement(By.className("android.widget.TextView")));
        // log our activity
        navigate.goBack();
        assertElementTextEquals("0 Beers Left", BEERS_LEFT);
        drivers.get().findElement(By.id("earnedABeer")).click();
        drivers.get().findElement(By.id("activityExercise")).click();
        activityList = drivers.get().findElements(By.className("android.widget.CheckedTextView"));
        activityList.get(2).click();
        drivers.get().findElement(By.id("activityAmount")).sendKeys("10");
        drivers.get().findElement(By.id("activityMeasurement")).click();
        durationList = drivers.get().findElements(By.className("android.widget.CheckedTextView"));
        durationList.get(4).click();
        drivers.get().findElement(By.id("android:id/button1")).click();
        //verify the run counted for beers
        assertElementTextEquals("2 Beers Left", BEERS_LEFT);
        // drink our beer
        drivers.get().findElement(By.id("drankABeer")).click();
        //verify we drank the beer
        assertElementTextEquals("1 Beer Left", BEERS_LEFT);
        // TODO - verify activities list
        // TODO - verify metrics list

    }

    // TODO - add usecase for adding new exercise, new measurement,
    //      then new goal and activity with that, then check beers
}
