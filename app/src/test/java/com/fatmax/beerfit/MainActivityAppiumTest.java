package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;

import org.junit.Test;

public class MainActivityAppiumTest extends AppiumTestBase {

    private final By drankABeer = By.id("drankABeer");
    private final By beersLeft = By.id("beersLeft");


    @Test
    public void titleExists() {
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
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
        assertElementTextEquals("Add An Activity", By.className("android.widget.TextView"));
    }

    @Test
    public void viewActivities() {
        new Navigate(driver).toActivities();
        assertElementTextEquals("BeerFit Activities", By.className("android.widget.TextView"));
    }

    @Test
    public void viewMetrics() {
        new Navigate(driver).toMetrics();
        assertElementTextEquals("BeerFit Metrics", By.className("android.widget.TextView"));
    }

    @Test
    public void viewGoals() {
        new Navigate(driver).toGoals();
        assertElementTextEquals("BeerFit Goals", By.className("android.widget.TextView"));
    }
}
