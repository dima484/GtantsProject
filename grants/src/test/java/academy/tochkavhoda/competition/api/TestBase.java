package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.*;
import academy.tochkavhoda.competition.api.dto.responses.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** Общие вспомогательные методы для тестов. Тест - клиент: он знает только Server, DTO и json. */
public abstract class TestBase {

    protected static final String PASSWORD = "password1";

    protected final Gson gson = new Gson();
    protected Server server;

    @BeforeEach
    public void startServer() {
        server = new Server(new Random(1));
    }

    protected ServerResponse registerParticipant(String login) {
        return server.registerParticipant(gson.toJson(
                new RegisterParticipantRequest("Иван", "Иванов", "Фирма " + login, login, PASSWORD)));
    }

    protected ServerResponse registerExpert(String login, String... directions) {
        return server.registerExpert(gson.toJson(
                new RegisterExpertRequest("Петр", "Петров", Arrays.asList(directions), login, PASSWORD)));
    }

    protected String login(String login) {
        ServerResponse response = server.login(gson.toJson(new LoginRequest(login, PASSWORD)));
        assertEquals(200, response.getResponseCode(), response.getResponseData());
        return gson.fromJson(response.getResponseData(), LoginResponse.class).getToken();
    }

    protected String participantToken(String login) {
        assertEquals(200, registerParticipant(login).getResponseCode());
        return login(login);
    }

    protected String expertToken(String login, String... directions) {
        assertEquals(200, registerExpert(login, directions).getResponseCode());
        return login(login);
    }

    protected ServerResponse addApplicationResponse(String token, String title, long amount, String... directions) {
        return server.addApplication(token, gson.toJson(
                new AddApplicationRequest(title, "Описание " + title, Arrays.asList(directions), amount)));
    }

    protected int addApplication(String token, String title, long amount, String... directions) {
        ServerResponse response = addApplicationResponse(token, title, amount, directions);
        assertEquals(200, response.getResponseCode(), response.getResponseData());
        return gson.fromJson(response.getResponseData(), AddApplicationResponse.class).getApplicationId();
    }

    protected ServerResponse rate(String token, int applicationId, int rating) {
        return server.rate(token, gson.toJson(new RateApplicationRequest(applicationId, rating)));
    }

    protected void rateOk(String token, int applicationId, int rating) {
        ServerResponse response = rate(token, applicationId, rating);
        assertEquals(200, response.getResponseCode(), response.getResponseData());
    }

    protected ServerResponse finish(long fund, double threshold) {
        return server.finishContest(gson.toJson(new FinishContestRequest(fund, threshold)));
    }

    protected List<ApplicationResponse> applications(ServerResponse response) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
        return gson.fromJson(response.getResponseData(), ApplicationsResponse.class).getApplications();
    }

    protected ContestResultResponse results(ServerResponse response) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
        return gson.fromJson(response.getResponseData(), ContestResultResponse.class);
    }

    /** Проверяет, что ответ - ошибка 400, и что в json есть поле "error" с ожидаемым текстом. */
    protected void assertError(ServerResponse response, String expectedPart) {
        assertEquals(400, response.getResponseCode(), response.getResponseData());
        ErrorResponse error = gson.fromJson(response.getResponseData(), ErrorResponse.class);
        assertNotNull(error.getError());
        assertTrue(error.getError().contains(expectedPart),
                "Ожидалось \"" + expectedPart + "\", получено: " + error.getError());
    }
}
