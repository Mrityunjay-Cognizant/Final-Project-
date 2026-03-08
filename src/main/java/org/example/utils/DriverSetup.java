package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * DriverSetup manages a single static WebDriver instance.
 * Designed for sequential (non-parallel) test execution only.
 */
public class DriverSetup {

    private static WebDriver driver;

    public static void prepareModule() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();

            // --- CORE CRASH FIXES (corporate/restricted Windows environments) ---
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");

            // Fixes "DevToolsActivePort file doesn't exist" crash
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-software-rasterizer");

            // Fixes crashes caused by corporate network/proxy intercepting Chrome
            options.addArguments("--no-proxy-server");
            options.addArguments("--disable-extensions");

            // Fixes crashes from profile locks or restricted app policies
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-sync");
            options.addArguments("--disable-translate");
            options.addArguments("--metrics-recording-only");
            options.addArguments("--safebrowsing-disable-auto-update");
            options.addArguments("--password-store=basic");
            options.addArguments("--use-mock-keychain");

            // Use a clean temporary user data directory per process.
            // Fixes conflicts when another Chrome window is already open on the machine.
            String tempDir = System.getProperty("java.io.tmpdir")
                    + "\\selenium_chrome_" + ProcessHandle.current().pid();
            options.addArguments("--user-data-dir=" + tempDir);

            options.addArguments("--start-maximized");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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