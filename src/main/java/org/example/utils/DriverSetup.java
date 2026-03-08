package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

/**
 * DriverSetup — single static WebDriver for sequential test execution.
 *
 * THE REAL PROBLEM:
 * Even when options.setBinary() is set, Selenium 4.x's built-in "Selenium Manager"
 * silently overrides it and downloads+uses its own Chrome binary from:
 *   C:\Users\<user>\.cache\selenium\chrome\win64\...
 * That downloaded binary is blocked by Cognizant corporate policy → crash.
 *
 * THE FIX (two-part):
 * 1. Set system property "webdriver.chrome.driver" to point ChromeDriver service
 *    at the ChromeDriver that matches your installed Chrome version.
 *    Download the correct ChromeDriver from: https://chromedriver.chromium.org/downloads
 *    Place it at: C:\drivers\chromedriver.exe  (or any path you choose)
 *
 * 2. Set the Chrome binary explicitly AND disable Selenium Manager so it cannot
 *    override your settings.
 */
public class DriverSetup {

    private static WebDriver driver;

    // -----------------------------------------------------------------------
    // STEP 1: Check your installed Chrome version:
    //   Open Chrome → three dots menu → Help → About Google Chrome
    //   e.g. if your Chrome is version 124, download ChromeDriver 124 from:
    //   https://chromedriver.chromium.org/downloads
    //
    // STEP 2: Place chromedriver.exe anywhere, update this path:
    private static final String CHROMEDRIVER_PATH = "C:\\drivers\\chromedriver.exe";
    //
    // STEP 3: Your system Chrome binary (leave as-is — these are auto-detected):
    private static final String[] CHROME_BINARY_PATHS = {
        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe"
    };
    // -----------------------------------------------------------------------

    public static void prepareModule() {
        if (driver == null) {

            // --- DISABLE SELENIUM MANAGER (prevents it from overriding your binary) ---
            System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);

            ChromeOptions options = new ChromeOptions();

            // --- FORCE SYSTEM CHROME BINARY ---
            String chromeBinary = findSystemChrome();
            if (chromeBinary != null) {
                System.out.println("[DriverSetup] Using Chrome binary: " + chromeBinary);
                options.setBinary(chromeBinary);
            } else {
                throw new RuntimeException(
                    "[DriverSetup] Could not find Chrome at any known path.\n" +
                    "Check CHROME_BINARY_PATHS in DriverSetup.java and add your Chrome location."
                );
            }

            // Verify ChromeDriver exe exists
            if (!new File(CHROMEDRIVER_PATH).exists()) {
                throw new RuntimeException(
                    "[DriverSetup] ChromeDriver not found at: " + CHROMEDRIVER_PATH + "\n" +
                    "Steps to fix:\n" +
                    "  1. Open Chrome → Menu → Help → About Google Chrome → note your version (e.g. 124)\n" +
                    "  2. Download matching ChromeDriver from https://chromedriver.chromium.org/downloads\n" +
                    "  3. Extract chromedriver.exe to C:\\drivers\\chromedriver.exe\n" +
                    "  4. Update CHROMEDRIVER_PATH in DriverSetup.java if you chose a different location."
                );
            }

            System.out.println("[DriverSetup] Using ChromeDriver: " + CHROMEDRIVER_PATH);

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

            // Fresh profile per run — avoids "profile locked" crash when Chrome is already open
            String tempDir = System.getProperty("java.io.tmpdir")
                    + "\\selenium_chrome_" + ProcessHandle.current().pid();
            options.addArguments("--user-data-dir=" + tempDir);
            options.addArguments("--start-maximized");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("[DriverSetup] Chrome launched successfully.");
        }
    }

    private static String findSystemChrome() {
        for (String path : CHROME_BINARY_PATHS) {
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