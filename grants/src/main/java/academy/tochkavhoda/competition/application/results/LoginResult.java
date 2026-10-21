package academy.tochkavhoda.competition.application.results;

import java.util.*;

public class LoginResult {

    private String token;

    public LoginResult() {
    }

    public LoginResult(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
