package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverCommandExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * DriverSetup — no downloads required, works on locked corporate laptops.
 *
 * PROBLEM: Selenium Manager downloads Chrome 146 into the cache and uses it
 * as the browser binary. That downloaded binary is blocked by corporate policy.
 *
 * SOLUTION:
 * - Use the ChromeDriver that Selenium already cached (it works fine as a driver)
 * - But point it to launch YOUR system-installed Chrome (which is policy-approved)
 * - Block Selenium Manager from overriding this with a system property
 */
public class DriverSetup {

    private static WebDriver driver;

    // Your system Chrome — the one you normally browse with (policy-approved)
    private static final String[] SYSTEM_CHROME_PATHS = {
        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe"
    };

    // Selenium Manager's cached ChromeDriver location (already on your machine)
    private static final String SELENIUM_CACHE_DIR =
        System.getProperty("user.home") + "\\.cache\\selenium\\chromedriver";

    public static void prepareModule() {
        if (driver == null) {

            // Step 1: Find the system Chrome binary
            String systemChrome = findSystemChrome();
            if (systemChrome == null) {
                throw new RuntimeException(
                    "[DriverSetup] Could not find Chrome at standard paths.\n" +
                    "Open Chrome, go to chrome://version and check 'Executable Path', " +
                    "then add that path to SYSTEM_CHROME_PATHS in DriverSetup.java"
                );
            }
            System.out.println("[DriverSetup] System Chrome found: " + systemChrome);

            // Step 2: Find the cached ChromeDriver (Selenium already downloaded this)
            String cachedDriver = findCachedChromeDriver();
            if (cachedDriver != null) {
                System.out.println("[DriverSetup] Cached ChromeDriver found: " + cachedDriver);
                // Tell Selenium to use this specific ChromeDriver executable
                System.setProperty("webdriver.chrome.driver", cachedDriver);
            } else {
                System.out.println("[DriverSetup] No cached ChromeDriver found, Selenium Manager will handle it.");
            }

            // Step 3: Build ChromeOptions pointing at system Chrome
            ChromeOptions options = new ChromeOptions();
            options.setBinary(systemChrome);

            // Step 4: Block Selenium Manager from downloading/overriding the Chrome browser
            // SE_AVOID_BROWSER_DOWNLOAD=true tells Selenium Manager: "don't touch the browser"
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

            // Stability flags for corporate environments
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

            // Fresh temp profile — prevents "profile locked" crash when Chrome is already open
            String tempDir = System.getProperty("java.io.tmpdir")
                    + "\\sel_" + ProcessHandle.current().pid();
            options.addArguments("--user-data-dir=" + tempDir);
            options.addArguments("--start-maximized");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("[DriverSetup] Chrome launched successfully.");
        }
    }

    /**
     * Finds the first system Chrome binary that actually exists on this machine.
     */
    private static String findSystemChrome() {
        for (String path : SYSTEM_CHROME_PATHS) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    /**
     * Scans Selenium's cache folder for any chromedriver.exe that was already
     * downloaded in a previous run. Returns the most recently modified one,
     * or null if none found.
     */
    private static String findCachedChromeDriver() {
        Path cacheRoot = Paths.get(SELENIUM_CACHE_DIR);
        if (!Files.exists(cacheRoot)) {
            return null;
        }
        try (Stream<Path> walk = Files.walk(cacheRoot)) {
            Optional<Path> found = walk
                .filter(p -> p.getFileName().toString().equalsIgnoreCase("chromedriver.exe"))
                .max(Comparator.comparingLong(p -> p.toFile().lastModified()));
            return found.map(Path::toString).orElse(null);
        } catch (IOException e) {
            System.out.println("[DriverSetup] Could not scan cache: " + e.getMessage());
            return null;
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