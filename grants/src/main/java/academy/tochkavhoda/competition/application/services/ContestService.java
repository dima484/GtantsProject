package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.ContestResult;
import academy.tochkavhoda.competition.application.results.GrantResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.Application;
import academy.tochkavhoda.competition.domain.models.ContestOutcome;
import academy.tochkavhoda.competition.domain.models.Grant;
import academy.tochkavhoda.competition.domain.models.Rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ContestService {

    private static final double MAX_RATING = 5;

    private final Database database;
    private final Random random;

    public ContestService(Database database, Random random) {
        this.database = database;
        this.random = random;
    }

    /**
     * Момент X: подведение итогов. После него заявки и оценки больше не принимаются.
     * Заявки без единой оценки в ранжировании не участвуют.
     */
    public ContestResult finishContest(long fund, double threshold) throws ServerException {
        if (fund <= 0 || threshold < 0 || threshold > MAX_RATING) {
            throw new ServerException(ErrorCode.INVALID_FUND);
        }
        if (database.isContestFinished()) {
            throw new ServerException(ErrorCode.CONTEST_ALREADY_FINISHED);
        }
        ContestOutcome outcome = calculate(fund, threshold);
        database.setContestOutcome(outcome);
        return toResult(outcome);
    }

    public ContestResult getResults(String token) throws ServerException {
        database.getUserByToken(token);
        if (!database.isContestFinished()) {
            throw new ServerException(ErrorCode.CONTEST_NOT_FINISHED);
        }
        return toResult(database.getContestOutcome());
    }

    private ContestOutcome calculate(long fund, double threshold) {
        List<Grant> candidates = new ArrayList<>();
        for (Application a : database.getApplications()) {
            List<Rating> ratings = database.getRatingsByApplication(a.getId());
            if (ratings.isEmpty()) {
                continue;
            }
            double sum = 0;
            for (Rating r : ratings) {
                sum += r.getValue();
            }
            candidates.add(new Grant(a, sum / ratings.size()));
        }
        // Сортировка устойчива, поэтому предварительное перемешивание
        // дает случайный порядок среди заявок с одинаковой оценкой и суммой.
        Collections.shuffle(candidates, random);
        candidates.sort(Comparator.comparingDouble(Grant::getAverageRating).reversed()
                .thenComparingLong(g -> g.getApplication().getAmount()));

        List<Grant> grants = new ArrayList<>();
        long remaining = fund;
        for (Grant c : candidates) {
            if (c.getAverageRating() <= threshold) {
                break;
            }
            if (c.getApplication().getAmount() <= remaining) {
                grants.add(c);
                remaining -= c.getApplication().getAmount();
            }
        }
        return new ContestOutcome(grants, remaining);
    }

    private static ContestResult toResult(ContestOutcome outcome) {
        List<GrantResult> grants = new ArrayList<>();
        for (Grant g : outcome.getGrants()) {
            Application a = g.getApplication();
            grants.add(new GrantResult(a.getId(), a.getTitle(), a.getOwner().getFirmName(),
                    a.getAmount(), g.getAverageRating()));
        }
        return new ContestResult(grants, outcome.getRemainingFund());
    }
}
