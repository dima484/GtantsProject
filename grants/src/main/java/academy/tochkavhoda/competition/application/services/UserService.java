package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.LoginResult;
import academy.tochkavhoda.competition.application.results.ParticipantResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.Application;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static academy.tochkavhoda.competition.application.services.Validation.*;

public class UserService {

    private static final int MIN_LOGIN_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final Database database;

    public UserService(Database database) {
        this.database = database;
    }

    public void registerParticipant(String firstName, String lastName, String firmName,
                                    String login, String password) throws ServerException {
        requireText(firstName, "firstName");
        requireText(lastName, "lastName");
        requireText(firmName, "firmName");
        validateCredentials(login, password);
        database.insertUser(new Participant(firstName.trim(), lastName.trim(), firmName.trim(), login, password));
    }

    public void registerExpert(String firstName, String lastName, List<String> directions,
                               String login, String password) throws ServerException {
        requireText(firstName, "firstName");
        requireText(lastName, "lastName");
        validateCredentials(login, password);
        Set<String> normalized = normalizeDirections(directions);
        database.insertUser(new Expert(firstName.trim(), lastName.trim(), normalized, login, password));
    }

    public LoginResult login(String login, String password) throws ServerException {
        requireText(login, "login");
        requireText(password, "password");
        User user = database.getUserByLogin(login);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ServerException(ErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        return new LoginResult(database.login(user));
    }

    public void logout(String token) throws ServerException {
        database.getUserByToken(token);
        database.logout(token);
    }

    /** Пользователь покидает сервер: удаляется он сам и все его заявки (оценки). */
    public void leave(String token) throws ServerException {
        database.deleteUser(database.getUserByToken(token));
    }

    /** Список участников; если переданы направления - только тех, у кого есть заявки по ним. */
    public List<ParticipantResult> getParticipants(String token, List<String> directions) throws ServerException {
        database.getUserByToken(token);
        boolean filter = directions != null && !directions.isEmpty();
        Set<String> normalized = filter ? normalizeDirections(directions) : null;
        List<ParticipantResult> result = new ArrayList<>();
        for (Participant p : database.getParticipants()) {
            if (!filter || hasApplicationIn(p, normalized)) {
                result.add(new ParticipantResult(p.getFirstName(), p.getLastName(), p.getFirmName()));
            }
        }
        return result;
    }

    private boolean hasApplicationIn(Participant participant, Set<String> directions) {
        for (Application a : database.getApplications()) {
            if (a.getOwner() == participant && intersects(a.getDirections(), directions)) {
                return true;
            }
        }
        return false;
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
