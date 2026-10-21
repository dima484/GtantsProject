package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;

public class AddApplicationsResponse {

    private List<Integer> applicationIds;

    public AddApplicationsResponse() {
    }

    public AddApplicationsResponse(List<Integer> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public List<Integer> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Integer> applicationIds) {
        this.applicationIds = applicationIds;
    }
}
