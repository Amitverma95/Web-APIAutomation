package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import pageObject.HeaderComponent_OR;
import pageObject.LoginPage_OR;
import util.Common_Function;
import util.Log;

/**
 * Reusable header component (title, cart, side menu) available on every page after login.
 */
public class HeaderComponentUtil {

    Common_Function cfObj;
    public List<String> headerMsgList = new ArrayList<String>();
    HeaderComponent_OR headerOr;

    public HeaderComponentUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        headerOr = new HeaderComponent_OR();
        PageFactory.initElements(driver, headerOr);
    }

    public String getPageTitle() {
        return cfObj.getText(headerOr.getPageTitle());
    }

    /**
     * Waits until the page title shows the expected text. The url changes before React renders the
     * new page, so title checks must wait for the text instead of reading it once.
     */
    public boolean isPageTitle(String strExpectedTitle) {
        try {
            // exact match, so a title like "Products Page" does not pass for "Products"
            return cfObj.getWait().until(ExpectedConditions.textToBe(HeaderComponent_OR.PAGE_TITLE, strExpectedTitle));
        } catch (Exception e) {
            headerMsgList.add("Page title expected '" + strExpectedTitle + "' but found '" + getPageTitle() + "'");
            return false;
        }
    }

    /** Number shown on the cart icon, 0 when the badge is not displayed. */
    public int getCartBadgeCount() {
        if (!cfObj.isElementPresent(HeaderComponent_OR.CART_BADGE)) {
            return 0;
        }
        return Integer.parseInt(cfObj.driver.findElement(HeaderComponent_OR.CART_BADGE).getText().trim());
    }

    public boolean openCart() {
        Log.info("Open cart from header");
        cfObj.click(headerOr.getCartLink());
        if (!cfObj.waitForUrlContains("cart.html")) {
            headerMsgList.add("Cart page did not open after clicking cart icon");
            return false;
        }
        return true;
    }

    public void openMenu() {
        cfObj.click(headerOr.getMenuButton());
        cfObj.waitForVisible(headerOr.getLogoutLink());
    }

    public boolean logout() {
        try {
            Log.info("Logout from side menu");
            openMenu();
            cfObj.click(headerOr.getLogoutLink());
            cfObj.waitForVisible(LoginPage_OR.LOGIN_BUTTON);
            if (cfObj.driver.getCurrentUrl().contains("inventory")) {
                headerMsgList.add("User was not redirected to login page after logout");
                return false;
            }
            return true;
        } catch (Exception e) {
            headerMsgList.add("logout_Exception: " + e.getMessage());
            return false;
        }
    }
}
