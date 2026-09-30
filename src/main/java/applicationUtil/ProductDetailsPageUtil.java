package applicationUtil;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import pageObject.ProductDetailsPage_OR;
import pojo.Product;
import util.Common_Function;
import util.Log;

public class ProductDetailsPageUtil {

    Common_Function cfObj;
    public List<String> productDetailsMsgList = new ArrayList<String>();
    ProductDetailsPage_OR productDetailsOr;

    public ProductDetailsPageUtil(WebDriver driver) {
        cfObj = new Common_Function(driver);
        productDetailsOr = new ProductDetailsPage_OR();
        PageFactory.initElements(driver, productDetailsOr);
    }

    public boolean isProductDetailsPageDisplayed() {
        if (!cfObj.waitForUrlContains("inventory-item.html")) {
            productDetailsMsgList.add("Product details page is not opened");
            return false;
        }
        return cfObj.isDisplayed(productDetailsOr.getProductName());
    }

    public Product getProduct() {
        String name = cfObj.getText(productDetailsOr.getProductName());
        return new Product(name, Common_Function.parsePrice(cfObj.getText(productDetailsOr.getProductPrice())));
    }

    public String getDescription() {
        return cfObj.getText(productDetailsOr.getProductDescription());
    }

    public boolean addToCart() {
        try {
            Log.info("Add product to cart from details page");
            cfObj.click(productDetailsOr.getAddToCartButton());
            if (!cfObj.isDisplayed(productDetailsOr.getRemoveButton())) {
                productDetailsMsgList.add("Remove button not shown after adding product to cart");
                return false;
            }
            return true;
        } catch (Exception e) {
            productDetailsMsgList.add("addToCart_Exception: " + e.getMessage());
            return false;
        }
    }

    public boolean backToProducts() {
        cfObj.click(productDetailsOr.getBackToProductsButton());
        if (!cfObj.waitForUrlContains("inventory.html")) {
            productDetailsMsgList.add("Did not return to inventory page");
            return false;
        }
        return true;
    }
}
