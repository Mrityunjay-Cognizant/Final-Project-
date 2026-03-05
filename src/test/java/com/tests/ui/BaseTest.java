package com.tests.ui;

import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;

import org.testng.annotations.*;
import utils.CredsUtil;

import java.io.IOException;

public class BaseTest {

        protected WebDriver driver;
     @Parameters("browser")
    @BeforeMethod(alwaysRun = true) // always test=true , if we do grouping in xml it helps their
    public void prepareModule(String browser) {
        DriverSetup.prepareModule(browser);
        driver =DriverSetup.getDriver();


        System.out.println("Browser launched for test method.");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupModule() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
     @DataProvider(name = "creds")
     public Object [][] dataPr() throws IOException {
        return CredsUtil.getxl();
     }


}
