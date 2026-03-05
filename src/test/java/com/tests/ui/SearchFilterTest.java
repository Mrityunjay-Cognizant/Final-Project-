
package com.tests.ui;

import org.example.pages.HomePage;
import org.example.pages.SearchFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class SearchFilterTest extends BaseTest {

    HomePage hp;
    SearchFilter sf;

    @BeforeMethod
    public void setupPage() {
        driver.get("https://www.naukri.com/");
        hp = new HomePage(driver);
        sf = new SearchFilter(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }


    @Test
    public void verifyTestResultRouting() throws InterruptedException {
        hp.typeSearch("QA Engineer");
        hp.clickSearch();
        Thread.sleep(100);
        //sf.clickLocationFilter();
        String actualUrl = driver.getCurrentUrl();

        Assert.assertTrue(actualUrl.contains("qa-engineer"),
                "The URL does not contain the keyword 'qa-engineer'. Actual URL: " + actualUrl);

        System.out.println("Test Passed! Validated via URL: " + actualUrl);
    }

    @Test
    public void verifyLocationFilter() throws InterruptedException {
        hp.typeSearch("QA Engineer");
        hp.clickSearch();

        Thread.sleep(1000);

        sf.clickLocationFilter();
    }


    @Test
    public void verifySalaryFilter() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        hp.typeSearch("QA Engineer");
        hp.clickSearch();


        String urlBefore = driver.getCurrentUrl();

        driver.findElement(By.tagName("body")).sendKeys(Keys.PAGE_DOWN);
        Thread.sleep(1000);


        sf.clickSalaryFilter();
        Thread.sleep(4000);

        String urlAfter = driver.getCurrentUrl();

        Assert.assertNotEquals(urlBefore, urlAfter,
                "The URL did not change after applying the salary filter!");

        Assert.assertTrue(urlAfter.contains("ctcFilter"),
                "URL does not contain salary filter parameters.");
    }

    @Test
    public void verifyZeroSearchResults() {
        hp.typeSearch("xyz123abc");
        hp.clickSearch();

        String errorMsg = sf.getErrorMessage();
        boolean found = errorMsg.contains("No results found") ||
                errorMsg.contains("No jobs match your criteria");

        Assert.assertTrue(found, "Error message for zero results not found. Got: " + errorMsg);
    }
}


