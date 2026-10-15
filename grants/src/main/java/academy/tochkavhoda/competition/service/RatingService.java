package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.ContestDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.ContestDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Rating;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RatingService extends BaseService {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();
    private final ContestDao contestDao = new ContestDaoImpl();

    /** Ставит оценку заявке; если эксперт уже оценивал эту заявку, оценка изменяется. */
    public ServerResponse rate(String token, String requestJsonString) {
        return execute(() -> {
            Expert expert = asExpert(userDao.getByToken(token));
            checkContestOpen();
            RateApplicationDtoRequest request = parse(requestJsonString, RateApplicationDtoRequest.class);
            if (request.getRating() < MIN_RATING || request.getRating() > MAX_RATING) {
                throw new ServerException(ErrorCode.INVALID_RATING);
            }
            Application application = applicationDao.get(request.getApplicationId());
            if (!intersects(application.getDirections(), expert.getDirections())) {
                throw new ServerException(ErrorCode.FOREIGN_DIRECTION);
            }
            ratingDao.put(new Rating(expert, application, request.getRating()));
            return null;
        });
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        return execute(() -> {
            Expert expert = asExpert(userDao.getByToken(token));
            checkContestOpen();
            ApplicationIdDtoRequest request = parse(requestJsonString, ApplicationIdDtoRequest.class);
            ratingDao.delete(request.getApplicationId(), expert.getLogin());
            return null;
        });
    }

    /** Все заявки, которым эксперт поставил оценку (вместе с его оценкой). */
    public ServerResponse getRatedApplications(String token) {
        return execute(() -> {
            Expert expert = asExpert(userDao.getByToken(token));
            List<Rating> ratings = ratingDao.getByExpert(expert.getLogin());
            ratings.sort(Comparator.comparingInt(r -> r.getApplication().getId()));
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Rating r : ratings) {
                result.add(ApplicationMapper.toDto(r.getApplication(), r.getValue()));
            }
            return new ApplicationsDtoResponse(result);
        });
    }

    private void checkContestOpen() throws ServerException {
        if (contestDao.isFinished()) {
            throw new ServerException(ErrorCode.CONTEST_CLOSED);
        }
    }
}
