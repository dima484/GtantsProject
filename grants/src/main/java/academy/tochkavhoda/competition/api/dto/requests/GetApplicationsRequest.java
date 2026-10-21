package academy.tochkavhoda.competition.api.dto.requests;

import java.util.*;

public class GetApplicationsRequest {

    private List<String> directions;

    public GetApplicationsRequest() {
    }

    public GetApplicationsRequest(List<String> directions) {
        this.directions = directions;
    }

    public List<String> getDirections() {
        return directions;
    }

    public void setDirections(List<String> directions) {
        this.directions = directions;
    }
}
