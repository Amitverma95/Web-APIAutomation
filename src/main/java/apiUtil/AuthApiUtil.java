package apiUtil;

import io.restassured.response.Response;
import pojo.auth.AuthRequest;
import pojo.auth.AuthResponse;
import util.APIEndPoint;
import util.ConfigFileReader;
import util.JsonUtil;
import util.Log;

public class AuthApiUtil {

    private static final ConfigFileReader config = new ConfigFileReader();
    private static String cachedToken;

    public Response createToken(AuthRequest authRequest) {
        Log.info("POST " + APIEndPoint.AUTH + " for user '" + authRequest.getUsername() + "'");
        return ApiClient.post(SpecBuilder.getRequestSpec().body(JsonUtil.toJson(authRequest)), APIEndPoint.AUTH);
    }

    /** Sends the body as-is (e.g. malformed JSON). */
    public Response createTokenWithRawBody(String rawBody) {
        Log.info("POST " + APIEndPoint.AUTH + " with raw body");
        return ApiClient.post(SpecBuilder.getRequestSpec().body(rawBody), APIEndPoint.AUTH);
    }

    public AuthRequest getValidCredentials() {
        return new AuthRequest(config.getApiUsername(), config.getApiPassword());
    }

    /**
     * Token for the configured admin user - created once and shared by all tests (thread safe).
     * Restful-Booker tokens expire after a few minutes - see refreshToken().
     */
    public static synchronized String getToken() {
        if (cachedToken == null) {
            AuthApiUtil auth = new AuthApiUtil();
            Response response = auth.createToken(auth.getValidCredentials());
            String token = response.getStatusCode() == 200 ? JsonUtil.fromJson(response, AuthResponse.class).getToken() : null;
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("Unable to create auth token, status " + response.getStatusCode()
                        + ", body: " + response.asString());
            }
            cachedToken = token;
        }
        return cachedToken;
    }

    /** Drops the rejected token and creates a new one. Threads that already refreshed it keep the new token. */
    public static synchronized String refreshToken(String rejectedToken) {
        if (cachedToken != null && cachedToken.equals(rejectedToken)) {
            Log.warn("Auth token was rejected (403) - creating a new token");
            cachedToken = null;
        }
        return getToken();
    }
}
