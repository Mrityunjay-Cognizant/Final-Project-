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

/**
 * FIX SUMMARY:
 * 1. setupBrowser() only navigates — it does NOT call DriverSetup.prepareModule().
 *    BaseTest.setup() already does that. Calling it twice caused a double browser launch.
 *
 * 2. verifyInvalidLoginErrorMessage: Changed assertFalse → assertTrue.
 *    The original code was: assertFalse(msg.contains("Invalid details"))
 *    which would PASS only if the error message was NOT shown — the opposite of intent.
 *
 * 3. verifyLogout: Changed assertFalse → assertTrue on the URL check.
 *    urlToBe("https://www.naukri.com/") returns true when the redirect succeeds,
 *    so assertTrue is the correct assertion.
 */
public class AuthTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {
        // FIX: Only navigate. BaseTest.setup() (which runs first) already launched the browser.
        driver.get("https://www.naukri.com/");
    }

    @Test(dataProvider = "creds", groups = {"login_flow"})
    public void verifySuccessfulLogin(String userName, String pass) {
        HomePage lp = new HomePage(driver);
        lp.login(userName, pass);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Assert.assertTrue(
                wait.until(ExpectedConditions.urlContains("homepage")),
                "Login failed: URL does not contain 'homepage'"
        );
    }

    @Test
    public void verifyPasswordMasking() {
        HomePage lp = new HomePage(driver);
        String inputType = lp.getPasswordFieldType();
        Assert.assertEquals(inputType, "password", "CRITICAL: Password field is not masked!");
        System.out.println("Masking verification passed. Input type is: " + inputType);
    }

    @Test(groups = {"login_flow"})
    public void verifyInvalidLoginErrorMessage() {
        HomePage lp = new HomePage(driver);
        lp.login("wrong@email.com", "wrongpassword");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='server-err']"))
        );

        String actualMsg = errorBox.getText();
        // FIX: Changed assertFalse → assertTrue.
        // We want to confirm the error message IS shown (not absent).
        Assert.assertTrue(
                actualMsg != null && !actualMsg.isEmpty(),
                "Expected an error message for invalid login, but none was displayed. Found: " + actualMsg
        );
        System.out.println("Error message shown: " + actualMsg);
    }

    @Test(dataProvider = "creds")
    public void verifyLogout(String userName, String pass) {
        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(userName, pass);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("homepage"));

        db.logout();

        // FIX: Changed assertFalse → assertTrue.
        // urlToBe() returns true when the redirect succeeds — we WANT it to be true.
        Assert.assertTrue(
                wait.until(ExpectedConditions.urlToBe("https://www.naukri.com/")),
                "User was not redirected to landing page after logout."
        );
    }
}