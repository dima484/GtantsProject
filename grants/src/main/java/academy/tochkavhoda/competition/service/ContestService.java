package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.ContestDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.ContestDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.FinishContestDtoRequest;
import academy.tochkavhoda.competition.dto.response.ContestResultDtoResponse;
import academy.tochkavhoda.competition.dto.response.GrantDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.ContestResult;
import academy.tochkavhoda.competition.model.GrantResult;
import academy.tochkavhoda.competition.model.Rating;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ContestService extends BaseService {

    private static final double MAX_RATING = 5;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();
    private final ContestDao contestDao = new ContestDaoImpl();
    private final Random random;

    public ContestService() {
        this(new Random());
    }

    public ContestService(Random random) {
        this.random = random;
    }

    /** Удаляет все данные сервера. */
    public void reset() {
        contestDao.clear();
    }

    /**
     * Момент X: подведение итогов. После него заявки и оценки больше не принимаются.
     * Заявки без единой оценки в ранжировании не участвуют.
     */
    public ServerResponse finishContest(String requestJsonString) {
        return execute(() -> {
            FinishContestDtoRequest request = parse(requestJsonString, FinishContestDtoRequest.class);
            if (request.getFund() <= 0 || request.getThreshold() < 0 || request.getThreshold() > MAX_RATING) {
                throw new ServerException(ErrorCode.INVALID_FUND);
            }
            if (contestDao.isFinished()) {
                throw new ServerException(ErrorCode.CONTEST_ALREADY_FINISHED);
            }
            ContestResult result = calculate(request.getFund(), request.getThreshold());
            contestDao.setResult(result);
            return toDto(result);
        });
    }

    public ServerResponse getResults(String token) {
        return execute(() -> {
            userDao.getByToken(token);
            if (!contestDao.isFinished()) {
                throw new ServerException(ErrorCode.CONTEST_NOT_FINISHED);
            }
            return toDto(contestDao.getResult());
        });
    }

    private ContestResult calculate(long fund, double threshold) {
        List<GrantResult> candidates = new ArrayList<>();
        for (Application a : applicationDao.getAll()) {
            List<Rating> ratings = ratingDao.getByApplication(a.getId());
            if (ratings.isEmpty()) {
                continue;
            }
            double sum = 0;
            for (Rating r : ratings) {
                sum += r.getValue();
            }
            candidates.add(new GrantResult(a, sum / ratings.size()));
        }
        // Сортировка устойчива, поэтому предварительное перемешивание
        // дает случайный порядок среди заявок с одинаковой оценкой и суммой.
        Collections.shuffle(candidates, random);
        candidates.sort(Comparator.comparingDouble(GrantResult::getAverageRating).reversed()
                .thenComparingLong(g -> g.getApplication().getAmount()));

        List<GrantResult> grants = new ArrayList<>();
        long remaining = fund;
        for (GrantResult c : candidates) {
            if (c.getAverageRating() <= threshold) {
                break;
            }
            if (c.getApplication().getAmount() <= remaining) {
                grants.add(c);
                remaining -= c.getApplication().getAmount();
            }
        }
        return new ContestResult(grants, remaining);
    }

    private static ContestResultDtoResponse toDto(ContestResult result) {
        List<GrantDtoResponse> grants = new ArrayList<>();
        for (GrantResult g : result.getGrants()) {
            Application a = g.getApplication();
            grants.add(new GrantDtoResponse(a.getId(), a.getTitle(), a.getOwner().getFirmName(),
                    a.getAmount(), g.getAverageRating()));
        }
        return new ContestResultDtoResponse(grants, result.getRemainingFund());
    }
}
