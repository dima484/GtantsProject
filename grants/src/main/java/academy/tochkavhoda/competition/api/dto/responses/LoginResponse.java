package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;
import academy.tochkavhoda.competition.application.results.LoginResult;

public class LoginResponse {

    private String token;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static LoginResponse from(LoginResult r) {
        return new LoginResponse(r.getToken());
    }
}
