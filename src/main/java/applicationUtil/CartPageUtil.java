package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

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
        WebElement removeButton;
        try {
            // wait for the row - cart rows render after the url changes
            removeButton = cfObj.getWait().until(d -> {
                for (WebElement row : cartPageOr.getCartItems()) {
                    if (row.findElement(CartPage_OR.ITEM_NAME).getText().trim().equals(productName)) {
                        return row.findElement(CartPage_OR.ITEM_REMOVE_BUTTON);
                    }
                }
                return null;
            });
        } catch (Exception e) {
            cartPageMsgList.add("'" + productName + "' not found in cart");
            return false;
        }
        try {
            cfObj.click(removeButton);
            return cfObj.getWait().until(d -> getCartItems().stream().noneMatch(p -> p.getName().equals(productName)));
        } catch (Exception e) {
            cartPageMsgList.add("'" + productName + "' is still in the cart after remove: " + e.getMessage());
        }
        return false;
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
