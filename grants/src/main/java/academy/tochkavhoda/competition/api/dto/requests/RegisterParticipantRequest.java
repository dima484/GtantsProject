package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class RegisterParticipantRequest {

    private String firstName;
    private String lastName;
    private String firmName;
    private String login;
    private String password;

    public RegisterParticipantRequest() {
    }

    public RegisterParticipantRequest(String firstName, String lastName, String firmName, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.firmName = firmName;
        this.login = login;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
