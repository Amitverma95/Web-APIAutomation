package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import pageObject.CartPage_OR;
import pojo.Product;
import util.Common_Function;
import util.Log;

public class CartPageUtil {

    Common_Function cfObj;
    public List<String> cartPageMsgList = new ArrayList<String>();
    CartPage_OR cartPageOr;
    HeaderComponentUtil headerUtil;

    public CartPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        cartPageOr = new CartPage_OR();
        PageFactory.initElements(driver, cartPageOr);
        headerUtil = new HeaderComponentUtil(driver);
    }

    public boolean isCartPageDisplayed() {
        if (!cfObj.waitForUrlContains("cart.html")) {
            cartPageMsgList.add("Cart page is not opened");
            return false;
        }
        if (!headerUtil.isPageTitle("Your Cart")) {
            cartPageMsgList.addAll(headerUtil.headerMsgList);
            return false;
        }
        return true;
    }

    public List<Product> getCartItems() {
        return CartItemListComponent.readItems(cfObj, cartPageOr.getCartItems());
    }

    public boolean verifyCartDetails(List<Product> expectedProducts) {
        Log.info("Verify cart contains " + expectedProducts);
        List<Product> actualProducts = CartItemListComponent.readItems(cfObj, cartPageOr.getCartItems(), expectedProducts.size());
        return CartItemListComponent.verifyItems(expectedProducts, actualProducts, cartPageMsgList, "Cart");
    }

    public boolean removeProduct(String productName) {
        Log.info("Remove '" + productName + "' from cart page");
        try {
            // cart rows render after the url changes, so wait for them first
            List<WebElement> rows = cfObj.getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(CartPage_OR.CART_ROW));
            WebElement removeButton = null;
            for (WebElement row : rows) {
                if (row.findElement(CartPage_OR.ITEM_NAME).getText().trim().equals(productName)) {
                    removeButton = row.findElement(CartPage_OR.ITEM_REMOVE_BUTTON);
                    break;
                }
            }
            if (removeButton == null) {
                cartPageMsgList.add("'" + productName + "' not found in cart");
                return false;
            }
            int rowsBefore = rows.size();
            cfObj.click(removeButton);
            // make sure the row is really gone from the cart list
            cfObj.getWait().until(ExpectedConditions.numberOfElementsToBe(CartPage_OR.CART_ROW, rowsBefore - 1));
            return true;
        } catch (Exception e) {
            cartPageMsgList.add("removeProduct_Exception: " + e.getMessage());
            return false;
        }
    }

    public boolean proceedToCheckout() {
        Log.info("Proceed to checkout");
        cfObj.click(cartPageOr.getCheckoutButton());
        if (!cfObj.waitForUrlContains("checkout-step-one.html")) {
            cartPageMsgList.add("Checkout information page did not open");
            return false;
        }
        return true;
    }

}
