package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

public class DriverSetup {

    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public static void prepareModule(String browser) {
        // 2. Initialize the driver locally
        WebDriver localDriver;
        if (browser.equals("edge")){
            localDriver = new EdgeDriver();

        }else{
           localDriver  = new ChromeDriver();

        }


        localDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // 3. Set the local driver into the ThreadLocal container
        driverThread.set(localDriver);
        System.out.println("Browser launched on Thread ID: " + Thread.currentThread().getId());
    }

    // 4. Use this method throughout your Page Objects/Tests to get the driver
    public static WebDriver getDriver() {
        return driverThread.get();
    }

    public static void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driverThread.remove(); // 5. Important: Clean up memory
        }
    }
}
