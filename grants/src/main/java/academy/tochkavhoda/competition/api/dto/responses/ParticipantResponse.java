package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;
import academy.tochkavhoda.competition.application.results.ParticipantResult;

public class ParticipantResponse {

    private String firstName;
    private String lastName;
    private String firmName;

    public ParticipantResponse() {
    }

    public ParticipantResponse(String firstName, String lastName, String firmName) {
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

    public static ParticipantResponse from(ParticipantResult r) {
        return new ParticipantResponse(r.getFirstName(), r.getLastName(), r.getFirmName());
    }
}
