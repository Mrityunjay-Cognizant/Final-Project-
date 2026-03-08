package com.tests.ui;

import org.example.pages.DashBoard;
import org.example.pages.HomePage;
import org.example.pages.ProfilePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.CredsUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FIX SUMMARY:
 * - setupBrowser() no longer calls DriverSetup.prepareModule() or touches the browser setup.
 *   BaseTest.setup() (which runs before this @BeforeMethod) already launched the browser.
 * - This method now only handles navigation and login — its correct responsibility.
 * - Removed deprecated TimeUnit.SECONDS.sleep — replaced with Thread.sleep for clarity.
 */
public class ResumeHeaderAndSkillUpdateTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() throws IOException {
        // FIX: Only navigate and login. Driver is already ready from BaseTest.setup().
        driver.get("https://www.naukri.com/");
        Object[][] data = CredsUtil.getxl();
        String user = data[0][0].toString();
        String pass = data[0][1].toString();
        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(user, pass);
        db.navigateToSetting();
    }

    @Test
    public void editHeader() {
        String header = "B.Tech CSE Student | Full Stack Developer | Java, Spring Boot, Next.js, AWS | Passionate about Scalable Web Apps, Generative AI, Agentic AI, RAG, and Google Agent Development KitB.Tech CSE Student | Full Stack Developer | Java, Spring Boot,Next js";
        ProfilePage sp = new ProfilePage(driver);
        sp.editHeadLine(header);
        String afterEdit = sp.getHeadLine();
        Assert.assertTrue(afterEdit.contains(header));
    }

    @Test
    public void insertSkill() throws InterruptedException {
        ProfilePage sp = new ProfilePage(driver);
        String sampleSkill = "Spring Microservices";
        sp.insertSkill(sampleSkill);
        Thread.sleep(2000);
        List<String> skills = sp.getListOfSkillChip();
        System.out.println(skills);
        for (String skill : skills) {
            if (skill.contains(sampleSkill)) {
                Assert.assertTrue(true);
                return;
            }
        }
        Assert.fail("Skill '" + sampleSkill + "' was not found in the skill chips after insert.");
    }
}