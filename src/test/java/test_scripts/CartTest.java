package test_scripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.CartPageUtil;
import applicationUtil.HeaderComponentUtil;
import applicationUtil.InventoryPageUtil;
import pojo.Product;
import util.TestDataReader;
import util.TestDescriptionContant;

public class CartTest extends BaseTest {

    @Test(groups = { "regression", "cart" }, description = TestDescriptionContant.VERIFY_CART_DETAILS)
    public void verifyCartDetails() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        List<Product> expectedProducts = inventoryPageUtil.getProducts(TestDataReader.getProductNames("multiple"));
        Assert.assertTrue(inventoryPageUtil.addProductsAndOpenCart(expectedProducts), inventoryPageUtil.inventoryPageMsgList.toString());

        CartPageUtil cartPageUtil = new CartPageUtil(getDriver());
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());
        Assert.assertTrue(cartPageUtil.verifyCartDetails(expectedProducts), cartPageUtil.cartPageMsgList.toString());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), expectedProducts.size(), "Cart badge count on cart page");
    }

    @Test(groups = { "regression", "cart" }, description = TestDescriptionContant.VERIFY_REMOVE_FROM_CART)
    public void verifyRemoveProductFromCart() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        List<Product> products = inventoryPageUtil.getProducts(TestDataReader.getProductNames("multiple"));
        Assert.assertTrue(inventoryPageUtil.addProductsAndOpenCart(products), inventoryPageUtil.inventoryPageMsgList.toString());

        CartPageUtil cartPageUtil = new CartPageUtil(getDriver());
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());
        Product removed = products.remove(0);
        Assert.assertTrue(cartPageUtil.removeProduct(removed.getName()), cartPageUtil.cartPageMsgList.toString());
        Assert.assertTrue(cartPageUtil.verifyCartDetails(products), cartPageUtil.cartPageMsgList.toString());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), products.size(), "Cart badge after removing '" + removed.getName() + "'");
    }
}