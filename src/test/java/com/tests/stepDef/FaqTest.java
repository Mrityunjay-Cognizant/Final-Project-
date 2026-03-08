package com.tests.stepDef;

import com.tests.ui.BaseTest;
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

public class FaqTest {
    private FaqPage fp;
    private String suggestedText;
    private WebDriver driver;

//    @Before
//    public void setup(){
//
//
//    }

    @Given("User is on the FaqHomepage")
    public void userIsOnTheFaqHomepage() throws IOException {
        // FIX 1: Remove "chrome" because prepareModule() now takes no arguments
        DriverSetup.prepareModule();

        // FIX 2: Assign the driver instance to the local variable
        this.driver = DriverSetup.getDriver();

        // Now driver is not null and can navigate
        driver.get("https://www.naukri.com/");

        Object[][] data = CredsUtil.getxl();
        String user = data[0][0].toString();
        String pass = data[0][1].toString();

        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(user, pass);

        this.fp = db.navigateToFaq();
    }
    @When("User serches jo")
    public void userSerchesJo() {
        this.suggestedText = fp.search("jo");



    }



    @Then("search result module should be appear and headline should be shown")
    public void searchResultModuleShouldBeAppearAndHeadlineShouldBeShown() {
        // Capture the headline text from the results page
        String resultHeadline = fp.getSearchResultHeadlineText();


        Assert.assertEquals(resultHeadline.toLowerCase().trim(),'"'+suggestedText.toLowerCase().trim().substring(0,suggestedText.length()-1)+'"',
                "Semantic mismatch! Expected: " + suggestedText + " but got: " + resultHeadline);
        System.out.println("Suggestion: " + suggestedText);
        System.out.println("Headline: " + resultHeadline);


    }
}
