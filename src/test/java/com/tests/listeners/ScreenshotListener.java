package com.tests.listeners;

import org.apache.commons.io.FileUtils;
import org.example.utils.DriverSetup;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;

public class ScreenshotListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result){
        System.out.println("failed");

    }


}
