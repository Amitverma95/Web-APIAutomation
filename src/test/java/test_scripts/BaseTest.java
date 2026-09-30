package test_scripts;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import applicationUtil.InventoryPageUtil;
import applicationUtil.LoginPageUtil;
import listener.TestListener;
import util.Common_Function;
import util.ConfigFileReader;
import util.Log;

// Listener attached here (not only in suite xml) so reports/screenshots also work for single test runs
@Listeners(TestListener.class)
public class BaseTest {

    // One browser per test method and thread - keeps tests independent and parallel safe
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected ConfigFileReader configReaderObj = new ConfigFileReader();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver localDriver = new Common_Function().initDriver(configReaderObj.getBaseUrlWeb());
        driver.set(localDriver);
        System.out.println("Session created: " + localDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver drv = driver.get();
        if (drv != null) {
            try {
                drv.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            } finally {
                driver.remove();
            }
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }

    /** Common precondition - login with the user from config and land on the inventory page. */
    protected InventoryPageUtil loginWithValidUser() {
        LoginPageUtil loginPageUtil = new LoginPageUtil(getDriver());
        boolean result = loginPageUtil.verifyLogin(configReaderObj.getUsername(), configReaderObj.getPassword(),
                configReaderObj.getInventoryUrl());
        Assert.assertTrue(result, "Precondition failed - login: " + loginPageUtil.loginPageMsgList);
        Log.info("Precondition: logged in as '" + configReaderObj.getUsername() + "'");
        return new InventoryPageUtil(getDriver());
    }
}
