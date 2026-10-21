package academy.tochkavhoda.competition.domain.models;

import java.util.*;

public class Rating {

    private Expert expert;
    private Application application;
    private int value;

    public Rating() {
    }

    public Rating(Expert expert, Application application, int value) {
        this.expert = expert;
        this.application = application;
        this.value = value;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
