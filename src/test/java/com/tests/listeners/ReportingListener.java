package com.tests.listeners;

import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportUtil;

/**
 * FIX SUMMARY:
 * - Removed all screenshot capture code (captureScreenshot method and its call
 *   in onTestFailure). Screenshots required commons-io (FileUtils) which was
 *   not in pom.xml, causing a compile error.
 * - Removed org.apache.commons.io.FileUtils import.
 * - Removed org.openqa.selenium.OutputType and TakesScreenshot imports.
 * - The listener now only handles Extent Report lifecycle: init, create test,
 *   log failure message, and flush.
 */
public class ReportingListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportUtil.initReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportUtil.createTest(result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportUtil.getTest().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportUtil.getTest().fail("Test Failed: " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportUtil.getTest().skip("Test Skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportUtil.flushReport();
    }
}