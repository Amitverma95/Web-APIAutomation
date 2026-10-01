package test_scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.HeaderComponentUtil;
import applicationUtil.LoginPageUtil;
import dataProvider.TestDataProvider;
import util.TestDescriptionContant;

public class LoginTest extends BaseTest {

    @Test(groups = { "smoke", "regression", "login" }, description = TestDescriptionContant.VERIFY_LOGIN)
    public void verifyLogin() {
        LoginPageUtil loginPageUtil = new LoginPageUtil(getDriver());
        boolean result = loginPageUtil.verifyLogin(configReaderObj.getUsername(), configReaderObj.getPassword(),
                configReaderObj.getInventoryUrl());
        Assert.assertTrue(result, loginPageUtil.loginPageMsgList.toString());
    }

    @Test(groups = { "regression", "login", "negative" }, dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProvider.class, description = TestDescriptionContant.VERIFY_INVALID_LOGIN)
    public void verifyInvalidLogin(String testCase, String username, String password, String expectedError) {
        LoginPageUtil loginPageUtil = new LoginPageUtil(getDriver());
        boolean result = loginPageUtil.verifyLoginError(username, password, expectedError);
        Assert.assertTrue(result, testCase + ": " + loginPageUtil.loginPageMsgList);
    }

    @Test(groups = { "regression", "login" }, description = TestDescriptionContant.VERIFY_LOGOUT)
    public void verifyLogout() {
        loginWithValidUser();
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());
        Assert.assertTrue(headerUtil.logout(), headerUtil.headerMsgList.toString());
        LoginPageUtil loginPageUtil = new LoginPageUtil(getDriver());
        Assert.assertTrue(loginPageUtil.isLoginPageDisplayed(), "Login page should be displayed after logout");
    }
}
