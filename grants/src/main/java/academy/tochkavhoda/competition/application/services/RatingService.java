package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.ApplicationResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.Application;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.Rating;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static academy.tochkavhoda.competition.application.services.Validation.asExpert;
import static academy.tochkavhoda.competition.application.services.Validation.intersects;

public class RatingService {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final Database database;

    public RatingService(Database database) {
        this.database = database;
    }

    /** Ставит оценку заявке; если эксперт уже оценивал эту заявку, оценка изменяется. */
    public void rate(String token, int applicationId, int rating) throws ServerException {
        Expert expert = asExpert(database.getUserByToken(token));
        checkContestOpen();
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new ServerException(ErrorCode.INVALID_RATING);
        }
        Application application = database.getApplication(applicationId);
        if (!intersects(application.getDirections(), expert.getDirections())) {
            throw new ServerException(ErrorCode.FOREIGN_DIRECTION);
        }
        database.putRating(new Rating(expert, application, rating));
    }

    public void removeRating(String token, int applicationId) throws ServerException {
        Expert expert = asExpert(database.getUserByToken(token));
        checkContestOpen();
        database.deleteRating(applicationId, expert.getLogin());
    }

    /** Все заявки, которым эксперт поставил оценку (вместе с его оценкой). */
    public List<ApplicationResult> getRatedApplications(String token) throws ServerException {
        Expert expert = asExpert(database.getUserByToken(token));
        List<Rating> ratings = database.getRatingsByExpert(expert.getLogin());
        ratings.sort(Comparator.comparingInt(r -> r.getApplication().getId()));
        List<ApplicationResult> result = new ArrayList<>();
        for (Rating r : ratings) {
            result.add(ApplicationMapper.toResult(r.getApplication(), r.getValue()));
        }
        return result;
    }

    private void checkContestOpen() throws ServerException {
        if (database.isContestFinished()) {
            throw new ServerException(ErrorCode.CONTEST_CLOSED);
        }
    }
}
