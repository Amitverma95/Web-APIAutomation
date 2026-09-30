package test_scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.HeaderComponentUtil;
import applicationUtil.LoginPageUtil;
import dataProvider.TestDataProvider;
import util.TestDescriptionContant;

public class LoginTest extends BaseTest {

    LoginPageUtil loginPageUtilObj;

    @Test(groups = { "smoke", "regression", "login" }, description = TestDescriptionContant.VERIFY_LOGIN)
    public void verifyLogin() {
        loginPageUtilObj = new LoginPageUtil(getDriver());
        boolean result = loginPageUtilObj.verifyLogin(configReaderObj.getUsername(), configReaderObj.getPassword(),
                configReaderObj.getInventoryUrl());
        Assert.assertTrue(result, loginPageUtilObj.loginPageMsgList.toString());
    }

    @Test(groups = { "regression", "login", "negative" }, dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProvider.class, description = TestDescriptionContant.VERIFY_INVALID_LOGIN)
    public void verifyInvalidLogin(String testCase, String username, String password, String expectedError) {
        loginPageUtilObj = new LoginPageUtil(getDriver());
        boolean result = loginPageUtilObj.verifyLoginError(username, password, expectedError);
        Assert.assertTrue(result, testCase + ": " + loginPageUtilObj.loginPageMsgList);
    }

    @Test(groups = { "regression", "login" }, description = TestDescriptionContant.VERIFY_LOGOUT)
    public void verifyLogout() {
        loginWithValidUser();
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());
        Assert.assertTrue(headerUtil.logout(), headerUtil.headerMsgList.toString());
        loginPageUtilObj = new LoginPageUtil(getDriver());
        Assert.assertTrue(loginPageUtilObj.isLoginPageDisplayed(), "Login page should be displayed after logout");
    }
}
