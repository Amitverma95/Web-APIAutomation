package pageObject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InventoryPage_OR {

    // Locators used inside a single product card
    public static final By PRODUCT_NAME = By.cssSelector("[data-test='inventory-item-name']");
    public static final By PRODUCT_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    public static final By PRODUCT_BUTTON = By.cssSelector("button");

    /** Product card located by its visible name, e.g. "Sauce Labs Backpack". */
    public static By productCard(String productName) {
        return By.xpath(productCardXpath(productName));
    }

    /** "Add to cart" / "Remove" button of a product card. React replaces it on click, so waits look it up again. */
    public static By productButton(String productName) {
        return By.xpath(productCardXpath(productName) + "//button");
    }

    private static String productCardXpath(String productName) {
        return "//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()="
                + xpathLiteral(productName) + "]]";
    }

    private static String xpathLiteral(String value) {
        return value.contains("'") ? "\"" + value + "\"" : "'" + value + "'";
    }

    @FindBy(css = "[data-test='inventory-list']")
    private WebElement inventoryList;

    public WebElement getInventoryList() {
        return inventoryList;
    }

    @FindBy(css = "[data-test='inventory-item']")
    private List<WebElement> inventoryItems;

    public List<WebElement> getInventoryItems() {
        return inventoryItems;
    }

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> productNames;

    public List<WebElement> getProductNames() {
        return productNames;
    }

    @FindBy(css = "[data-test='inventory-item-price']")
    private List<WebElement> productPrices;

    public List<WebElement> getProductPrices() {
        return productPrices;
    }

    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdown;

    public WebElement getSortDropdown() {
        return sortDropdown;
    }
}
