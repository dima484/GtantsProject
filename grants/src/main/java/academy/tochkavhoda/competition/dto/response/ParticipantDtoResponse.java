package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ParticipantDtoResponse {

    private String firstName;
    private String lastName;
    private String firmName;

    public ParticipantDtoResponse() {
    }

    public ParticipantDtoResponse(String firstName, String lastName, String firmName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.firmName = firmName;
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
}
