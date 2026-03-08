package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class DriverSetup {

    // Simple static variable for a single-threaded execution
    private static WebDriver driver;

    public static void prepareModule() {
        // Only initialize if the driver is null
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();

            // Essential arguments to prevent browser crashes in corporate environments
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--start-maximized");

            driver = new ChromeDriver(options);
            // standard wait time for finding elements [cite: 216]
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null; // Important: Reset to null so it can be re-initialized
        }
    }
}