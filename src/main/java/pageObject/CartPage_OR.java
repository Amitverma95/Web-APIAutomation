package pageObject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CartPage_OR {

    // A cart row (same markup on cart and checkout overview pages)
    public static final By CART_ROW = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item']");

    // Locators used inside a single cart row (same markup on cart and checkout overview pages)
    public static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    public static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    public static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    public static final By ITEM_REMOVE_BUTTON = By.cssSelector("button[data-test^='remove']");

    @FindBy(css = "[data-test='cart-list'] [data-test='inventory-item']")
    private List<WebElement> cartItems;

    public List<WebElement> getCartItems() {
        return cartItems;
    }

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    public WebElement getCheckoutButton() {
        return checkoutButton;
    }
}
