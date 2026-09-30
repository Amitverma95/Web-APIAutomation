package apiUtil;

import static org.hamcrest.Matchers.lessThan;

import java.util.concurrent.TimeUnit;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import util.ConfigFileReader;

/**
 * Request / response specifications shared by every API call.
 * Specs are built per call (no global RestAssured state) so tests can run in parallel.
 */
public class SpecBuilder {

    private static final ConfigFileReader config = new ConfigFileReader();

    /** JSON in / JSON out request with base url and logging. */
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                // exact value - RestAssured's ContentType.JSON sends a list of JSON types and the API answers 418
                .setAccept("application/json")
                .addFilter(new ApiLogFilter())
                .build();
    }

    /** Request without default content type / accept headers - used to send unsupported headers or raw bodies. */
    public static RequestSpecification getBaseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.getApiBaseUrl())
                .addFilter(new ApiLogFilter())
                .build();
    }

    /** Successful JSON response within the configured time limit. */
    public static ResponseSpecification getJsonResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(config.getApiResponseTimeLimitMs()), TimeUnit.MILLISECONDS)
                .build();
    }

    /** Error / plain text response (the API answers errors with text like "Forbidden"). */
    public static ResponseSpecification getStatusResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectResponseTime(lessThan(config.getApiResponseTimeLimitMs()), TimeUnit.MILLISECONDS)
                .build();
    }
}
