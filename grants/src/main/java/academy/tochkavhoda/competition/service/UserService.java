package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.dto.response.ParticipantDtoResponse;
import academy.tochkavhoda.competition.dto.response.ParticipantsDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserService extends BaseService {

    private static final int MIN_LOGIN_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    public ServerResponse registerParticipant(String requestJsonString) {
        return execute(() -> {
            RegisterParticipantDtoRequest request = parse(requestJsonString, RegisterParticipantDtoRequest.class);
            validate(request);
            userDao.insert(new Participant(request.getFirstName().trim(), request.getLastName().trim(),
                    request.getFirmName().trim(), request.getLogin(), request.getPassword()));
            return null;
        });
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return execute(() -> {
            RegisterExpertDtoRequest request = parse(requestJsonString, RegisterExpertDtoRequest.class);
            validate(request);
            Set<String> directions = normalizeDirections(request.getDirections());
            userDao.insert(new Expert(request.getFirstName().trim(), request.getLastName().trim(),
                    directions, request.getLogin(), request.getPassword()));
            return null;
        });
    }

    public ServerResponse login(String requestJsonString) {
        return execute(() -> {
            LoginDtoRequest request = parse(requestJsonString, LoginDtoRequest.class);
            requireText(request.getLogin(), "login");
            requireText(request.getPassword(), "password");
            User user = userDao.getByLogin(request.getLogin());
            if (user == null || !user.getPassword().equals(request.getPassword())) {
                throw new ServerException(ErrorCode.WRONG_LOGIN_OR_PASSWORD);
            }
            return new LoginDtoResponse(userDao.login(user));
        });
    }

    public ServerResponse logout(String token) {
        return execute(() -> {
            userDao.getByToken(token);
            userDao.logout(token);
            return null;
        });
    }

    /** Пользователь покидает сервер: удаляется он сам и все его заявки (оценки). */
    public ServerResponse leave(String token) {
        return execute(() -> {
            User user = userDao.getByToken(token);
            userDao.delete(user);
            return null;
        });
    }

    /** Список участников; если переданы направления - только тех, у кого есть заявки по ним. */
    public ServerResponse getParticipants(String token, String requestJsonString) {
        return execute(() -> {
            userDao.getByToken(token);
            GetParticipantsDtoRequest request = parseOptional(requestJsonString, GetParticipantsDtoRequest.class);
            boolean filter = request != null && request.getDirections() != null && !request.getDirections().isEmpty();
            Set<String> directions = filter ? normalizeDirections(request.getDirections()) : null;
            List<ParticipantDtoResponse> result = new ArrayList<>();
            for (Participant p : userDao.getParticipants()) {
                if (!filter || hasApplicationIn(p, directions)) {
                    result.add(new ParticipantDtoResponse(p.getFirstName(), p.getLastName(), p.getFirmName()));
                }
            }
            return new ParticipantsDtoResponse(result);
        });
    }

    private boolean hasApplicationIn(Participant participant, Set<String> directions) {
        for (Application a : applicationDao.getAll()) {
            if (a.getOwner() == participant && intersects(a.getDirections(), directions)) {
                return true;
            }
        }
        return false;
    }

    private static void validate(RegisterParticipantDtoRequest r) throws ServerException {
        requireText(r.getFirstName(), "firstName");
        requireText(r.getLastName(), "lastName");
        requireText(r.getFirmName(), "firmName");
        validateCredentials(r.getLogin(), r.getPassword());
    }

    private static void validate(RegisterExpertDtoRequest r) throws ServerException {
        requireText(r.getFirstName(), "firstName");
        requireText(r.getLastName(), "lastName");
        validateCredentials(r.getLogin(), r.getPassword());
    }

    private static void validateCredentials(String login, String password) throws ServerException {
        if (login == null || login.length() < MIN_LOGIN_LENGTH || !login.matches("[\\p{L}\\p{N}_]+")) {
            throw new ServerException(ErrorCode.INVALID_LOGIN);
        }
        boolean letter = false;
        boolean digit = false;
        if (password != null) {
            for (char c : password.toCharArray()) {
                letter |= Character.isLetter(c);
                digit |= Character.isDigit(c);
            }
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || !letter || !digit) {
            throw new ServerException(ErrorCode.WEAK_PASSWORD);
        }
    }
}
