package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class GrantDtoResponse {

    private int applicationId;
    private String title;
    private String firmName;
    private long amount;
    private double averageRating;

    public GrantDtoResponse() {
    }

    public GrantDtoResponse(int applicationId, String title, String firmName, long amount, double averageRating) {
        this.applicationId = applicationId;
        this.title = title;
        this.firmName = firmName;
        this.amount = amount;
        this.averageRating = averageRating;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
