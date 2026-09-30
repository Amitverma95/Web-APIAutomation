package pageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProductDetailsPage_OR {

    @FindBy(css = "[data-test='inventory-item-name']")
    private WebElement productName;

    public WebElement getProductName() {
        return productName;
    }

    @FindBy(css = "[data-test='inventory-item-desc']")
    private WebElement productDescription;

    public WebElement getProductDescription() {
        return productDescription;
    }

    @FindBy(css = "[data-test='inventory-item-price']")
    private WebElement productPrice;

    public WebElement getProductPrice() {
        return productPrice;
    }

    @FindBy(id = "add-to-cart")
    private WebElement addToCartButton;

    public WebElement getAddToCartButton() {
        return addToCartButton;
    }

    @FindBy(id = "remove")
    private WebElement removeButton;

    public WebElement getRemoveButton() {
        return removeButton;
    }

    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;

    public WebElement getBackToProductsButton() {
        return backToProductsButton;
    }
}
