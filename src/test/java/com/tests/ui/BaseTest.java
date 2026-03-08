package com.tests.ui;

import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import utils.CredsUtil;

import java.io.IOException;

/**
 * BaseTest is the single source of truth for browser lifecycle.
 *
 * FIX SUMMARY:
 * - setup() carries @BeforeMethod(alwaysRun = true) and initialises the driver.
 * - cleanupModule() carries @AfterMethod(alwaysRun = true) and quits the driver.
 * - All sub-class @BeforeMethod methods must NOT call DriverSetup.prepareModule()
 *   again. They should only navigate to a URL or perform login steps.
 *   TestNG calls BOTH the parent and child @BeforeMethod — the parent always
 *   runs first (correct order), so the driver is ready when the subclass method runs.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        DriverSetup.prepareModule();
        this.driver = DriverSetup.getDriver();
        System.out.println("Browser launched for test.");
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