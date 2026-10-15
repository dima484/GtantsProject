package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class FinishContestDtoRequest {

    private long fund;
    private double threshold;

    public FinishContestDtoRequest() {
    }

    public FinishContestDtoRequest(long fund, double threshold) {
        this.fund = fund;
        this.threshold = threshold;
    }

    public long getFund() {
        return fund;
    }

    public void setFund(long fund) {
        this.fund = fund;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
