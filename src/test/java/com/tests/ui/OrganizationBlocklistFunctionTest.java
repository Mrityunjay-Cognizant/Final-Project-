package com.tests.ui;

import org.example.pages.AccountSettingPage;
import org.example.pages.DashBoard;
import org.example.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.CredsUtil;

import java.io.IOException;
import java.util.List;

public class OrganizationBlocklistFunctionTest extends BaseTest {
    private AccountSettingPage asp;
    @BeforeMethod
    public void setupBrowser() throws IOException {
        driver.get("https://www.naukri.com/");
        Object[][] data = CredsUtil.getxl();
        String user = data[0][0].toString();
        String pass = data[0][1].toString();
        HomePage lp = new HomePage(driver);
        DashBoard db = lp.login(user,pass);
       this.asp= db.navigateToAccountSetting();

    }
    @Test
    public void addToBlocklistAndRemoveFromIt(){
        String sub = "Cognizant";
        asp.navigateToBlocking();
        asp.setBlockCompany(sub);
        List<String>lst = asp.getListOfBlockCompanies();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).equalsIgnoreCase(sub)){
                Assert.assertTrue(true);
                return;
            }

        }
        Assert.fail();

    }

}
