package academy.tochkavhoda.competition.domain.models;

import java.util.*;

public class ContestOutcome {

    private List<Grant> grants;
    private long remainingFund;

    public ContestOutcome() {
    }

    public ContestOutcome(List<Grant> grants, long remainingFund) {
        this.grants = grants;
        this.remainingFund = remainingFund;
    }

    public List<Grant> getGrants() {
        return grants;
    }

    public void setGrants(List<Grant> grants) {
        this.grants = grants;
    }

    public long getRemainingFund() {
        return remainingFund;
    }

    public void setRemainingFund(long remainingFund) {
        this.remainingFund = remainingFund;
    }
}
