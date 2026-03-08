package com.tests.stepDef;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.DashBoard;
import org.example.pages.FaqPage;
import org.example.pages.HomePage;
import org.example.utils.DriverSetup;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utils.CredsUtil;

import java.io.IOException;

/**
 * FIX SUMMARY:
 * - Cucumber step definitions manage their own driver lifecycle via @Before/@After hooks
 *   because they run outside of the TestNG BaseTest class hierarchy.
 * - DriverSetup.prepareModule() is correctly called in the @Before Cucumber hook.
 * - The driver is assigned from DriverSetup.getDriver() after prepareModule() is called,
 *   so it will never be null.
 * - Added DriverSetup.tearDown() in @After to quit the browser after the Cucumber scenario.
 */
public class FaqTest {

    private FaqPage fp;
    private String suggestedText;
    private WebDriver driver;

    @Before
    public void setup() throws IOException {
        // Cucumber tests manage their own driver — BaseTest is not in scope here.
        DriverSetup.prepareModule();
        this.driver = DriverSetup.getDriver();

        driver.get("https://www.naukri.com/");

        Object[][] data = CredsUtil.getxl();
        String user = data[0][0].toString();
        String pass = data[0][1].toString();

        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(user, pass);
        this.fp = db.navigateToFaq();
    }

    @io.cucumber.java.After
    public void teardown() {
        DriverSetup.tearDown();
    }

    @Given("User is on the FaqHomepage")
    public void userIsOnTheFaqHomepage() {
        // Navigation and login are handled in @Before hook above.
        // This step just confirms the FaqPage object is ready.
        Assert.assertNotNull(fp, "FaqPage was not initialised in the @Before hook.");
    }

    @When("User serches jo")
    public void userSerchesJo() {
        this.suggestedText = fp.search("jo");
    }

    @Then("search result module should be appear and headline should be shown")
    public void searchResultModuleShouldBeAppearAndHeadlineShouldBeShown() {
        String resultHeadline = fp.getSearchResultHeadlineText();
        // Trim the suggested text (it may have a trailing autocomplete character)
        String expectedFragment = suggestedText.toLowerCase().trim();
        Assert.assertTrue(
                resultHeadline.toLowerCase().trim().contains(expectedFragment) ||
                expectedFragment.contains(resultHeadline.toLowerCase().trim()),
                "Semantic mismatch! Expected headline to relate to: [" + suggestedText +
                "] but got: [" + resultHeadline + "]"
        );
        System.out.println("Suggestion: " + suggestedText);
        System.out.println("Headline:   " + resultHeadline);
    }
}