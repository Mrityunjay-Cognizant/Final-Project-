package com.tests.ui;


import org.example.pages.DashBoard;
import org.example.pages.HomePage;

import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
public class AuthTest extends BaseTest {
    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {
        driver.get("https://www.naukri.com/");

    }
    // Removed redundant login logic from individual tests where possible

    @Test(dataProvider = "creds", groups = {"login_flow"})
    public void verifySuccessfulLogin(String userName, String pass) {

        HomePage lp = new HomePage(driver);
        lp.login(userName, pass);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // It's safer to wait for an element on the dashboard rather than just the URL
        Assert.assertTrue(wait.until(ExpectedConditions.urlContains("homepage")),
                "Login failed: URL does not contain 'homepage'");
    }
    @Test()
    public void verifyPasswordMasking() {
        HomePage lp = new HomePage(driver);
        String inputType =lp.getPasswordFieldType();
        // Assert that it is 'password' to ensure masking is active
        Assert.assertEquals(inputType, "password", "CRITICAL: Password field is not masked!");

        System.out.println("Masking verification passed. Input type is: " + inputType);
    }
    @Test(groups = {"login_flow"})
    public void verifyInvalidLoginErrorMessage() {

        HomePage lp = new HomePage(driver);
        lp.login("wrong@email.com", "wrongpassword");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Use presenceOfElement first, then getText
        WebElement errorBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='server-err']")));

        String actualMsg = errorBox.getText();
        Assert.assertTrue(actualMsg.contains("Invalid details"), "Error message text mismatch! Found: " + actualMsg);
    }

    @Test( dataProvider = "creds")
    public void verifyLogout(String userName, String pass) {
        HomePage lp = new HomePage(driver);

        DashBoard db = lp.login(userName, pass);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("homepage"));

        // Critical: Ensure the logout method handles the 'Hover' action
        db.logout();

        Assert.assertFalse(wait.until(ExpectedConditions.urlToBe("https://www.naukri.com/")),
                "User was not redirected to landing page after logout.");
    }
// THREADLOCAL AND PARALLEL
}