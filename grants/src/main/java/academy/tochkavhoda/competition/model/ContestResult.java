package academy.tochkavhoda.competition.model;

import java.util.*;

public class ContestResult {

    private List<GrantResult> grants;
    private long remainingFund;

    public ContestResult() {
    }

    public ContestResult(List<GrantResult> grants, long remainingFund) {
        this.grants = grants;
        this.remainingFund = remainingFund;
    }

    public List<GrantResult> getGrants() {
        return grants;
    }

    public void setGrants(List<GrantResult> grants) {
        this.grants = grants;
    }

    public long getRemainingFund() {
        return remainingFund;
    }

    public void setRemainingFund(long remainingFund) {
        this.remainingFund = remainingFund;
    }
}
