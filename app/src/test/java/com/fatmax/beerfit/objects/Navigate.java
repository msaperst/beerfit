package com.fatmax.beerfit.objects;

import com.testpros.fast.By;
import com.testpros.fast.WebDriver;
import com.testpros.fast.WebElement;

import java.util.List;

public class Navigate {

    private final WebDriver driver;

    public Navigate(WebDriver driver) {
        this.driver = driver;
    }

    private List<WebElement> getMainMenu() {
        driver.findElement(By.AccessibilityId("Open")).click();
        driver.waitForElementPresent(By.className("android.widget.CheckedTextView"));
        return driver.findElements(By.className("android.widget.CheckedTextView"));
    }

    public void goBack() {
        driver.findElement(By.AccessibilityId("Navigate up")).click();
    }

    public void toActivities() {
        getMainMenu().get(0).click();
    }

    public void toGoals() {
        getMainMenu().get(1).click();
    }

    public void toMetrics() {
        getMainMenu().get(2).click();
    }

    public void clickOnExport() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
    }

    public void export() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(0).click();
        if (driver.isElementPresent(By.id("com.android.permissioncontroller:id/permission_allow_button"))) {
            driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        }
    }

    public void mport() {
        driver.findElement(By.AccessibilityId("More options")).click();
        List<WebElement> menuOptions = driver.findElements(By.className("android.widget.TextView"));
        menuOptions.get(1).click();
        if (driver.isElementPresent(By.id("com.android.permissioncontroller:id/permission_allow_button"))) {
            driver.findElement(By.id("com.android.permissioncontroller:id/permission_allow_button")).click();
        }
    }

    public void mport(int option) {
        mport();
        List<WebElement> imports = driver.findElements(By.id("android:id/text1"));
        imports.get(option).click();
    }
}
