package com.fatmax.beerfit;

import com.testpros.fast.By;

import org.junit.Test;

public class MainActivityAppiumTest extends AppiumTestBase {

    private final By drankABeer = By.id("drankABeer");
    private final By beersLeft = By.id("beersLeft");


    @Test
    public void titleExists() {
        assertElementTextEquals("Welcome to BeerFit", By.id("welcomeHeader"));
    }

    @Test
    public void freshStartBeers() {
        assertElementTextEquals("0", beersLeft);
    }

    @Test
    public void drinkABeer() {
        driver.findElement(drankABeer).click();
        assertElementTextEquals("-1", beersLeft);
    }

    @Test
    public void drinkThreeBeers() {
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        assertElementTextEquals("-3", beersLeft);
    }

    @Test
    public void earnBeer() {
        driver.findElement(By.id("earnedABeer")).click();
        assertElementTextEquals("Add An Activity", By.id("addActivityHeader"));
    }

    @Test
    public void viewActivities() {
        driver.findElement(By.id("viewActivities")).click();
        assertElementTextEquals("BeerFit Activities", By.id("viewActivitiesTitle"));
    }

    @Test
    public void viewMetrics() {
        driver.findElement(By.id("viewMetrics")).click();
        assertElementDisplayed(By.id("metricsLayout"));
    }

    @Test
    public void viewGoals() {
        driver.findElement(By.id("viewGoals")).click();
        assertElementTextEquals("BeerFit Goals", By.id("viewGoalsTitle"));
    }
}
