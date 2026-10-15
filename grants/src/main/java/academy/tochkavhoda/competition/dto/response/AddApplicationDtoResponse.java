package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class AddApplicationDtoResponse {

    private int applicationId;

    public AddApplicationDtoResponse() {
    }

    public AddApplicationDtoResponse(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
}
