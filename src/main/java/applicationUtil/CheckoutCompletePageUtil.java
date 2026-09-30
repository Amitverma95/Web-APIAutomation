package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import pageObject.CheckoutCompletePage_OR;
import util.Common_Function;
import util.Log;

public class CheckoutCompletePageUtil {

    Common_Function cfObj;
    public List<String> checkoutCompleteMsgList = new ArrayList<String>();
    CheckoutCompletePage_OR checkoutCompleteOr;
    HeaderComponentUtil headerUtil;

    public CheckoutCompletePageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        checkoutCompleteOr = new CheckoutCompletePage_OR();
        PageFactory.initElements(driver, checkoutCompleteOr);
        headerUtil = new HeaderComponentUtil(driver);
    }

    public boolean verifyOrderConfirmation(String strExpectedHeader, String strExpectedText) {
        Log.info("Verify order confirmation");
        boolean result = true;
        try {
            if (!cfObj.waitForUrlContains("checkout-complete.html")) {
                checkoutCompleteMsgList.add("Checkout complete page is not opened");
                return false;
            }
            if (!headerUtil.isPageTitle("Checkout: Complete!")) {
                checkoutCompleteMsgList.addAll(headerUtil.headerMsgList);
                result = false;
            }
            String strActualHeader = cfObj.getText(checkoutCompleteOr.getCompleteHeader());
            if (!strActualHeader.equals(strExpectedHeader)) {
                checkoutCompleteMsgList.add("Expected header '" + strExpectedHeader + "' but found '" + strActualHeader + "'");
                result = false;
            }
            String strActualText = cfObj.getText(checkoutCompleteOr.getCompleteText());
            if (!strActualText.equals(strExpectedText)) {
                checkoutCompleteMsgList.add("Expected text '" + strExpectedText + "' but found '" + strActualText + "'");
                result = false;
            }
            if (!cfObj.isDisplayed(checkoutCompleteOr.getPonyExpressImage())) {
                checkoutCompleteMsgList.add("Order confirmation image is not displayed");
                result = false;
            }
        } catch (Exception e) {
            checkoutCompleteMsgList.add("verifyOrderConfirmation_Exception: " + e.getMessage());
            result = false;
        }
        return result;
    }

}
