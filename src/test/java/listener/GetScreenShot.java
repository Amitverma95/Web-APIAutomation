package listener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

import util.Constant;

public class GetScreenShot {

    /** Saves a screenshot to ErrorScreenshots/ and returns its absolute path (null if it could not be taken). */
    public static String capture(WebDriver driver, String screenShotName) {
        if (driver == null) {
            System.err.println("Driver is null, cannot capture screenshot");
            return null;
        }
        try {
            // Check if session is still active
            if (((RemoteWebDriver) driver).getSessionId() == null) {
                System.err.println("Driver session is already closed, skipping screenshot");
                return null;
            }

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // timestamp + thread id keep names unique for retries and parallel runs
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String dest = Constant.SCREENSHOT_DIR + screenShotName + "_" + timestamp + "_" + Thread.currentThread().getId() + ".png";
            FileUtils.copyFile(source, new File(dest));
            return dest;
        } catch (WebDriverException e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
