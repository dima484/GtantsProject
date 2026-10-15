package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class RateApplicationDtoRequest {

    private int applicationId;
    private int rating;

    public RateApplicationDtoRequest() {
    }

    public RateApplicationDtoRequest(int applicationId, int rating) {
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
