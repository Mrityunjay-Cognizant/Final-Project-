package com.tests.ui;

import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.CredsUtil;
import java.io.IOException;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Directly initialize the Chrome driver
        DriverSetup.prepareModule();
        this.driver = DriverSetup.getDriver();
        System.out.println("Browser launched for test.");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupModule(ITestResult iTestResult) {
        // Close the browser after every test method
        DriverSetup.tearDown();
        System.out.println("Browser closed.");
    }

    @DataProvider(name = "creds")
    public Object [][] dataPr() throws IOException {
        // Pulls data from Excel for data-driven testing [cite: 280]
        return CredsUtil.getxl();
    }
}