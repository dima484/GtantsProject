package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.commands.NewApplication;
import academy.tochkavhoda.competition.application.results.ApplicationResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.Application;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.Rating;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static academy.tochkavhoda.competition.application.services.Validation.*;

public class ApplicationService {

    private final Database database;

    public ApplicationService(Database database) {
        this.database = database;
    }

    public int addApplication(String token, NewApplication request) throws ServerException {
        Participant participant = asParticipant(database.getUserByToken(token));
        checkContestOpen();
        return database.insertApplication(toModel(participant, request));
    }

    /** Добавляет сразу несколько заявок. Если хотя бы одна некорректна - не добавляется ни одна. */
    public List<Integer> addApplications(String token, List<NewApplication> requests) throws ServerException {
        Participant participant = asParticipant(database.getUserByToken(token));
        checkContestOpen();
        if (requests == null || requests.isEmpty()) {
            throw new ServerException(ErrorCode.EMPTY_FIELD, "applications");
        }
        List<Application> models = new ArrayList<>();
        for (NewApplication r : requests) {
            models.add(toModel(participant, r));
        }
        List<Integer> ids = new ArrayList<>();
        for (Application a : models) {
            ids.add(database.insertApplication(a));
        }
        return ids;
    }

    /** Участник отменяет (удаляет) свою заявку. Вместе с ней удаляются оценки. */
    public void cancelApplication(String token, int applicationId) throws ServerException {
        Participant participant = asParticipant(database.getUserByToken(token));
        checkContestOpen();
        Application application = database.getApplication(applicationId);
        if (application.getOwner() != participant) {
            throw new ServerException(ErrorCode.ACCESS_DENIED, "это не Ваша заявка");
        }
        database.deleteApplication(applicationId);
    }

    public List<ApplicationResult> getMyApplications(String token) throws ServerException {
        Participant participant = asParticipant(database.getUserByToken(token));
        List<ApplicationResult> result = new ArrayList<>();
        for (Application a : database.getApplications()) {
            if (a.getOwner() == participant) {
                result.add(ApplicationMapper.toResult(a, null));
            }
        }
        return result;
    }

    /**
     * Заявки по направлению (набору направлений) из числа направлений эксперта.
     * Если направления не указаны - по всем направлениям эксперта.
     */
    public List<ApplicationResult> getApplications(String token, List<String> directions) throws ServerException {
        Expert expert = asExpert(database.getUserByToken(token));
        Set<String> wanted = expert.getDirections();
        if (directions != null && !directions.isEmpty()) {
            wanted = normalizeDirections(directions);
            if (!expert.getDirections().containsAll(wanted)) {
                throw new ServerException(ErrorCode.FOREIGN_DIRECTION);
            }
        }
        List<ApplicationResult> result = new ArrayList<>();
        for (Application a : database.getApplications()) {
            if (intersects(a.getDirections(), wanted)) {
                Rating mine = database.getRating(a.getId(), expert.getLogin());
                result.add(ApplicationMapper.toResult(a, mine == null ? null : mine.getValue()));
            }
        }
        return result;
    }

    private void checkContestOpen() throws ServerException {
        if (database.isContestFinished()) {
            throw new ServerException(ErrorCode.CONTEST_CLOSED);
        }
    }

    private static Application toModel(Participant owner, NewApplication r) throws ServerException {
        if (r == null) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        requireText(r.getTitle(), "title");
        requireText(r.getDescription(), "description");
        Set<String> directions = normalizeDirections(r.getDirections());
        if (r.getAmount() <= 0) {
            throw new ServerException(ErrorCode.INVALID_AMOUNT);
        }
        return new Application(0, owner, r.getTitle().trim(), r.getDescription().trim(), directions, r.getAmount());
    }
}
