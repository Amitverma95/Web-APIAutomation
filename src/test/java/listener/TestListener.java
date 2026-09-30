package listener;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import test_scripts.BaseTest;
import util.ExtentManager;

public class TestListener implements ITestListener {

    // Extent Report Declarations
    private static ExtentReports extent = ExtentManager.createInstance();

    @Override
    public synchronized void onStart(ITestContext context) {
        System.out.println("Test Suite started!");
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        System.out.println(("Test Suite is ending!"));
        extent.flush();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " started!"));
        ExtentManager.setTest(createTest(result));
    }

    private ExtentTest createTest(ITestResult result) {
        String name = result.getMethod().getMethodName();
        // Show data provider values (e.g. "Locked out user") in the report name
        if (result.getParameters().length > 0) {
            name += " [" + result.getParameters()[0] + "]";
        }
        ExtentTest extentTest = extent.createTest(name, result.getMethod().getDescription());
        extentTest.assignCategory(result.getMethod().getGroups());
        extentTest.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        return extentTest;
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " passed!"));
        ExtentManager.getTest().pass("Test passed");
        ExtentManager.removeTest();
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        String path = null;
        String strSessionId = null;
        WebDriver driver = null;
        ExtentTest test = ExtentManager.getTest() != null ? ExtentManager.getTest() : createTest(result);

        try {
            Object testInstance = result.getInstance();
            if (testInstance instanceof BaseTest) {
                driver = ((BaseTest) testInstance).getDriver();
            }

            System.out.println(result.getMethod().getMethodName() + " failed!");

            if (driver != null && ((RemoteWebDriver) driver).getSessionId() != null) {
                strSessionId = ((RemoteWebDriver) driver).getSessionId().toString();
                path = GetScreenShot.capture(driver, result.getMethod().getMethodName());
            } else {
                System.err.println("No active WebDriver session, skipping screenshot");
            }

            test.fail(result.getThrowable());
            if (path != null) {
                // Report lives in TestReport/, screenshots in ErrorScreenshots/ - use a relative link
                String relativePath = "../ErrorScreenshots/" + new File(path).getName();
                test.fail("Screenshot on failure", MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }
            if (strSessionId != null) {
                test.fail("Session ID: " + strSessionId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            test.fail(e.getMessage());
        } finally {
            ExtentManager.removeTest();
        }
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " skipped!"));
        ExtentTest test = ExtentManager.getTest() != null ? ExtentManager.getTest() : createTest(result);
        if (result.getThrowable() != null) {
            test.skip(result.getThrowable());
        } else {
            test.skip("Test skipped");
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
    }

}
