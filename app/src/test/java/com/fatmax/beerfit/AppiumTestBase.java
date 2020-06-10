package com.fatmax.beerfit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class AppiumTestBase {

    AndroidDriver driver;
    AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder().usingAnyFreePort());

    WebDriverWait wait;
    long waitTime = 5;
    long pollTime = 50;

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
        wait = new WebDriverWait(driver, waitTime, pollTime);
    }

    @After
    public void tearDownDriver() {
        String screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
        driver.quit();
        service.stop();
    }
}
