package util;

import java.util.Arrays;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    int counter = 0;

    int retryLimit =Integer.parseInt(ConfigFileReader.retryCount);

    // API tests run against the shared public Restful-Booker server, where data can change or be reset
    // underneath a test - they get at least apiTestRetryCount retries
    int apiRetryLimit = new ConfigFileReader().getApiTestRetryCount();

    /*
     * (non-Javadoc)
     *
     * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
     *
     * This method decides how many times a test needs to be rerun. TestNg will call
     * this method every time a test fails. So we can put some code in here to
     * decide when to rerun the test.
     *
     * Note: This method will return true if a tests needs to be retried and false
     * it not.
     *
     */
    @Override
    public boolean retry(ITestResult result) {
        boolean isApiTest = Arrays.asList(result.getMethod().getGroups()).contains("api");
        int limit = isApiTest ? Math.max(retryLimit, apiRetryLimit) : retryLimit;
        if (counter < limit) {
            counter++;
            Log.warn("Retrying " + result.getMethod().getMethodName() + " (" + counter + "/" + limit + ") after: "
                    + result.getThrowable());
            return true;
        }
        return false;
    }

}
