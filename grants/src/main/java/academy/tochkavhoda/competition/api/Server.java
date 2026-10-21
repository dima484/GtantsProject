package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.*;
import academy.tochkavhoda.competition.api.dto.responses.*;
import academy.tochkavhoda.competition.application.commands.NewApplication;
import academy.tochkavhoda.competition.application.services.ApplicationService;
import academy.tochkavhoda.competition.application.services.ContestService;
import academy.tochkavhoda.competition.application.services.RatingService;
import academy.tochkavhoda.competition.application.services.UserService;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Сервер конкурса грантов (слой представления).
 * Разбирает json в DTO запроса, вызывает сервис, из результата сервиса строит DTO ответа.
 * Исключения превращает в ServerResponse с кодом 400. Создание нового экземпляра очищает базу.
 */
public class Server {

    private interface Action {
        /** Возвращает DTO ответа или null, если ответ пустой. */
        Object run() throws ServerException;
    }

    private final Gson gson = new Gson();
    private final UserService userService;
    private final ApplicationService applicationService;
    private final RatingService ratingService;
    private final ContestService contestService;

    public Server() {
        this(new Random());
    }

    /** Конструктор с заданным генератором случайных чисел (для воспроизводимых тестов). */
    public Server(Random random) {
        Database database = Database.getInstance();
        database.clear();
        userService = new UserService(database);
        applicationService = new ApplicationService(database);
        ratingService = new RatingService(database);
        contestService = new ContestService(database, random);
    }

    // ---------- регистрация и вход ----------

    public ServerResponse registerParticipant(String requestJsonString) {
        return execute(() -> {
            RegisterParticipantRequest r = parse(requestJsonString, RegisterParticipantRequest.class);
            userService.registerParticipant(r.getFirstName(), r.getLastName(), r.getFirmName(),
                    r.getLogin(), r.getPassword());
            return null;
        });
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return execute(() -> {
            RegisterExpertRequest r = parse(requestJsonString, RegisterExpertRequest.class);
            userService.registerExpert(r.getFirstName(), r.getLastName(), r.getDirections(),
                    r.getLogin(), r.getPassword());
            return null;
        });
    }

    public ServerResponse login(String requestJsonString) {
        return execute(() -> {
            LoginRequest r = parse(requestJsonString, LoginRequest.class);
            return LoginResponse.from(userService.login(r.getLogin(), r.getPassword()));
        });
    }

    public ServerResponse logout(String token) {
        return execute(() -> {
            userService.logout(token);
            return null;
        });
    }

    /** Участник или эксперт покидает сервер: вся информация о нем удаляется. */
    public ServerResponse leave(String token) {
        return execute(() -> {
            userService.leave(token);
            return null;
        });
    }

    public ServerResponse getParticipants(String token, String requestJsonString) {
        return execute(() -> {
            GetParticipantsRequest r = parseOptional(requestJsonString, GetParticipantsRequest.class);
            return ParticipantsResponse.from(
                    userService.getParticipants(token, r == null ? null : r.getDirections()));
        });
    }

    // ---------- заявки (участники) ----------

    public ServerResponse addApplication(String token, String requestJsonString) {
        return execute(() -> {
            AddApplicationRequest r = parse(requestJsonString, AddApplicationRequest.class);
            return new AddApplicationResponse(applicationService.addApplication(token, toCommand(r)));
        });
    }

    public ServerResponse addApplications(String token, String requestJsonString) {
        return execute(() -> {
            AddApplicationsRequest r = parse(requestJsonString, AddApplicationsRequest.class);
            List<NewApplication> commands = null;
            if (r.getApplications() != null) {
                commands = new ArrayList<>();
                for (AddApplicationRequest item : r.getApplications()) {
                    commands.add(toCommand(item));
                }
            }
            return new AddApplicationsResponse(applicationService.addApplications(token, commands));
        });
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return execute(() -> {
            ApplicationIdRequest r = parse(requestJsonString, ApplicationIdRequest.class);
            applicationService.cancelApplication(token, r.getApplicationId());
            return null;
        });
    }

    public ServerResponse getMyApplications(String token) {
        return execute(() -> ApplicationsResponse.from(applicationService.getMyApplications(token)));
    }

    // ---------- заявки и оценки (эксперты) ----------

    public ServerResponse getApplications(String token, String requestJsonString) {
        return execute(() -> {
            GetApplicationsRequest r = parseOptional(requestJsonString, GetApplicationsRequest.class);
            return ApplicationsResponse.from(
                    applicationService.getApplications(token, r == null ? null : r.getDirections()));
        });
    }

    public ServerResponse rate(String token, String requestJsonString) {
        return execute(() -> {
            RateApplicationRequest r = parse(requestJsonString, RateApplicationRequest.class);
            ratingService.rate(token, r.getApplicationId(), r.getRating());
            return null;
        });
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        return execute(() -> {
            ApplicationIdRequest r = parse(requestJsonString, ApplicationIdRequest.class);
            ratingService.removeRating(token, r.getApplicationId());
            return null;
        });
    }

    public ServerResponse getRatedApplications(String token) {
        return execute(() -> ApplicationsResponse.from(ratingService.getRatedApplications(token)));
    }

    // ---------- итоги ----------

    /** Момент X: подведение итогов. Параметры: общий фонд и пороговая оценка. */
    public ServerResponse finishContest(String requestJsonString) {
        return execute(() -> {
            FinishContestRequest r = parse(requestJsonString, FinishContestRequest.class);
            return ContestResultResponse.from(contestService.finishContest(r.getFund(), r.getThreshold()));
        });
    }

    public ServerResponse getResults(String token) {
        return execute(() -> ContestResultResponse.from(contestService.getResults(token)));
    }

    // ---------- вспомогательные методы ----------

    private ServerResponse execute(Action action) {
        try {
            Object result = action.run();
            return new ServerResponse(200, result == null ? "{}" : gson.toJson(result));
        } catch (ServerException e) {
            return new ServerResponse(400, gson.toJson(new ErrorResponse(e.getMessage())));
        }
    }

    private <T> T parse(String json, Class<T> cls) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        try {
            T result = gson.fromJson(json, cls);
            if (result == null) {
                throw new ServerException(ErrorCode.INVALID_JSON);
            }
            return result;
        } catch (JsonParseException e) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
    }

    /** Для методов, у которых тело запроса необязательно. Возвращает null, если тела нет. */
    private <T> T parseOptional(String json, Class<T> cls) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return parse(json, cls);
    }

    private static NewApplication toCommand(AddApplicationRequest r) {
        if (r == null) {
            return null;
        }
        return new NewApplication(r.getTitle(), r.getDescription(), r.getDirections(), r.getAmount());
    }
}
