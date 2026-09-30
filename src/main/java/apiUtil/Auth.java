package apiUtil;

import io.restassured.specification.RequestSpecification;

/**
 * Authentication applied to a request. Restful-Booker accepts a token cookie or basic auth
 * for PUT / PATCH / DELETE.
 */
public class Auth {

    private final String description;
    private final boolean sharedToken;
    private final String token;
    private final String username;
    private final String password;

    private Auth(String description, boolean sharedToken, String token, String username, String password) {
        this.description = description;
        this.sharedToken = sharedToken;
        this.token = token;
        this.username = username;
        this.password = password;
    }

    /**
     * Token created from POST /auth with the configured admin user. The token is read when the request is
     * built, and BookingApiUtil refreshes it when the API rejects it (tokens expire after a few minutes).
     */
    public static Auth sharedToken() {
        return new Auth("Token", true, null, null, null);
    }

    /** A fixed token value - e.g. an invalid token for negative tests. */
    public static Auth token(String token) {
        return new Auth("Token", false, token, null, null);
    }

    public static Auth basic(String username, String password) {
        return new Auth("Basic auth", false, null, username, password);
    }

    public static Auth none() {
        return new Auth("No auth", false, null, null, null);
    }

    public boolean isSharedToken() {
        return sharedToken;
    }

    public RequestSpecification apply(RequestSpecification request) {
        if (sharedToken) {
            request.cookie("token", AuthApiUtil.getToken());
        } else if (token != null) {
            request.cookie("token", token);
        } else if (username != null) {
            request.auth().preemptive().basic(username, password);
        }
        return request;
    }

    @Override
    public String toString() {
        return description;
    }
}
