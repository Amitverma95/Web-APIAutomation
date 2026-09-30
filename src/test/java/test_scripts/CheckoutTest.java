package test_scripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.CartPageUtil;
import applicationUtil.CheckoutInformationPageUtil;
import applicationUtil.CheckoutOverviewPageUtil;
import applicationUtil.InventoryPageUtil;
import dataProvider.TestDataProvider;
import pojo.CustomerDetails;
import pojo.Product;
import util.TestDataReader;
import util.TestDescriptionContant;

public class CheckoutTest extends BaseTest {

    @Test(groups = { "regression", "checkout", "negative" }, dataProvider = "invalidCheckoutData",
            dataProviderClass = TestDataProvider.class, description = TestDescriptionContant.VERIFY_CHECKOUT_MANDATORY_FIELDS)
    public void verifyCheckoutMandatoryFields(String testCase, CustomerDetails customer, String expectedError) {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        List<Product> products = inventoryPageUtil.getProducts(TestDataReader.getProductNames("single"));
        Assert.assertTrue(inventoryPageUtil.addProductsAndProceedToCheckout(products), inventoryPageUtil.inventoryPageMsgList.toString());

        CheckoutInformationPageUtil infoPageUtil = new CheckoutInformationPageUtil(getDriver());
        Assert.assertTrue(infoPageUtil.verifyCustomerDetailsError(customer, expectedError),
                testCase + ": " + infoPageUtil.checkoutInfoMsgList);
    }

    @Test(groups = { "regression", "checkout" }, description = TestDescriptionContant.VERIFY_CHECKOUT_OVERVIEW)
    public void verifyCheckoutOverview() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        List<Product> products = inventoryPageUtil.getProducts(TestDataReader.getProductNames("multiple"));
        Assert.assertTrue(inventoryPageUtil.addProductsAndProceedToCheckout(products), inventoryPageUtil.inventoryPageMsgList.toString());

        CheckoutInformationPageUtil infoPageUtil = new CheckoutInformationPageUtil(getDriver());
        Assert.assertTrue(infoPageUtil.continueWithCustomerDetails(TestDataReader.getCustomer("valid")),
                infoPageUtil.checkoutInfoMsgList.toString());

        CheckoutOverviewPageUtil overviewPageUtil = new CheckoutOverviewPageUtil(getDriver());
        Assert.assertTrue(overviewPageUtil.verifyOverview(products, TestDataReader.getTaxRate(),
                TestDataReader.getValue("checkout", "paymentInfo"), TestDataReader.getValue("checkout", "shippingInfo")),
                overviewPageUtil.checkoutOverviewMsgList.toString());
    }

    @Test(groups = { "regression", "checkout" }, description = TestDescriptionContant.VERIFY_CANCEL_CHECKOUT)
    public void verifyCancelCheckoutReturnsToCart() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        List<Product> products = inventoryPageUtil.getProducts(TestDataReader.getProductNames("single"));
        Assert.assertTrue(inventoryPageUtil.addProductsAndProceedToCheckout(products), inventoryPageUtil.inventoryPageMsgList.toString());

        CheckoutInformationPageUtil infoPageUtil = new CheckoutInformationPageUtil(getDriver());
        Assert.assertTrue(infoPageUtil.isCheckoutInformationPageDisplayed(), infoPageUtil.checkoutInfoMsgList.toString());
        Assert.assertTrue(infoPageUtil.cancelCheckout(), infoPageUtil.checkoutInfoMsgList.toString());

        CartPageUtil cartPageUtil = new CartPageUtil(getDriver());
        Assert.assertTrue(cartPageUtil.verifyCartDetails(products), "Cart should keep its items after cancel: " + cartPageUtil.cartPageMsgList);
    }
}