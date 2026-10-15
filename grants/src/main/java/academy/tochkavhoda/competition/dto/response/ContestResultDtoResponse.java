package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ContestResultDtoResponse {

    private List<GrantDtoResponse> grants;
    private long remainingFund;

    public ContestResultDtoResponse() {
    }

    public ContestResultDtoResponse(List<GrantDtoResponse> grants, long remainingFund) {
        this.grants = grants;
        this.remainingFund = remainingFund;
    }

    public List<GrantDtoResponse> getGrants() {
        return grants;
    }

    public void setGrants(List<GrantDtoResponse> grants) {
        this.grants = grants;
    }

    public long getRemainingFund() {
        return remainingFund;
    }

    public void setRemainingFund(long remainingFund) {
        this.remainingFund = remainingFund;
    }
}
