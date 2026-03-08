package com.tests.ui;

import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import utils.CredsUtil;

import java.io.IOException;

/**
 * BaseTest is the single source of truth for browser lifecycle.
 *
 * HOW @BeforeMethod ORDERING WORKS IN TESTNG:
 * When a subclass also has @BeforeMethod, TestNG runs them in this order:
 *   1. BaseTest.setup()       ← launches browser, sets this.driver
 *   2. SubClass.setupBrowser() ← can safely use this.driver (it's already set)
 *
 * If BaseTest.setup() fails (e.g. Chrome crash), this.driver stays null.
 * The subclass @BeforeMethod then gets a NullPointerException on driver.get().
 * The guard check below prevents that cascade and shows the real error instead.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        DriverSetup.prepareModule();
        this.driver = DriverSetup.getDriver();

        if (this.driver == null) {
            throw new IllegalStateException(
                "Chrome failed to launch. Check DriverSetup — the system Chrome binary " +
                "may not be found, or the cached Selenium binary is blocked by corporate policy. " +
                "See the console log above for details."
            );
        }
        System.out.println("Browser launched successfully.");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupModule() {
        DriverSetup.tearDown();
        System.out.println("Browser closed.");
    }

    @DataProvider(name = "creds")
    public Object[][] dataPr() throws IOException {
        return CredsUtil.getxl();
    }
}