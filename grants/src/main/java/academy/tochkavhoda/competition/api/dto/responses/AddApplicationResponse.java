package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;

public class AddApplicationResponse {

    private int applicationId;

    public AddApplicationResponse() {
    }

    public AddApplicationResponse(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
}
