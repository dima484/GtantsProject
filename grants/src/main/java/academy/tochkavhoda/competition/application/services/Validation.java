package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.domain.exceptions.ErrorCode;
import academy.tochkavhoda.competition.domain.exceptions.ServerException;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Общие проверки входных данных и ролей для сервисов. */
final class Validation {

    private Validation() {
    }

    static void requireText(String value, String field) throws ServerException {
        if (value == null || value.trim().isEmpty()) {
            throw new ServerException(ErrorCode.EMPTY_FIELD, field);
        }
    }

    /** Приводит направления к единому виду: без пробелов по краям, в нижнем регистре, без повторов. */
    static Set<String> normalizeDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ErrorCode.INVALID_DIRECTIONS);
        }
        Set<String> result = new LinkedHashSet<>();
        for (String d : directions) {
            if (d == null || d.trim().isEmpty()) {
                throw new ServerException(ErrorCode.INVALID_DIRECTIONS);
            }
            result.add(d.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    static Participant asParticipant(User user) throws ServerException {
        if (!(user instanceof Participant)) {
            throw new ServerException(ErrorCode.ACCESS_DENIED);
        }
        return (Participant) user;
    }

    static Expert asExpert(User user) throws ServerException {
        if (!(user instanceof Expert)) {
            throw new ServerException(ErrorCode.ACCESS_DENIED);
        }
        return (Expert) user;
    }

    static boolean intersects(Set<String> a, Set<String> b) {
        for (String s : a) {
            if (b.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
