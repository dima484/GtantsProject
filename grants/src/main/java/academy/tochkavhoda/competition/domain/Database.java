package academy.tochkavhoda.competition.domain;

import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.*;

import java.util.*;

/** "База данных" в оперативной памяти. Синглтон. Знает только классы модели. */
public final class Database {

    private static final Database INSTANCE = new Database();

    private final Map<String, User> usersByLogin = new LinkedHashMap<>();
    private final Map<String, User> usersByToken = new HashMap<>();
    private final Map<Integer, Application> applications = new LinkedHashMap<>();
    // id заявки -> (логин эксперта -> оценка)
    private final Map<Integer, Map<String, Rating>> ratings = new HashMap<>();
    private int nextApplicationId = 1;
    private ContestOutcome contestOutcome;

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public synchronized void clear() {
        usersByLogin.clear();
        usersByToken.clear();
        applications.clear();
        ratings.clear();
        nextApplicationId = 1;
        contestOutcome = null;
    }

    // ---------- пользователи ----------

    public synchronized void insertUser(User user) throws ServerException {
        if (usersByLogin.containsKey(user.getLogin())) {
            throw new ServerException(ErrorCode.LOGIN_ALREADY_USED, user.getLogin());
        }
        usersByLogin.put(user.getLogin(), user);
    }

    public synchronized User getUserByLogin(String login) {
        return usersByLogin.get(login);
    }

    /** Выдает новый токен; прежний токен этого пользователя перестает действовать. */
    public synchronized String login(User user) {
        usersByToken.values().removeIf(u -> u == user);
        String token = UUID.randomUUID().toString();
        usersByToken.put(token, user);
        return token;
    }

    public synchronized User getUserByToken(String token) throws ServerException {
        User user = token == null ? null : usersByToken.get(token);
        if (user == null) {
            throw new ServerException(ErrorCode.INVALID_TOKEN);
        }
        return user;
    }

    public synchronized void logout(String token) {
        usersByToken.remove(token);
    }

    /** Удаляет пользователя вместе с его заявками (участник) или оценками (эксперт). */
    public synchronized void deleteUser(User user) {
        usersByToken.values().removeIf(u -> u == user);
        usersByLogin.remove(user.getLogin());
        if (user instanceof Participant) {
            List<Integer> ids = new ArrayList<>();
            for (Application a : applications.values()) {
                if (a.getOwner() == user) {
                    ids.add(a.getId());
                }
            }
            ids.forEach(this::deleteApplication);
        } else {
            for (Map<String, Rating> byExpert : ratings.values()) {
                byExpert.remove(user.getLogin());
            }
        }
    }

    public synchronized List<Participant> getParticipants() {
        List<Participant> result = new ArrayList<>();
        for (User u : usersByLogin.values()) {
            if (u instanceof Participant) {
                result.add((Participant) u);
            }
        }
        return result;
    }

    // ---------- заявки ----------

    public synchronized int insertApplication(Application application) {
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        return application.getId();
    }

    public synchronized Application getApplication(int id) throws ServerException {
        Application application = applications.get(id);
        if (application == null) {
            throw new ServerException(ErrorCode.APPLICATION_NOT_FOUND, String.valueOf(id));
        }
        return application;
    }

    public synchronized void deleteApplication(int id) {
        applications.remove(id);
        ratings.remove(id);
    }

    public synchronized List<Application> getApplications() {
        return new ArrayList<>(applications.values());
    }

    // ---------- оценки ----------

    public synchronized void putRating(Rating rating) {
        ratings.computeIfAbsent(rating.getApplication().getId(), k -> new LinkedHashMap<>())
                .put(rating.getExpert().getLogin(), rating);
    }

    public synchronized Rating getRating(int applicationId, String expertLogin) {
        Map<String, Rating> byExpert = ratings.get(applicationId);
        return byExpert == null ? null : byExpert.get(expertLogin);
    }

    public synchronized void deleteRating(int applicationId, String expertLogin) throws ServerException {
        Map<String, Rating> byExpert = ratings.get(applicationId);
        if (byExpert == null || byExpert.remove(expertLogin) == null) {
            throw new ServerException(ErrorCode.RATING_NOT_FOUND, String.valueOf(applicationId));
        }
    }

    public synchronized List<Rating> getRatingsByApplication(int applicationId) {
        Map<String, Rating> byExpert = ratings.get(applicationId);
        return byExpert == null ? new ArrayList<>() : new ArrayList<>(byExpert.values());
    }

    public synchronized List<Rating> getRatingsByExpert(String expertLogin) {
        List<Rating> result = new ArrayList<>();
        for (Map<String, Rating> byExpert : ratings.values()) {
            Rating r = byExpert.get(expertLogin);
            if (r != null) {
                result.add(r);
            }
        }
        return result;
    }

    // ---------- итоги ----------

    public synchronized boolean isContestFinished() {
        return contestOutcome != null;
    }

    public synchronized void setContestOutcome(ContestOutcome outcome) {
        this.contestOutcome = outcome;
    }

    public synchronized ContestOutcome getContestOutcome() {
        return contestOutcome;
    }
}
