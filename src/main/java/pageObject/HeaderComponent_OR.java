package pageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Header shown on every page after login - title, cart and side menu.
 */
public class HeaderComponent_OR {

    // Badge is removed from the DOM when the cart is empty, so it is looked up with a By
    public static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    @FindBy(css = "[data-test='title']")
    private WebElement pageTitle;

    public WebElement getPageTitle() {
        return pageTitle;
    }

    @FindBy(css = "[data-test='shopping-cart-link']")
    private WebElement cartLink;

    public WebElement getCartLink() {
        return cartLink;
    }

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    public WebElement getMenuButton() {
        return menuButton;
    }

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public WebElement getLogoutLink() {
        return logoutLink;
    }
}
