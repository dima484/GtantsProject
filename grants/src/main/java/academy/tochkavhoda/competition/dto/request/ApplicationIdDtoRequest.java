package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class ApplicationIdDtoRequest {

    private int applicationId;

    public ApplicationIdDtoRequest() {
    }

    public ApplicationIdDtoRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
}
