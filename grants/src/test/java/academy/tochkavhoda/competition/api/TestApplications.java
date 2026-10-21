package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.*;
import academy.tochkavhoda.competition.api.dto.responses.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestApplications extends TestBase {

    @Test
    public void testAddApplication() {
        String token = participantToken("participant1");
        int id = addApplication(token, "Проект", 1000, "Математика", " информатика ");
        List<ApplicationResponse> mine = applications(server.getMyApplications(token));
        assertEquals(1, mine.size());
        assertEquals(id, mine.get(0).getId());
        assertEquals("Проект", mine.get(0).getTitle());
        assertEquals(1000, mine.get(0).getAmount());
        assertEquals(Arrays.asList("математика", "информатика"), mine.get(0).getDirections());
        assertEquals("Фирма participant1", mine.get(0).getFirmName());
    }

    @Test
    public void testAddInvalidApplication() {
        String token = participantToken("participant1");
        assertError(addApplicationResponse(token, "", 100, "математика"), "не заполнено");
        assertError(addApplicationResponse(token, "Проект", 100), "направлений");
        assertError(addApplicationResponse(token, "Проект", 0, "математика"), "сумма");
        assertError(addApplicationResponse(token, "Проект", -5, "математика"), "сумма");
        assertError(server.addApplication(token, "не json"), "формат");
        assertEquals(0, applications(server.getMyApplications(token)).size());
    }

    @Test
    public void testAddSeveralApplications() {
        String token = participantToken("participant1");
        String json = gson.toJson(new AddApplicationsRequest(Arrays.asList(
                new AddApplicationRequest("A", "описание", Arrays.asList("математика"), 10),
                new AddApplicationRequest("B", "описание", Arrays.asList("физика"), 20))));
        ServerResponse response = server.addApplications(token, json);
        assertEquals(200, response.getResponseCode());
        assertEquals(2, gson.fromJson(response.getResponseData(), AddApplicationsResponse.class)
                .getApplicationIds().size());
        assertEquals(2, applications(server.getMyApplications(token)).size());
    }

    @Test
    public void testAddSeveralApplicationsIsAtomic() {
        String token = participantToken("participant1");
        String json = gson.toJson(new AddApplicationsRequest(Arrays.asList(
                new AddApplicationRequest("A", "описание", Arrays.asList("математика"), 10),
                new AddApplicationRequest("B", "описание", Arrays.asList("физика"), 0))));
        assertError(server.addApplications(token, json), "сумма");
        assertEquals(0, applications(server.getMyApplications(token)).size());
        assertError(server.addApplications(token, gson.toJson(new AddApplicationsRequest())), "не заполнено");
    }

    @Test
    public void testCancelApplication() {
        String token = participantToken("participant1");
        int id = addApplication(token, "Проект", 100, "математика");
        assertEquals(200, server.cancelApplication(token, gson.toJson(new ApplicationIdRequest(id)))
                .getResponseCode());
        assertEquals(0, applications(server.getMyApplications(token)).size());
        assertError(server.cancelApplication(token, gson.toJson(new ApplicationIdRequest(id))), "не найдена");
    }

    @Test
    public void testCannotCancelForeignApplication() {
        String owner = participantToken("participant1");
        String other = participantToken("participant2");
        int id = addApplication(owner, "Проект", 100, "математика");
        assertError(server.cancelApplication(other, gson.toJson(new ApplicationIdRequest(id))), "Операция");
        assertEquals(1, applications(server.getMyApplications(owner)).size());
    }

    @Test
    public void testRolesAreSeparated() {
        String participant = participantToken("participant1");
        String expert = expertToken("expert001", "математика");
        assertError(addApplicationResponse(expert, "Проект", 100, "математика"), "Операция");
        assertError(server.getApplications(participant, null), "Операция");
        assertError(server.getMyApplications(expert), "Операция");
        assertError(rate(participant, 1, 5), "Операция");
    }

    @Test
    public void testExpertGetsApplicationsByDirection() {
        String p = participantToken("participant1");
        int math = addApplication(p, "Матем", 100, "математика");
        int mixed = addApplication(p, "Смешанная", 100, "математика", "физика");
        addApplication(p, "Биология", 100, "биология");
        String expert = expertToken("expert001", "математика", "физика");

        // без параметров - по всем направлениям эксперта
        assertEquals(2, applications(server.getApplications(expert, null)).size());

        List<ApplicationResponse> onlyPhysics = applications(server.getApplications(expert,
                gson.toJson(new GetApplicationsRequest(Arrays.asList("Физика")))));
        assertEquals(1, onlyPhysics.size());
        assertEquals(mixed, onlyPhysics.get(0).getId());

        List<ApplicationResponse> both = applications(server.getApplications(expert,
                gson.toJson(new GetApplicationsRequest(Arrays.asList("математика", "физика")))));
        assertEquals(2, both.size());
        assertEquals(math, both.get(0).getId());
    }

    @Test
    public void testExpertCannotRequestForeignDirection() {
        String expert = expertToken("expert001", "математика");
        assertError(server.getApplications(expert,
                gson.toJson(new GetApplicationsRequest(Arrays.asList("биология")))), "не входит");
    }

    @Test
    public void testGetParticipants() {
        String p1 = participantToken("participant1");
        String p2 = participantToken("participant2");
        participantToken("participant3");
        addApplication(p1, "A", 10, "математика");
        addApplication(p2, "B", 10, "физика");
        String expert = expertToken("expert001", "математика");

        ParticipantsResponse all = gson.fromJson(server.getParticipants(expert, null).getResponseData(),
                ParticipantsResponse.class);
        assertEquals(3, all.getParticipants().size());

        ParticipantsResponse math = gson.fromJson(server.getParticipants(expert,
                gson.toJson(new GetParticipantsRequest(Arrays.asList("математика")))).getResponseData(),
                ParticipantsResponse.class);
        assertEquals(1, math.getParticipants().size());
        assertEquals("Фирма participant1", math.getParticipants().get(0).getFirmName());
        assertError(server.getParticipants("fake", null), "токен");
    }
}
