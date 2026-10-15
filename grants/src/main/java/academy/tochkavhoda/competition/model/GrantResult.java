package academy.tochkavhoda.competition.model;

import java.util.*;

public class GrantResult {

    private Application application;
    private double averageRating;

    public GrantResult() {
    }

    public GrantResult(Application application, double averageRating) {
        this.application = application;
        this.averageRating = averageRating;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
