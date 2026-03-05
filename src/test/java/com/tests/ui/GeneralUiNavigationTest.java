package com.tests.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.example.pages.FooterSection;
import org.example.pages.HomePage;
import org.example.pages.JobDetailPage;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

public class GeneralUiNavigationTest extends BaseTest {

    private static final String BASE_URL = "https://www.naukri.com/";

    // ---------- TC-18 (priority 1): verifyCompanyLogoVisibility ----------
    @Test( description = "TC-18: Company logo is visible and rendered on job detail page")
    public void verifyCompanyLogoVisibility() {
        driver.get(BASE_URL);

        HomePage home = new HomePage(driver).waitForReady();
        home.clearSearch().typeSearch("qa engineer");
        home.clickSearch();

        // On results page, click first result (opens job detail in new tab)
        clickFirstResultFromSearch();

        // Switch to job detail tab and verify logo
        switchToNewTab();
        JobDetailPage detail = new JobDetailPage(driver);
        boolean rendered = detail.isCompanyLogoRendered();
        Assert.assertTrue(rendered, "Expected company logo image to be visible and rendered (naturalWidth > 0).");

        // Clean up: close tab and return
        closeCurrentTabAndSwitchBack();
    }

    // ---------- TC-19 (priority 2): verifyFooterSocialLinks (LinkedIn) ----------
    @Test( description = "TC-19: Scroll to footer, ensure LinkedIn icon visible, click, verify new tab host")
    public void verifyFooterLinkedInNavigation() {
        driver.get(BASE_URL);

        HomePage home = new HomePage(driver).waitForReady();
        FooterSection footer = new FooterSection(driver);

        // Ensure cookie bar doesn't block footer clicks
        home.dismissCookieBannerIfPresent();

        // Scroll until LinkedIn is visible (exact first, else generic)
        try {
            home.scrollUntilVisible(footer.linkedInExact);
        } catch (Exception ignored) {
            home.scrollUntilVisible(footer.linkedInAny);
        }

        // Click and verify the new tab URL host
        footer.clickLinkedInVisible();
        String newUrl = switchToNewTab();
        String host = getHost(newUrl);
        Assert.assertTrue(host.contains("linkedin.com"),
                "Expected LinkedIn host in new tab, but got: " + newUrl);

        closeCurrentTabAndSwitchBack();
    }

    // ---------- TC-20 (priority 3): verifySearchButtonState ----------
    @Test( description = "TC-20: Search button behavior when input is empty (disabled or validation)")
    public void verifySearchButtonState() {
        driver.get(BASE_URL);

        HomePage home = new HomePage(driver).waitForReady();
        boolean prevented = home.isEmptySearchPrevented();
        Assert.assertTrue(prevented,
                "Expected empty search to be prevented (disabled or validation), but it appears navigation occurred without input.");
    }

    // ----------------- Helpers -----------------

    private String getHost(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost() == null ? "" : uri.getHost();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    /** Results pages evolve; try several robust locators for "first job link". */
    private void clickFirstResultFromSearch() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(12));

        By[] candidates = new By[]{
                By.cssSelector("a.title"),                          // common job title link
                By.cssSelector("a[href*='job-listings']"),          // generic pattern
                By.xpath("(//article|//div[contains(@class,'list') or contains(@class,'tuple')])//a[normalize-space()][1]")
        };

        WebElement target = null;

        for (By by : candidates) {
            try {
                target = shortWait.until(ExpectedConditions.visibilityOfElementLocated(by));
                List<WebElement> many = driver.findElements(by);
                for (WebElement e : many) {
                    if (e.isDisplayed()) { target = e; break; }
                }
                if (target != null && target.isDisplayed()) break;
            } catch (TimeoutException ignored) {}
        }

        if (target == null) {
            Assert.fail("Could not locate a job result link to click.");
        }

        try {
            target.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
        }
    }

    /** Wait for a second tab, switch to it, return its URL. */
    private String switchToNewTab() {
        for (int i = 0; i < 30; i++) {
            if (driver.getWindowHandles().size() > 1) break;
            sleep(200);
        }
        var tabs = driver.getWindowHandles().toArray(new String[0]);
        driver.switchTo().window(tabs[tabs.length - 1]);
        return driver.getCurrentUrl();
    }

    private void closeCurrentTabAndSwitchBack() {
        String current = driver.getWindowHandle();
        var handles = driver.getWindowHandles().toArray(new String[0]);
        driver.close();
        for (String h : handles) {
            if (!h.equals(current)) {
                driver.switchTo().window(h);
                break;
            }
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}