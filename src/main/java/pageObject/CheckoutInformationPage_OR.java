package pageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutInformationPage_OR {

    @FindBy(id = "first-name")
    private WebElement firstNameTextBox;

    public WebElement getFirstNameTextBox() {
        return firstNameTextBox;
    }

    @FindBy(id = "last-name")
    private WebElement lastNameTextBox;

    public WebElement getLastNameTextBox() {
        return lastNameTextBox;
    }

    @FindBy(id = "postal-code")
    private WebElement postalCodeTextBox;

    public WebElement getPostalCodeTextBox() {
        return postalCodeTextBox;
    }

    @FindBy(id = "continue")
    private WebElement continueButton;

    public WebElement getContinueButton() {
        return continueButton;
    }

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    public WebElement getCancelButton() {
        return cancelButton;
    }

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public WebElement getErrorMessage() {
        return errorMessage;
    }
}
