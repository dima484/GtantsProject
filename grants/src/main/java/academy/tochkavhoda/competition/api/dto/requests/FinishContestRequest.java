package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class FinishContestRequest {

    private long fund;
    private double threshold;

    public FinishContestRequest() {
    }

    public FinishContestRequest(long fund, double threshold) {
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
