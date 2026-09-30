package pojo.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Success: {"token": "..."} - bad credentials: {"reason": "Bad credentials"} */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    private String token;
    private String reason;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
