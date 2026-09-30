package pageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutCompletePage_OR {

    @FindBy(css = "[data-test='complete-header']")
    private WebElement completeHeader;

    public WebElement getCompleteHeader() {
        return completeHeader;
    }

    @FindBy(css = "[data-test='complete-text']")
    private WebElement completeText;

    public WebElement getCompleteText() {
        return completeText;
    }

    @FindBy(css = "[data-test='pony-express']")
    private WebElement ponyExpressImage;

    public WebElement getPonyExpressImage() {
        return ponyExpressImage;
    }
}
