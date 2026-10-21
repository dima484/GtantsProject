package academy.tochkavhoda.competition.api.dto.responses;

import java.util.*;
import academy.tochkavhoda.competition.application.results.ParticipantResult;

public class ParticipantsResponse {

    private List<ParticipantResponse> participants;

    public ParticipantsResponse() {
    }

    public ParticipantsResponse(List<ParticipantResponse> participants) {
        this.participants = participants;
    }

    public List<ParticipantResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantResponse> participants) {
        this.participants = participants;
    }

    public static ParticipantsResponse from(List<ParticipantResult> results) {
        List<ParticipantResponse> list = new ArrayList<>();
        for (ParticipantResult r : results) {
            list.add(ParticipantResponse.from(r));
        }
        return new ParticipantsResponse(list);
    }
}
