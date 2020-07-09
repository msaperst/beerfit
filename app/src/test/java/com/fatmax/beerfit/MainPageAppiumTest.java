package com.fatmax.beerfit;

import com.fatmax.beerfit.objects.Navigate;
import com.testpros.fast.By;
import com.testpros.fast.WebElement;

import org.junit.Test;

import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class MainPageAppiumTest extends AppiumTestBase {

    private final By drankABeer = By.id("drankABeer");
    private final By beersLeft = By.id("beersLeft");


    @Test
    public void mainTitleExists() {
        assertElementTextEquals("BeerFit", By.className("android.widget.TextView"));
    }

    @Test
    public void freshStartBeers() {
        assertElementTextEquals("0 Beers Left", beersLeft);
    }

    @Test
    public void drinkABeer() {
        driver.findElement(drankABeer).click();
        assertElementTextEquals("-1 Beers Left", beersLeft);
    }

    @Test
    public void drinkThreeBeers() {
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        driver.findElement(drankABeer).click();
        assertElementTextEquals("-3 Beers Left", beersLeft);
    }

    @Test
    public void oneBeerIsSingular() {
        //kludge to have it redraw
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,\"2020-01-01 00:00\",1,2,5,2);");
        driver.findElement(drankABeer).click();
        assertElementTextEquals("1 Beer Left", beersLeft);
    }

    @Test
    public void twoBeersArePlural() {
        //kludge to have it redraw
        modifyDB("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,\"2020-01-01 00:00\",1,2,5,3);");
        driver.findElement(drankABeer).click();
        assertElementTextEquals("2 Beers Left", beersLeft);
    }

    @Test
    public void earnBeer() {
        driver.findElement(By.id("earnedABeer")).click();
        assertElementTextEquals("Add An Activity", By.className("android.widget.TextView"));
    }

    @Test
    public void checkMainMenu() {
        driver.findElement(By.AccessibilityId("Open")).click();
        assertElementDisplayed(By.id("nav_header_image"));
        assertElementTextEquals("BeerFit", By.id("nav_header_title"));
        // menu items
        List<WebElement> menuList = driver.findElements(By.className("android.widget.CheckedTextView"));
        assertEquals(menuList.size(), 3, "Expected to find '3' menu items", "Actually found '" + menuList.size() + "' items");
        assertElementTextEquals("Activities", menuList.get(0));
        assertElementTextEquals("Goals", menuList.get(1));
        assertElementTextEquals("Metrics", menuList.get(2));
    }

    @Test
    public void viewSite() {
        new Navigate(driver).toSite();
        assertElementTextEquals("https://beerfit.app", By.className("android.widget.EditText"));
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
