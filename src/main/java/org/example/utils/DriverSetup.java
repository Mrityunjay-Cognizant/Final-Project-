package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

/**
 * DriverSetup manages a single static WebDriver instance.
 * Designed for sequential (non-parallel) test execution only.
 *
 * ROOT CAUSE OF CRASH:
 * Selenium Manager auto-downloads Chrome into:
 *   C:\Users\<user>\.cache\selenium\chrome\win64\146.x.x\chrome.exe
 * On corporate/Cognizant machines, that binary is blocked by policy and crashes
 * immediately with "DevToolsActivePort file doesn't exist".
 *
 * FIX: Point ChromeOptions at the SYSTEM-INSTALLED Chrome binary directly.
 * This is the Chrome that is already trusted and allowed by corporate policy.
 */
public class DriverSetup {

    private static WebDriver driver;

    // Standard installation paths for Chrome on Windows.
    // The first one that actually exists on this machine will be used.
    private static final String[] CHROME_PATHS = {
        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe"
    };

    public static void prepareModule() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();

            // --- POINT TO SYSTEM CHROME (bypasses the Selenium-cached binary) ---
            String chromeBinary = findSystemChrome();
            if (chromeBinary != null) {
                System.out.println("Using system Chrome at: " + chromeBinary);
                options.setBinary(chromeBinary);
            } else {
                System.out.println("WARNING: System Chrome not found at known paths. " +
                    "Selenium Manager will attempt to use cached binary, which may crash " +
                    "on corporate machines. Add your Chrome path to CHROME_PATHS in DriverSetup.");
            }

            // --- STABILITY FLAGS ---
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-sync");
            options.addArguments("--safebrowsing-disable-auto-update");
            options.addArguments("--password-store=basic");
            options.addArguments("--use-mock-keychain");

            // Use a fresh temporary profile directory per run to avoid profile lock
            // conflicts when a regular Chrome window is already open on the machine.
            String tempDir = System.getProperty("java.io.tmpdir")
                    + "\\selenium_chrome_" + ProcessHandle.current().pid();
            options.addArguments("--user-data-dir=" + tempDir);

            options.addArguments("--start-maximized");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }

    /**
     * Returns the path of the first Chrome binary found on this machine,
     * or null if none of the known paths exist.
     */
    private static String findSystemChrome() {
        for (String path : CHROME_PATHS) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
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