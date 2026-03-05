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

public class ResumeHeaderAndSkillUpdateTest extends BaseTest {

    @BeforeMethod
    public void setupBrowser() throws IOException {
        driver.get("https://www.naukri.com/");
        Object[][] data = CredsUtil.getxl();
        String user = data[0][0].toString();
        String pass = data[0][1].toString();
        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(user,pass);
        db.navigateToSetting();

    }


    @Test
    public void editHeader(){
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
        TimeUnit.SECONDS.sleep(2);
        List<String> skills = sp.getListOfSkillChip();
        System.out.println(skills);
        for (String skill : skills) {
            if (skill.contains(sampleSkill)) {
                Assert.assertTrue(true);
                return;
            }


        }
        Assert.fail();

    }



}
