package pageObject;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutOverviewPage_OR {

    @FindBy(css = "[data-test='cart-list'] [data-test='inventory-item']")
    private List<WebElement> orderItems;

    public List<WebElement> getOrderItems() {
        return orderItems;
    }

    @FindBy(css = "[data-test='payment-info-value']")
    private WebElement paymentInfo;

    public WebElement getPaymentInfo() {
        return paymentInfo;
    }

    @FindBy(css = "[data-test='shipping-info-value']")
    private WebElement shippingInfo;

    public WebElement getShippingInfo() {
        return shippingInfo;
    }

    @FindBy(css = "[data-test='subtotal-label']")
    private WebElement itemTotal;

    public WebElement getItemTotal() {
        return itemTotal;
    }

    @FindBy(css = "[data-test='tax-label']")
    private WebElement tax;

    public WebElement getTax() {
        return tax;
    }

    @FindBy(css = "[data-test='total-label']")
    private WebElement total;

    public WebElement getTotal() {
        return total;
    }

    @FindBy(id = "finish")
    private WebElement finishButton;

    public WebElement getFinishButton() {
        return finishButton;
    }

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    public WebElement getCancelButton() {
        return cancelButton;
    }
}
