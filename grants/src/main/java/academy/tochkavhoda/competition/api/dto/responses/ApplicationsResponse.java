package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;
import academy.tochkavhoda.competition.application.results.ApplicationResult;

public class ApplicationsResponse {

    private List<ApplicationResponse> applications;

    public ApplicationsResponse() {
    }

    public ApplicationsResponse(List<ApplicationResponse> applications) {
        this.applications = applications;
    }

    public List<ApplicationResponse> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationResponse> applications) {
        this.applications = applications;
    }

    public static ApplicationsResponse from(List<ApplicationResult> results) {
        List<ApplicationResponse> list = new ArrayList<>();
        for (ApplicationResult r : results) {
            list.add(ApplicationResponse.from(r));
        }
        return new ApplicationsResponse(list);
    }
}
