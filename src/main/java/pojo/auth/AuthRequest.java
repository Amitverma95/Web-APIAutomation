package pojo.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Null fields are left out - used to send requests with a missing username/password. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {

    private String username;
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
