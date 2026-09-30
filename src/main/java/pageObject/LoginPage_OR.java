package pageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage_OR {

    public static final By LOGIN_BUTTON = By.id("login-button");

    @FindBy(className = "login_logo")
    private WebElement loginLogo;

    public WebElement getLoginLogo() {
        return loginLogo;
    }

    @FindBy(id = "user-name")
    private WebElement usernameTextBox;

    public WebElement getUsernameTextBox() {

        return usernameTextBox;
    }

    @FindBy(id = "password")
    private WebElement passwordTextBox;

    public WebElement getPasswordTextBox() {

        return passwordTextBox;
    }

    @FindBy(id = "login-button")
    private WebElement loginButton;

    public WebElement getLoginButton() {

        return loginButton;
    }

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public WebElement getErrorMessage() {
        return errorMessage;
    }
}
