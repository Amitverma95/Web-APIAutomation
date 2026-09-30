package applicationUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import pageObject.InventoryPage_OR;
import pojo.Product;
import util.Common_Function;
import util.Log;

public class InventoryPageUtil {

    Common_Function cfObj;
    public List<String> inventoryPageMsgList = new ArrayList<String>();
    InventoryPage_OR inventoryPageOr;
    HeaderComponentUtil headerUtil;

    public InventoryPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        inventoryPageOr = new InventoryPage_OR();
        PageFactory.initElements(driver, inventoryPageOr);
        headerUtil = new HeaderComponentUtil(driver);
    }

    public boolean isInventoryPageDisplayed() {
        try {
            cfObj.waitForVisible(inventoryPageOr.getInventoryList());
            if (!headerUtil.isPageTitle("Products")) {
                inventoryPageMsgList.addAll(headerUtil.headerMsgList);
                return false;
            }
            if (inventoryPageOr.getInventoryItems().isEmpty()) {
                inventoryPageMsgList.add("No products listed on inventory page");
                return false;
            }
            return true;
        } catch (Exception e) {
            inventoryPageMsgList.add("Inventory page is not displayed: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement name : cfObj.waitForAllVisible(inventoryPageOr.getProductNames())) {
            names.add(name.getText().trim());
        }
        return names;
    }

    public List<BigDecimal> getAllProductPrices() {
        List<BigDecimal> prices = new ArrayList<>();
        for (WebElement price : cfObj.waitForAllVisible(inventoryPageOr.getProductPrices())) {
            prices.add(Common_Function.parsePrice(price.getText()));
        }
        return prices;
    }

    /** Every listed product should have a non empty name and a price greater than 0. */
    public boolean verifyAllProductsHaveNameAndPrice() {
        List<String> names = getAllProductNames();
        List<BigDecimal> prices = getAllProductPrices();
        if (names.isEmpty()) {
            inventoryPageMsgList.add("Inventory does not list any product");
            return false;
        }
        if (names.size() != prices.size()) {
            inventoryPageMsgList.add("Product count " + names.size() + " does not match price count " + prices.size());
            return false;
        }
        boolean result = true;
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).isEmpty()) {
                inventoryPageMsgList.add("Product at position " + (i + 1) + " has no name");
                result = false;
            }
            if (prices.get(i).compareTo(BigDecimal.ZERO) <= 0) {
                inventoryPageMsgList.add("Price of '" + names.get(i) + "' should be > 0 but is " + prices.get(i));
                result = false;
            }
        }
        return result;
    }

    public boolean isSortedByPriceLowToHigh() {
        List<BigDecimal> actualPrices = getAllProductPrices();
        List<BigDecimal> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);
        if (!actualPrices.equals(expectedPrices)) {
            inventoryPageMsgList.add("Products not sorted by price low to high. Expected " + expectedPrices + " but found " + actualPrices);
            return false;
        }
        return true;
    }

    public boolean isSortedByNameZToA() {
        List<String> actualNames = getAllProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(Collections.reverseOrder());
        if (!actualNames.equals(expectedNames)) {
            inventoryPageMsgList.add("Products not sorted by name Z to A. Expected " + expectedNames + " but found " + actualNames);
            return false;
        }
        return true;
    }

    private WebElement getProductCard(String productName) {
        try {
            return cfObj.waitForVisible(InventoryPage_OR.productCard(productName));
        } catch (Exception e) {
            throw new NoSuchElementException("Product '" + productName + "' not found on inventory page");
        }
    }

    /** Name and price of a product as shown on the inventory list. */
    public Product getProduct(String productName) {
        WebElement card = getProductCard(productName);
        String name = card.findElement(InventoryPage_OR.PRODUCT_NAME).getText().trim();
        BigDecimal price = Common_Function.parsePrice(card.findElement(InventoryPage_OR.PRODUCT_PRICE).getText());
        return new Product(name, price);
    }

    /** Name and price of each product as listed on the inventory page. */
    public List<Product> getProducts(List<String> productNames) {
        List<Product> products = new ArrayList<>();
        for (String productName : productNames) {
            products.add(getProduct(productName));
        }
        return products;
    }

    public String getProductButtonText(String productName) {
        return getProductCard(productName).findElement(InventoryPage_OR.PRODUCT_BUTTON).getText().trim();
    }

    /** Select a product - opens its details page. */
    public void openProductDetails(String productName) {
        Log.info("Select product '" + productName + "'");
        cfObj.click(getProductCard(productName).findElement(InventoryPage_OR.PRODUCT_NAME));
        cfObj.waitForUrlContains("inventory-item.html");
    }

    public boolean addProductToCart(String productName) {
        Log.info("Add '" + productName + "' to cart from inventory page");
        return clickProductButton(productName, "Add to cart", "Remove");
    }

    /** Adds every product with its "Add to cart" button on the inventory page. */
    public boolean addProductsToCart(List<Product> products) {
        for (Product product : products) {
            if (!addProductToCart(product.getName())) {
                return false;
            }
        }
        return true;
    }

    /** Adds the products and opens the cart page. */
    public boolean addProductsAndOpenCart(List<Product> products) {
        if (!addProductsToCart(products)) {
            return false;
        }
        if (!headerUtil.openCart()) {
            inventoryPageMsgList.addAll(headerUtil.headerMsgList);
            return false;
        }
        CartPageUtil cartPageUtil = new CartPageUtil(cfObj.driver);
        if (!cartPageUtil.isCartPageDisplayed()) {
            inventoryPageMsgList.addAll(cartPageUtil.cartPageMsgList);
            return false;
        }
        return true;
    }

    /** Adds the products, opens the cart and proceeds to the checkout information page. */
    public boolean addProductsAndProceedToCheckout(List<Product> products) {
        if (!addProductsAndOpenCart(products)) {
            return false;
        }
        CartPageUtil cartPageUtil = new CartPageUtil(cfObj.driver);
        if (!cartPageUtil.proceedToCheckout()) {
            inventoryPageMsgList.addAll(cartPageUtil.cartPageMsgList);
            return false;
        }
        return true;
    }

    /** Opens each product, checks the details page matches the list, adds it to cart and returns to inventory. */
    public boolean addProductsFromDetailsPage(List<Product> products) {
        ProductDetailsPageUtil detailsPageUtil = new ProductDetailsPageUtil(cfObj.driver);
        for (Product product : products) {
            openProductDetails(product.getName());
            if (!detailsPageUtil.isProductDetailsPageDisplayed()) {
                inventoryPageMsgList.addAll(detailsPageUtil.productDetailsMsgList);
                return false;
            }
            Product detailsProduct = detailsPageUtil.getProduct();
            if (!product.equals(detailsProduct)) {
                inventoryPageMsgList.add("Details page shows " + detailsProduct + " but list shows " + product);
                return false;
            }
            if (!detailsPageUtil.addToCart() || !detailsPageUtil.backToProducts()) {
                inventoryPageMsgList.add("Failed for '" + product.getName() + "': " + detailsPageUtil.productDetailsMsgList);
                return false;
            }
        }
        return true;
    }

    public boolean removeProductFromCart(String productName) {
        Log.info("Remove '" + productName + "' from cart on inventory page");
        return clickProductButton(productName, "Remove", "Add to cart");
    }

    private boolean clickProductButton(String productName, String expectedBefore, String expectedAfter) {
        try {
            String before = getProductButtonText(productName);
            if (!before.equals(expectedBefore)) {
                inventoryPageMsgList.add("Button for '" + productName + "' shows '" + before + "' instead of '" + expectedBefore + "'");
                return false;
            }
            cfObj.click(getProductCard(productName).findElement(InventoryPage_OR.PRODUCT_BUTTON));
            boolean changed = cfObj.getWait().until(d -> getProductButtonText(productName).equals(expectedAfter));
            if (!changed) {
                inventoryPageMsgList.add("Button for '" + productName + "' did not change to '" + expectedAfter + "'");
            }
            return changed;
        } catch (Exception e) {
            inventoryPageMsgList.add("clickProductButton_Exception for '" + productName + "': " + e.getMessage());
            return false;
        }
    }

    public void sortBy(String sortOption) {
        Log.info("Sort products by '" + sortOption + "'");
        cfObj.selectByVisibleText(inventoryPageOr.getSortDropdown(), sortOption);
    }
}
