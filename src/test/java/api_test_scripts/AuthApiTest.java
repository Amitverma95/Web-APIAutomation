package api_test_scripts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.Assert;
import org.testng.annotations.Test;

import apiUtil.SpecBuilder;
import dataProvider.ApiDataProvider;
import io.restassured.response.Response;
import pojo.auth.AuthRequest;
import pojo.auth.AuthResponse;
import util.JsonUtil;
import util.TestDescriptionContant;

public class AuthApiTest extends BaseApiTest {

    @Test(groups = { "api", "smoke", "regression", "auth" }, description = TestDescriptionContant.API_CREATE_TOKEN)
    public void verifyCreateTokenWithValidCredentials() {
        Response response = authApi.createToken(authApi.getValidCredentials());

        response.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/auth-token-schema.json"));
        AuthResponse authResponse = JsonUtil.fromJson(response, AuthResponse.class);
        Assert.assertNotNull(authResponse.getToken(), "Token should be returned");
        Assert.assertTrue(authResponse.getToken().matches("[a-f0-9]{15}"), "Unexpected token format: " + authResponse.getToken());
        Assert.assertNull(authResponse.getReason(), "Successful auth should not return a failure reason");
    }

    @Test(groups = { "api", "regression", "auth", "negative" }, dataProvider = "invalidCredentials",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_CREATE_TOKEN_INVALID)
    public void verifyCreateTokenWithInvalidCredentials(String testCase, AuthRequest credentials) {
        Response response = authApi.createToken(credentials);

        // Restful-Booker answers bad credentials with 200 + reason instead of 401
        response.then().spec(SpecBuilder.getJsonResponseSpec(200));
        AuthResponse authResponse = JsonUtil.fromJson(response, AuthResponse.class);
        Assert.assertNull(authResponse.getToken(), testCase + ": no token should be issued");
        Assert.assertEquals(authResponse.getReason(), "Bad credentials", testCase + ": failure reason");
    }

    @Test(groups = { "api", "regression", "auth", "negative" }, description = TestDescriptionContant.API_CREATE_TOKEN_MALFORMED)
    public void verifyCreateTokenWithMalformedBody() {
        Response response = authApi.createTokenWithRawBody("{\"username\": \"admin\", \"password\": ");

        response.then().spec(SpecBuilder.getStatusResponseSpec(400));
        Assert.assertEquals(response.asString(), "Bad Request", "Error body");
    }
}
