package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class AddApplicationsRequest {

    private List<AddApplicationRequest> applications;

    public AddApplicationsRequest() {
    }

    public AddApplicationsRequest(List<AddApplicationRequest> applications) {
        this.applications = applications;
    }

    public List<AddApplicationRequest> getApplications() {
        return applications;
    }

    public void setApplications(List<AddApplicationRequest> applications) {
        this.applications = applications;
    }
}
