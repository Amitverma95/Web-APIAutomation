package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import pageObject.CartPage_OR;
import pojo.Product;
import util.Common_Function;

/**
 * Reusable component for the item list shared by the cart page and the checkout overview page.
 */
public class CartItemListComponent {

    /** Waits for rows to be visible then reads them. */
    public static List<Product> readItems(Common_Function cfObj, List<WebElement> rows) {
        cfObj.waitForAllVisible(rows);
        return read(rows);
    }

    /**
     * Waits until the expected number of rows is shown, then reads them. On timeout the actual rows are
     * read anyway, so verifyItems() reports the mismatch.
     */
    public static List<Product> readItems(Common_Function cfObj, List<WebElement> rows, int expectedCount) {
        try {
            cfObj.getWait().until(ExpectedConditions.numberOfElementsToBe(CartPage_OR.CART_ROW, expectedCount));
        } catch (TimeoutException e) {
            // count mismatch is reported by verifyItems()
        }
        return read(rows);
    }

    private static List<Product> read(List<WebElement> rows) {
        List<Product> items = new ArrayList<>();
        for (WebElement row : rows) {
            String name = row.findElement(CartPage_OR.ITEM_NAME).getText().trim();
            String price = row.findElement(CartPage_OR.ITEM_PRICE).getText();
            int quantity = Integer.parseInt(row.findElement(CartPage_OR.ITEM_QUANTITY).getText().trim());
            items.add(new Product(name, Common_Function.parsePrice(price), quantity));
        }
        return items;
    }

    /** Compares expected and actual items (name, price, quantity) and records every mismatch in msgList. */
    public static boolean verifyItems(List<Product> expected, List<Product> actual, List<String> msgList, String pageName) {
        boolean result = true;
        if (expected.size() != actual.size()) {
            msgList.add(pageName + ": expected " + expected.size() + " item(s) but found " + actual.size() + " - " + actual);
            result = false;
        }
        for (Product product : expected) {
            if (!actual.contains(product)) {
                msgList.add(pageName + ": expected item " + product + " not found, actual items " + actual);
                result = false;
            }
        }
        return result;
    }
}
