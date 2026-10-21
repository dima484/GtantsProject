package academy.tochkavhoda.competition.domain.models;

import java.util.*;

public class Grant {

    private Application application;
    private double averageRating;

    public Grant() {
    }

    public Grant(Application application, double averageRating) {
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
