package apiUtil;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import util.ConfigFileReader;
import util.Log;

/**
 * Reusable request methods. Retries 5xx responses and connection errors (apiRetryCount in config)
 * because the public Restful-Booker server fails intermittently.
 */
public class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);
    private static final int RETRY_COUNT = new ConfigFileReader().getApiRetryCount();
    private static final Set<Integer> RETRYABLE_STATUS = Set.of(500, 502, 503, 504);
    private static final long RETRY_DELAY_MS = 1000;

    public static Response get(RequestSpecification request, String path) {
        return send(Method.GET, request, path);
    }

    public static Response post(RequestSpecification request, String path) {
        return send(Method.POST, request, path);
    }

    public static Response put(RequestSpecification request, String path) {
        return send(Method.PUT, request, path);
    }

    public static Response patch(RequestSpecification request, String path) {
        return send(Method.PATCH, request, path);
    }

    public static Response delete(RequestSpecification request, String path) {
        return send(Method.DELETE, request, path);
    }

    public static Response send(Method method, RequestSpecification request, String path) {
        return send(method, request, path, RETRY_COUNT);
    }

    /** retryCount 0 for negative tests that expect a 5xx (retrying them would only slow the run). */
    public static Response send(Method method, RequestSpecification request, String path, int retryCount) {
        Response response = null;
        RuntimeException lastError = null;
        for (int attempt = 0; attempt <= retryCount; attempt++) {
            if (attempt > 0) {
                Log.warn("Retry " + attempt + "/" + retryCount + " for " + method + " " + path
                        + (response != null ? " (last status " + response.getStatusCode() + ")" : " (" + lastError.getMessage() + ")"));
                sleep(RETRY_DELAY_MS * attempt);
            }
            try {
                response = RestAssured.given().spec(request).when().request(method, path);
                lastError = null;
                if (!RETRYABLE_STATUS.contains(response.getStatusCode())) {
                    return response;
                }
            } catch (RuntimeException e) {
                // connection reset / timeout
                log.warn("{} {} failed: {}", method, path, e.getMessage());
                lastError = e;
                response = null;
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        return response;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
