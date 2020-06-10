package com.fatmax.beerfit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainActivityAppiumTest extends AppiumTestBase {

    private final By drankABeer = By.id("drankABeer");


    @Test
    public void titleExists() {
        assertEquals("Welcome to BeerFit", driver.findElement(By.id("welcomeHeader")).getText());
    }

    @Test
    public void freshStartBeers() {
        assertEquals(0, Integer.parseInt(driver.findElement(By.id("beersLeft")).getText()));
    }

    @Test
    public void drinkABeer() {
        driver.findElement(drankABeer).click();
        assertEquals( - 1, Integer.parseInt(driver.findElement(By.id("beersLeft")).getText()));
    }

    @Test
    public void drinkThreeBeers() {
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        assertEquals( - 3, Integer.parseInt(driver.findElement(By.id("beersLeft")).getText()));
    }

    @Test
    public void earnBeer() {
        driver.findElement(By.id("earnedABeer")).click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("addActivityHeader")));
        assertEquals("Add An Activity", driver.findElement(By.id("addActivityHeader")).getText());
    }

    @Test
    public void viewActivities() {
        driver.findElement(By.id("viewActivities")).click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("viewActivitiesTitle")));
        assertEquals("BeerFit Activities", driver.findElement(By.id("viewActivitiesTitle")).getText());
    }

    @Test
    public void viewMetrics() {
        driver.findElement(By.id("viewMetrics")).click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("metricsLayout")));
        assertTrue(driver.findElement(By.id("metricsLayout")).isDisplayed());
    }

    @Test
    public void viewGoals() {
        driver.findElement(By.id("viewGoals")).click();
        assertEquals("BeerFit Goals", driver.findElement(By.id("viewGoalsTitle")).getText());
    }
}
