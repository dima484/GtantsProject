package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;
import academy.tochkavhoda.competition.application.results.ContestResult;
import academy.tochkavhoda.competition.application.results.GrantResult;

public class ContestResultResponse {

    private List<GrantResponse> grants;
    private long remainingFund;

    public ContestResultResponse() {
    }

    public ContestResultResponse(List<GrantResponse> grants, long remainingFund) {
        this.grants = grants;
        this.remainingFund = remainingFund;
    }

    public List<GrantResponse> getGrants() {
        return grants;
    }

    public void setGrants(List<GrantResponse> grants) {
        this.grants = grants;
    }

    public long getRemainingFund() {
        return remainingFund;
    }

    public void setRemainingFund(long remainingFund) {
        this.remainingFund = remainingFund;
    }

    public static ContestResultResponse from(ContestResult result) {
        List<GrantResponse> list = new ArrayList<>();
        for (GrantResult g : result.getGrants()) {
            list.add(GrantResponse.from(g));
        }
        return new ContestResultResponse(list, result.getRemainingFund());
    }
}
