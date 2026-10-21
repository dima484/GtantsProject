package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class RateApplicationRequest {

    private int applicationId;
    private int rating;

    public RateApplicationRequest() {
    }

    public RateApplicationRequest(int applicationId, int rating) {
        this.applicationId = applicationId;
        this.rating = rating;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
