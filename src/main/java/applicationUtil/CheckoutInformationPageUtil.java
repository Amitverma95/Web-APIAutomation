package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import pageObject.CheckoutInformationPage_OR;
import pojo.CustomerDetails;
import util.Common_Function;
import util.Log;

public class CheckoutInformationPageUtil {

    Common_Function cfObj;
    public List<String> checkoutInfoMsgList = new ArrayList<String>();
    CheckoutInformationPage_OR checkoutInfoOr;
    HeaderComponentUtil headerUtil;

    public CheckoutInformationPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        checkoutInfoOr = new CheckoutInformationPage_OR();
        PageFactory.initElements(driver, checkoutInfoOr);
        headerUtil = new HeaderComponentUtil(driver);
    }

    public boolean isCheckoutInformationPageDisplayed() {
        if (!cfObj.waitForUrlContains("checkout-step-one.html")) {
            checkoutInfoMsgList.add("Checkout information page is not opened");
            return false;
        }
        if (!headerUtil.isPageTitle("Checkout: Your Information")) {
            checkoutInfoMsgList.addAll(headerUtil.headerMsgList);
            return false;
        }
        return true;
    }

    public void enterCustomerDetails(CustomerDetails customer) {
        Log.info("Enter customer details " + customer);
        cfObj.type(checkoutInfoOr.getFirstNameTextBox(), customer.getFirstName());
        cfObj.type(checkoutInfoOr.getLastNameTextBox(), customer.getLastName());
        cfObj.type(checkoutInfoOr.getPostalCodeTextBox(), customer.getPostalCode());
    }

    public void clickContinue() {
        cfObj.click(checkoutInfoOr.getContinueButton());
    }

    /** Positive flow - fills the form and moves to the overview page. */
    public boolean continueWithCustomerDetails(CustomerDetails customer) {
        try {
            enterCustomerDetails(customer);
            clickContinue();
            if (!cfObj.waitForUrlContains("checkout-step-two.html")) {
                checkoutInfoMsgList.add("Checkout overview page did not open for " + customer
                        + (cfObj.isDisplayed(checkoutInfoOr.getErrorMessage()) ? ", error: " + getErrorMessage() : ""));
                return false;
            }
            return true;
        } catch (Exception e) {
            checkoutInfoMsgList.add("continueWithCustomerDetails_Exception: " + e.getMessage());
            return false;
        }
    }

    /** Negative flow - form must show the expected error and stay on the information page. */
    public boolean verifyCustomerDetailsError(CustomerDetails customer, String strExpectedError) {
        try {
            enterCustomerDetails(customer);
            clickContinue();
            boolean result = true;
            String strActualError = getErrorMessage();
            if (!strActualError.equals(strExpectedError)) {
                checkoutInfoMsgList.add("Expected error '" + strExpectedError + "' but found '" + strActualError + "'");
                result = false;
            }
            if (!cfObj.driver.getCurrentUrl().contains("checkout-step-one.html")) {
                checkoutInfoMsgList.add("User moved past the information page with invalid details " + customer);
                result = false;
            }
            return result;
        } catch (Exception e) {
            checkoutInfoMsgList.add("Error message is not displayed: " + e.getMessage());
            return false;
        }
    }

    public String getErrorMessage() {
        return cfObj.getText(checkoutInfoOr.getErrorMessage());
    }

    public boolean cancelCheckout() {
        Log.info("Cancel checkout from information page");
        cfObj.click(checkoutInfoOr.getCancelButton());
        if (!cfObj.waitForUrlContains("cart.html")) {
            checkoutInfoMsgList.add("Did not return to cart page on Cancel");
            return false;
        }
        return true;
    }
}
