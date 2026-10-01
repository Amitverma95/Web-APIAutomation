package test_scripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.CartPageUtil;
import applicationUtil.CheckoutCompletePageUtil;
import applicationUtil.CheckoutInformationPageUtil;
import applicationUtil.CheckoutOverviewPageUtil;
import applicationUtil.HeaderComponentUtil;
import applicationUtil.InventoryPageUtil;
import dataProvider.TestDataProvider;
import pojo.Product;
import util.Log;
import util.TestDataReader;
import util.TestDescriptionContant;

public class EndToEndPurchaseTest extends BaseTest {


    @Test(groups = { "smoke", "regression", "e2e" }, dataProvider = "purchaseData", dataProviderClass = TestDataProvider.class,
            description = TestDescriptionContant.VERIFY_END_TO_END_PURCHASE)
    public void verifyEndToEndPurchase(String scenario, List<String> productNames) {
        Log.info("Scenario: " + scenario + " - " + productNames);

        // 1-3. Launch application (BaseTest), login and verify successful login
        loginWithValidUser();
        InventoryPageUtil inventoryPageUtil = new InventoryPageUtil(getDriver());
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());

        // 4-5. Select each product, verify its details and add it to the cart
        List<Product> products = inventoryPageUtil.getProducts(productNames);
        Assert.assertTrue(inventoryPageUtil.addProductsFromDetailsPage(products), inventoryPageUtil.inventoryPageMsgList.toString());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), products.size(), "Cart badge count after adding products");

        // 6. Verify the cart details
        Assert.assertTrue(headerUtil.openCart(), headerUtil.headerMsgList.toString());
        CartPageUtil cartPageUtil = new CartPageUtil(getDriver());
        Assert.assertTrue(cartPageUtil.isCartPageDisplayed(), cartPageUtil.cartPageMsgList.toString());
        Assert.assertTrue(cartPageUtil.verifyCartDetails(products), cartPageUtil.cartPageMsgList.toString());

        // 7. Proceed to checkout
        Assert.assertTrue(cartPageUtil.proceedToCheckout(), cartPageUtil.cartPageMsgList.toString());
        CheckoutInformationPageUtil infoPageUtil = new CheckoutInformationPageUtil(getDriver());
        Assert.assertTrue(infoPageUtil.isCheckoutInformationPageDisplayed(), infoPageUtil.checkoutInfoMsgList.toString());

        // 8. Enter customer / shipping details
        Assert.assertTrue(infoPageUtil.continueWithCustomerDetails(TestDataReader.getCustomer("valid")),
                infoPageUtil.checkoutInfoMsgList.toString());

        // 9. Verify the order summary and complete the checkout
        CheckoutOverviewPageUtil overviewPageUtil = new CheckoutOverviewPageUtil(getDriver());
        Assert.assertTrue(overviewPageUtil.verifyOverview(products, TestDataReader.getTaxRate(),
                TestDataReader.getValue("checkout", "paymentInfo"), TestDataReader.getValue("checkout", "shippingInfo")),
                overviewPageUtil.checkoutOverviewMsgList.toString());
        Assert.assertTrue(overviewPageUtil.finishOrder(), overviewPageUtil.checkoutOverviewMsgList.toString());

        // 10. Verify the order confirmation
        CheckoutCompletePageUtil completePageUtil = new CheckoutCompletePageUtil(getDriver());
        Assert.assertTrue(completePageUtil.verifyOrderConfirmation(TestDataReader.getValue("checkout", "confirmationHeader"),
                TestDataReader.getValue("checkout", "confirmationText")), completePageUtil.checkoutCompleteMsgList.toString());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), 0, "Cart should be empty after the order is placed");
        Log.info("Order placed successfully for scenario: " + scenario);
    }
}