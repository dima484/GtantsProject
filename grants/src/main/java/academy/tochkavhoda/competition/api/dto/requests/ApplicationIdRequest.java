package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class ApplicationIdRequest {

    private int applicationId;

    public ApplicationIdRequest() {
    }

    public ApplicationIdRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
}
