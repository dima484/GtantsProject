package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.ContestDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.ContestDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.GetApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationsDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.Rating;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ApplicationService extends BaseService {

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();
    private final ContestDao contestDao = new ContestDaoImpl();

    public ServerResponse addApplication(String token, String requestJsonString) {
        return execute(() -> {
            Participant participant = asParticipant(userDao.getByToken(token));
            checkContestOpen();
            AddApplicationDtoRequest request = parse(requestJsonString, AddApplicationDtoRequest.class);
            Application application = toModel(participant, request);
            return new AddApplicationDtoResponse(applicationDao.insert(application));
        });
    }

    /** Добавляет сразу несколько заявок. Если хотя бы одна некорректна - не добавляется ни одна. */
    public ServerResponse addApplications(String token, String requestJsonString) {
        return execute(() -> {
            Participant participant = asParticipant(userDao.getByToken(token));
            checkContestOpen();
            AddApplicationsDtoRequest request = parse(requestJsonString, AddApplicationsDtoRequest.class);
            if (request.getApplications() == null || request.getApplications().isEmpty()) {
                throw new ServerException(ErrorCode.EMPTY_FIELD, "applications");
            }
            List<Application> models = new ArrayList<>();
            for (AddApplicationDtoRequest r : request.getApplications()) {
                if (r == null) {
                    throw new ServerException(ErrorCode.INVALID_JSON);
                }
                models.add(toModel(participant, r));
            }
            List<Integer> ids = new ArrayList<>();
            for (Application a : models) {
                ids.add(applicationDao.insert(a));
            }
            return new AddApplicationsDtoResponse(ids);
        });
    }

    /** Участник отменяет (удаляет) свою заявку. Вместе с ней удаляются оценки. */
    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return execute(() -> {
            Participant participant = asParticipant(userDao.getByToken(token));
            checkContestOpen();
            ApplicationIdDtoRequest request = parse(requestJsonString, ApplicationIdDtoRequest.class);
            Application application = applicationDao.get(request.getApplicationId());
            if (application.getOwner() != participant) {
                throw new ServerException(ErrorCode.ACCESS_DENIED, "это не Ваша заявка");
            }
            applicationDao.delete(application.getId());
            return null;
        });
    }

    public ServerResponse getMyApplications(String token) {
        return execute(() -> {
            Participant participant = asParticipant(userDao.getByToken(token));
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application a : applicationDao.getAll()) {
                if (a.getOwner() == participant) {
                    result.add(ApplicationMapper.toDto(a, null));
                }
            }
            return new ApplicationsDtoResponse(result);
        });
    }

    /**
     * Заявки по направлению (набору направлений) из числа направлений эксперта.
     * Если направления не указаны - по всем направлениям эксперта.
     */
    public ServerResponse getApplications(String token, String requestJsonString) {
        return execute(() -> {
            Expert expert = asExpert(userDao.getByToken(token));
            GetApplicationsDtoRequest request = parseOptional(requestJsonString, GetApplicationsDtoRequest.class);
            Set<String> directions = expert.getDirections();
            if (request != null && request.getDirections() != null && !request.getDirections().isEmpty()) {
                directions = normalizeDirections(request.getDirections());
                if (!expert.getDirections().containsAll(directions)) {
                    throw new ServerException(ErrorCode.FOREIGN_DIRECTION);
                }
            }
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application a : applicationDao.getAll()) {
                if (intersects(a.getDirections(), directions)) {
                    Rating mine = ratingDao.get(a.getId(), expert.getLogin());
                    result.add(ApplicationMapper.toDto(a, mine == null ? null : mine.getValue()));
                }
            }
            return new ApplicationsDtoResponse(result);
        });
    }

    private void checkContestOpen() throws ServerException {
        if (contestDao.isFinished()) {
            throw new ServerException(ErrorCode.CONTEST_CLOSED);
        }
    }

    private static Application toModel(Participant owner, AddApplicationDtoRequest r) throws ServerException {
        requireText(r.getTitle(), "title");
        requireText(r.getDescription(), "description");
        Set<String> directions = normalizeDirections(r.getDirections());
        if (r.getAmount() <= 0) {
            throw new ServerException(ErrorCode.INVALID_AMOUNT);
        }
        return new Application(0, owner, r.getTitle().trim(), r.getDescription().trim(), directions, r.getAmount());
    }
}
