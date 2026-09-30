package applicationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import pageObject.CheckoutOverviewPage_OR;
import pojo.Product;
import util.Common_Function;
import util.Log;

public class CheckoutOverviewPageUtil {

    Common_Function cfObj;
    public List<String> checkoutOverviewMsgList = new ArrayList<String>();
    CheckoutOverviewPage_OR checkoutOverviewOr;
    HeaderComponentUtil headerUtil;

    public CheckoutOverviewPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        checkoutOverviewOr = new CheckoutOverviewPage_OR();
        PageFactory.initElements(driver, checkoutOverviewOr);
        headerUtil = new HeaderComponentUtil(driver);
    }

    public boolean isCheckoutOverviewPageDisplayed() {
        if (!cfObj.waitForUrlContains("checkout-step-two.html")) {
            checkoutOverviewMsgList.add("Checkout overview page is not opened");
            return false;
        }
        if (!headerUtil.isPageTitle("Checkout: Overview")) {
            checkoutOverviewMsgList.addAll(headerUtil.headerMsgList);
            return false;
        }
        return true;
    }

    public List<Product> getOrderItems() {
        return CartItemListComponent.readItems(cfObj, checkoutOverviewOr.getOrderItems());
    }

    public String getPaymentInfo() {
        return cfObj.getText(checkoutOverviewOr.getPaymentInfo());
    }

    public String getShippingInfo() {
        return cfObj.getText(checkoutOverviewOr.getShippingInfo());
    }

    public BigDecimal getItemTotal() {
        return Common_Function.parsePrice(cfObj.getText(checkoutOverviewOr.getItemTotal()));
    }

    public BigDecimal getTax() {
        return Common_Function.parsePrice(cfObj.getText(checkoutOverviewOr.getTax()));
    }

    public BigDecimal getTotal() {
        return Common_Function.parsePrice(cfObj.getText(checkoutOverviewOr.getTotal()));
    }

    /**
     * Verifies the items and the price calculation:
     * item total = sum(price x qty), tax = item total x tax rate, total = item total + tax.
     */
    public boolean verifyOrderSummary(List<Product> expectedProducts, BigDecimal taxRate) {
        Log.info("Verify order summary for " + expectedProducts);
        List<Product> actualProducts = CartItemListComponent.readItems(cfObj, checkoutOverviewOr.getOrderItems(),
                expectedProducts.size());
        boolean result = CartItemListComponent.verifyItems(expectedProducts, actualProducts, checkoutOverviewMsgList,
                "Checkout overview");

        BigDecimal expectedItemTotal = BigDecimal.ZERO;
        for (Product product : expectedProducts) {
            expectedItemTotal = expectedItemTotal.add(product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())));
        }
        BigDecimal expectedTax = expectedItemTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedTotal = expectedItemTotal.add(expectedTax);

        BigDecimal actualItemTotal = getItemTotal();
        BigDecimal actualTax = getTax();
        BigDecimal actualTotal = getTotal();
        Log.info("Item total: " + actualItemTotal + ", Tax: " + actualTax + ", Total: " + actualTotal);

        if (actualItemTotal.compareTo(expectedItemTotal) != 0) {
            checkoutOverviewMsgList.add("Item total expected $" + expectedItemTotal + " but found $" + actualItemTotal);
            result = false;
        }
        if (actualTax.compareTo(expectedTax) != 0) {
            checkoutOverviewMsgList.add("Tax expected $" + expectedTax + " but found $" + actualTax);
            result = false;
        }
        if (actualTotal.compareTo(expectedTotal) != 0) {
            checkoutOverviewMsgList.add("Total expected $" + expectedTotal + " but found $" + actualTotal);
            result = false;
        }
        return result;
    }

    /** Page is shown, order summary is correct and payment / shipping info match. */
    public boolean verifyOverview(List<Product> expectedProducts, BigDecimal taxRate, String strPaymentInfo, String strShippingInfo) {
        if (!isCheckoutOverviewPageDisplayed()) {
            return false;
        }
        boolean result = verifyOrderSummary(expectedProducts, taxRate);
        String strActualPayment = getPaymentInfo();
        if (!strActualPayment.equals(strPaymentInfo)) {
            checkoutOverviewMsgList.add("Payment info expected '" + strPaymentInfo + "' but found '" + strActualPayment + "'");
            result = false;
        }
        String strActualShipping = getShippingInfo();
        if (!strActualShipping.equals(strShippingInfo)) {
            checkoutOverviewMsgList.add("Shipping info expected '" + strShippingInfo + "' but found '" + strActualShipping + "'");
            result = false;
        }
        return result;
    }

    public boolean finishOrder() {
        Log.info("Finish order");
        cfObj.click(checkoutOverviewOr.getFinishButton());
        if (!cfObj.waitForUrlContains("checkout-complete.html")) {
            checkoutOverviewMsgList.add("Checkout complete page did not open after Finish");
            return false;
        }
        return true;
    }
}
