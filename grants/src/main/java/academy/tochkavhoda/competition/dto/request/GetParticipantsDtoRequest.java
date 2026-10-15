package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class GetParticipantsDtoRequest {

    private List<String> directions;

    public GetParticipantsDtoRequest() {
    }

    public GetParticipantsDtoRequest(List<String> directions) {
        this.directions = directions;
    }

    public List<String> getDirections() {
        return directions;
    }

    public void setDirections(List<String> directions) {
        this.directions = directions;
    }
}
