package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.service.ApplicationService;
import academy.tochkavhoda.competition.service.ContestService;
import academy.tochkavhoda.competition.service.RatingService;
import academy.tochkavhoda.competition.service.UserService;

import java.util.Random;

/**
 * Сервер конкурса грантов. Сам ничего не делает - делегирует вызовы сервисам.
 * Создание нового экземпляра сервера очищает все данные (база данных в оперативной памяти).
 */
public class Server {

    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();
    private final RatingService ratingService = new RatingService();
    private final ContestService contestService;

    public Server() {
        this(new Random());
    }

    /** Конструктор с заданным генератором случайных чисел (для воспроизводимых тестов). */
    public Server(Random random) {
        contestService = new ContestService(random);
        contestService.reset();
    }

    // ---------- регистрация и вход ----------

    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    /** Участник или эксперт покидает сервер: вся информация о нем удаляется. */
    public ServerResponse leave(String token) {
        return userService.leave(token);
    }

    public ServerResponse getParticipants(String token, String requestJsonString) {
        return userService.getParticipants(token, requestJsonString);
    }

    // ---------- заявки (участники) ----------

    public ServerResponse addApplication(String token, String requestJsonString) {
        return applicationService.addApplication(token, requestJsonString);
    }

    public ServerResponse addApplications(String token, String requestJsonString) {
        return applicationService.addApplications(token, requestJsonString);
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return applicationService.cancelApplication(token, requestJsonString);
    }

    public ServerResponse getMyApplications(String token) {
        return applicationService.getMyApplications(token);
    }

    // ---------- заявки и оценки (эксперты) ----------

    public ServerResponse getApplications(String token, String requestJsonString) {
        return applicationService.getApplications(token, requestJsonString);
    }

    public ServerResponse rate(String token, String requestJsonString) {
        return ratingService.rate(token, requestJsonString);
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        return ratingService.removeRating(token, requestJsonString);
    }

    public ServerResponse getRatedApplications(String token) {
        return ratingService.getRatedApplications(token);
    }

    // ---------- итоги ----------

    /** Момент X: подведение итогов. Параметры: общий фонд и пороговая оценка. */
    public ServerResponse finishContest(String requestJsonString) {
        return contestService.finishContest(requestJsonString);
    }

    public ServerResponse getResults(String token) {
        return contestService.getResults(token);
    }
}
