package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

/**
 * DriverSetup — based on your original working version.
 *
 * WHY EDGE INSTEAD OF CHROME:
 * Selenium Manager was downloading Chrome 146 into:
 *   C:\Users\<user>\.cache\selenium\chrome\win64\146.x.x\chrome.exe
 * That cached binary is blocked by Cognizant corporate policy → crash.
 *
 * Microsoft Edge is pre-installed on every Windows 10/11 machine including
 * corporate laptops. Selenium Manager handles its driver automatically,
 * and the browser itself is never blocked by IT policy.
 * No downloads, no path configuration — it just works.
 */
public class DriverSetup {

    // Static driver — matches your sequential (non-parallel) test design.
    // Your original used ThreadLocal, but your testng.xml runs parallel="none"
    // so a plain static variable is simpler and equally correct here.
    private static WebDriver driver;

    public static void prepareModule() {
        if (driver == null) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-gpu");

            driver = new EdgeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("Browser launched: Microsoft Edge");
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}