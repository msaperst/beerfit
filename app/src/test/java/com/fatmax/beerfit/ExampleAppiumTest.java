package com.fatmax.beerfit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import static org.junit.Assert.assertEquals;

public class ExampleAppiumTest {
    private AndroidDriver driver;
    private AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder().usingAnyFreePort());

    @Before
    public void setupDriver() {
        service.start();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 60);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        capabilities.setCapability("appPackage", "com.fatmax.beerfit");
        capabilities.setCapability("appActivity", "MainActivity");
        driver = new AndroidDriver(service, capabilities);
    }

    @After
    public void tearDownDriver() {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        driver.quit();
        service.stop();
    }

    @Test
    public void drinkABeer() {
        int beers = Integer.parseInt(driver.findElement(By.id("beersLeft")).getText());
        driver.findElement(By.id("drankABeer")).click();
        assertEquals(beers - 1, Integer.parseInt(driver.findElement(By.id("beersLeft")).getText()));
    }
}
