package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ParticipantsDtoResponse {

    private List<ParticipantDtoResponse> participants;

    public ParticipantsDtoResponse() {
    }

    public ParticipantsDtoResponse(List<ParticipantDtoResponse> participants) {
        this.participants = participants;
    }

    public List<ParticipantDtoResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDtoResponse> participants) {
        this.participants = participants;
    }
}
