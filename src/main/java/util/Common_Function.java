package util;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common_Function {

    private static final Logger log = LoggerFactory.getLogger(Common_Function.class);
    private static final ConfigFileReader config = new ConfigFileReader();
    private static final int EXPLICIT_WAIT = config.getExplicitWait();

    public WebDriver driver;

    public Common_Function() {
    }

    public Common_Function(WebDriver driver) {
        this.driver = driver;
    }

    // ------------------------------------------------------------------
    // Driver
    // ------------------------------------------------------------------

    /**
     * Starts a new browser session and opens the given url. Returns a new driver on every call and
     * does not store it in a shared field, so it is safe to call from parallel test threads.
     */
    public WebDriver initDriver(String strBaseUrl) {
        String browser = ConfigFileReader.strBrowser.isEmpty() ? "chrome" : ConfigFileReader.strBrowser.toLowerCase();
        log.info("Launching browser '{}' (headless={}) for {}", browser, ConfigFileReader.isHeadless(), strBaseUrl);

        WebDriver localDriver;
        try {
            Capabilities options = buildOptions(browser);
            if (ConfigFileReader.runOnDocker.equalsIgnoreCase("true")) {
                localDriver = new RemoteWebDriver(new URL(ConfigFileReader.dockerUrl), options);
            } else if (browser.equals("firefox")) {
                localDriver = new FirefoxDriver((FirefoxOptions) options);
            } else if (browser.equals("edge")) {
                localDriver = new EdgeDriver((EdgeOptions) options);
            } else {
                localDriver = new ChromeDriver((ChromeOptions) options);
            }
        } catch (Exception e) {
            log.error("Failed to initialize driver", e);
            throw new RuntimeException("Failed to initialize '" + browser + "' driver: " + e.getMessage(), e);
        }

        // headless has no screen to maximize to - it keeps --window-size=1920,1080 instead
        if (!ConfigFileReader.isHeadless()) {
            localDriver.manage().window().maximize();
        }
        localDriver.manage().deleteAllCookies();
        // Framework relies on explicit waits; keep implicitWait=0 in config to avoid mixing the two
        localDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        localDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        log.info("Navigating to: {}", strBaseUrl);
        localDriver.get(strBaseUrl);
        log.info("Driver initialized successfully");
        return localDriver;
    }

    private Capabilities buildOptions(String browser) throws IOException {
        if (browser.equals("firefox")) {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            if (ConfigFileReader.isHeadless()) {
                firefoxOptions.addArguments("-headless");
            }
            return firefoxOptions;
        }
        if (browser.equals("edge")) {
            return applyChromiumOptions(new EdgeOptions());
        }
        ChromeOptions chromeOptions = new ChromeOptions();
        if (browser.equals("brave")) {
            chromeOptions.setBinary("/Applications/Brave Browser.app/Contents/MacOS/Brave Browser");
        }
        return applyChromiumOptions(chromeOptions);
    }

    private <T extends ChromiumOptions<?>> T applyChromiumOptions(T options) throws IOException {
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("use-fake-ui-for-media-stream");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--no-first-run");
        options.addArguments("--deny-permission-prompts");
        options.addArguments("--disable-features=PasswordLeakDetection,PasswordManagerOnboarding");

        String downloadDir = System.getProperty("user.dir") + File.separator + "target" + File.separator + "downloads";
        new File(downloadDir).mkdirs();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.notifications", 2);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("download.default_directory", downloadDir);
        prefs.put("safebrowsing.enabled", true);
        // Stop Chrome's "change your password" / save-password popups from blocking clicks after login
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        // Separate profile per session so parallel browsers don't share state
        Path tempProfile = Files.createTempDirectory("chrome-profile-");
        options.addArguments("--user-data-dir=" + tempProfile.toAbsolutePath());

        if (ConfigFileReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.setExperimentalOption("useAutomationExtension", false);
            options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        }
        return options;
    }

    // ------------------------------------------------------------------
    // Waits
    // ------------------------------------------------------------------

    /** Explicit wait (explicitWait from config) that tolerates React re-rendering elements. */
    public FluentWait<WebDriver> getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT))
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitForVisible(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForVisible(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForAllVisible(List<WebElement> elements) {
        return getWait().until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public boolean waitForUrlContains(String fragment) {
        try {
            return getWait().until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException e) {
            log.warn("Url did not contain '{}' within {}s, current url: {}", fragment, EXPLICIT_WAIT, driver.getCurrentUrl());
            return false;
        }
    }

    public boolean commonWaitForElementToBeVisible(WebElement element, int timeOutInSeconds) {
        try {
            // visibilityOf already checks isDisplayed inside the wait - calling it again outside
            // the wait can hit a stale element when React re-renders
            new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds))
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            log.warn("Element is not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isDisplayed(WebElement element) {
        return commonWaitForElementToBeVisible(element, 5);
    }

    /** Immediate check without waiting - use for elements that may legitimately be absent (e.g. empty cart badge). */
    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------

    // If React re-renders the element (stale element), the action is tried once more instead of failing

    public void click(WebElement element) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (StaleElementReferenceException e) {
            getWait().until(ExpectedConditions.elementToBeClickable(element)).click();
        }
        log.debug("Clicked on element: {}", element);
    }

    public void type(WebElement element, String text) {
        try {
            WebElement el = waitForVisible(element);
            el.clear();
            el.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            WebElement el = waitForVisible(element);
            el.clear();
            el.sendKeys(text);
        }
        log.debug("Typed '{}' into element {}", text, element);
    }

    public String getText(WebElement element) {
        try {
            return waitForVisible(element).getText().trim();
        } catch (StaleElementReferenceException e) {
            return waitForVisible(element).getText().trim();
        }
    }

    public void selectByVisibleText(WebElement element, String text) {
        new Select(waitForVisible(element)).selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown {}", text, element);
    }

    /** "$29.99", "Item total: $29.99" or "Tax: $2.40" -> 29.99 / 2.40 */
    public static BigDecimal parsePrice(String text) {
        return new BigDecimal(text.replaceAll("[^0-9.]", ""));
    }

}
