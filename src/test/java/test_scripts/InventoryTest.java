package test_scripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import applicationUtil.HeaderComponentUtil;
import applicationUtil.InventoryPageUtil;
import applicationUtil.ProductDetailsPageUtil;
import pojo.Product;
import util.TestDataReader;
import util.TestDescriptionContant;

public class InventoryTest extends BaseTest {

    @Test(groups = { "regression", "inventory" }, description = TestDescriptionContant.VERIFY_INVENTORY_PRODUCTS)
    public void verifyInventoryListsProducts() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        Assert.assertTrue(inventoryPageUtil.verifyAllProductsHaveNameAndPrice(), inventoryPageUtil.inventoryPageMsgList.toString());
    }

    @Test(groups = { "smoke", "regression", "inventory" }, description = TestDescriptionContant.VERIFY_PRODUCT_DETAILS)
    public void verifySelectProductOpensDetails() {
        String productName = TestDataReader.getProductNames("single").get(0);
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        Product listedProduct = inventoryPageUtil.getProduct(productName);

        inventoryPageUtil.openProductDetails(productName);
        ProductDetailsPageUtil detailsPageUtil = new ProductDetailsPageUtil(getDriver());
        Assert.assertTrue(detailsPageUtil.isProductDetailsPageDisplayed(), detailsPageUtil.productDetailsMsgList.toString());
        Assert.assertEquals(detailsPageUtil.getProduct(), listedProduct, "Details page should show same name and price as the list");
        Assert.assertFalse(detailsPageUtil.getDescription().isEmpty(), "Product description should not be empty");
    }

    @Test(groups = { "regression", "inventory", "cart" }, description = TestDescriptionContant.VERIFY_ADD_REMOVE_CART_BADGE)
    public void verifyAddAndRemoveProductUpdatesCartBadge() {
        List<String> products = TestDataReader.getProductNames("multiple");
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        HeaderComponentUtil headerUtil = new HeaderComponentUtil(getDriver());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), 0, "Cart badge should not be shown before adding products");

        for (int i = 0; i < products.size(); i++) {
            Assert.assertTrue(inventoryPageUtil.addProductToCart(products.get(i)), inventoryPageUtil.inventoryPageMsgList.toString());
            Assert.assertEquals(headerUtil.getCartBadgeCount(), i + 1, "Cart badge after adding '" + products.get(i) + "'");
        }

        Assert.assertTrue(inventoryPageUtil.removeProductFromCart(products.get(0)), inventoryPageUtil.inventoryPageMsgList.toString());
        Assert.assertEquals(headerUtil.getCartBadgeCount(), products.size() - 1, "Cart badge after removing '" + products.get(0) + "'");
    }

    @Test(groups = { "regression", "inventory" }, description = TestDescriptionContant.VERIFY_SORT_BY_PRICE)
    public void verifySortByPriceLowToHigh() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        inventoryPageUtil.sortBy("Price (low to high)");
        Assert.assertTrue(inventoryPageUtil.isSortedByPriceLowToHigh(), inventoryPageUtil.inventoryPageMsgList.toString());
    }

    @Test(groups = { "regression", "inventory" }, description = TestDescriptionContant.VERIFY_SORT_BY_NAME)
    public void verifySortByNameZToA() {
        InventoryPageUtil inventoryPageUtil = loginWithValidUser();
        inventoryPageUtil.sortBy("Name (Z to A)");
        Assert.assertTrue(inventoryPageUtil.isSortedByNameZToA(), inventoryPageUtil.inventoryPageMsgList.toString());
    }
}
