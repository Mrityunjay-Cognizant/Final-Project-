package com.tests.listeners;

import org.apache.commons.io.FileUtils;
import org.example.utils.DriverSetup;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportingListener implements ITestListener {

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            System.out.println("Driver is null, skipping screenshot.");
            return null;
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = System.getProperty("user.dir") + "/reports/screenshots/" + screenshotName + "_" + timestamp + ".png";

        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(path);
            FileUtils.copyFile(source, destination);
            return path;
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onStart(ITestContext context) {
        ExtentReportUtil.initReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportUtil.createTest(result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverSetup.getDriver();
        String screenshotPath = captureScreenshot(driver, result.getName());

        ExtentReportUtil.getTest().fail("Test Failed: " + result.getThrowable());

        if (screenshotPath != null) {
            ExtentReportUtil.getTest().addScreenCaptureFromPath(screenshotPath);
        }
        // REMOVED: driver.quit() - BaseTest handles this now!
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportUtil.flushReport();
    }
}