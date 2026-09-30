package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import pageObject.LoginPage_OR;
import util.Common_Function;
import util.Log;

public class LoginPageUtil {

    Common_Function cfObj;
    public List<String> loginPageMsgList = new ArrayList<String>();
    LoginPage_OR loginPageOr;
    InventoryPageUtil inventoryPageUtil;

    public LoginPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        loginPageOr = new LoginPage_OR();
        PageFactory.initElements(driver, loginPageOr);
        inventoryPageUtil = new InventoryPageUtil(driver);
    }

    public boolean isLoginPageDisplayed() {
        return cfObj.isDisplayed(loginPageOr.getLoginLogo()) && cfObj.isDisplayed(loginPageOr.getLoginButton());
    }

    public boolean login(String strUsername, String strPassword) {
        try {
            Log.info("Login with username '" + strUsername + "'");
            if (!isLoginPageDisplayed()) {
                loginPageMsgList.add("Login page is not displayed");
                return false;
            }
            cfObj.type(loginPageOr.getUsernameTextBox(), strUsername);
            cfObj.type(loginPageOr.getPasswordTextBox(), strPassword);
            cfObj.click(loginPageOr.getLoginButton());
            return true;
        } catch (Exception e) {
            loginPageMsgList.add("login_Exception: " + e.getMessage());
            return false;
        }
    }

    /** Positive login - user must land on the inventory page. */
    public boolean verifyLogin(String strUsername, String strPassword, String strInventoryUrl) {
        if (!login(strUsername, strPassword)) {
            return false;
        }
        if (!cfObj.waitForUrlContains("inventory.html")) {
            loginPageMsgList.add("User was not redirected to inventory page after login, current url: "
                    + cfObj.driver.getCurrentUrl());
            return false;
        }
        boolean result = true;
        if (!cfObj.driver.getCurrentUrl().equals(strInventoryUrl)) {
            loginPageMsgList.add("Expected url " + strInventoryUrl + " but found " + cfObj.driver.getCurrentUrl());
            result = false;
        }
        if (!inventoryPageUtil.isInventoryPageDisplayed()) {
            loginPageMsgList.addAll(inventoryPageUtil.inventoryPageMsgList);
            result = false;
        }
        if (result) {
            Log.info("Login successful, inventory page displayed");
        }
        return result;
    }

    /** Negative login - error must be shown and user must stay on login page. */
    public boolean verifyLoginError(String strUsername, String strPassword, String strExpectedError) {
        if (!login(strUsername, strPassword)) {
            return false;
        }
        boolean result = true;
        try {
            String strActualError = getErrorMessage();
            if (!strActualError.equals(strExpectedError)) {
                loginPageMsgList.add("Expected error '" + strExpectedError + "' but found '" + strActualError + "'");
                result = false;
            }
            if (cfObj.driver.getCurrentUrl().contains("inventory.html")) {
                loginPageMsgList.add("User was logged in with invalid credentials");
                result = false;
            }
        } catch (Exception e) {
            result = false;
            loginPageMsgList.add("Error message is not displayed: " + e.getMessage());
        }
        return result;
    }

    public String getErrorMessage() {
        return cfObj.getText(loginPageOr.getErrorMessage());
    }
}
