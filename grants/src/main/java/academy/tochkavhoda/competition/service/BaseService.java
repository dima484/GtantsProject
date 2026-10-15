package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.*;

/** Общие утилиты сервисов: разбор json, формирование ответа, общие проверки. */
public abstract class BaseService {

    protected static final Gson GSON = new Gson();

    protected interface Action {
        /** Возвращает DTO ответа (или null, если ответ пустой). */
        Object run() throws ServerException;
    }

    /** Выполняет действие и превращает результат или исключение в ServerResponse. */
    protected ServerResponse execute(Action action) {
        try {
            Object result = action.run();
            return new ServerResponse(200, result == null ? "{}" : GSON.toJson(result));
        } catch (ServerException e) {
            return new ServerResponse(400, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
        }
    }

    protected static <T> T parse(String json, Class<T> cls) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
        try {
            T result = GSON.fromJson(json, cls);
            if (result == null) {
                throw new ServerException(ErrorCode.INVALID_JSON);
            }
            return result;
        } catch (JsonParseException e) {
            throw new ServerException(ErrorCode.INVALID_JSON);
        }
    }

    /** Для методов, у которых тело запроса необязательно. Возвращает null, если тела нет. */
    protected static <T> T parseOptional(String json, Class<T> cls) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return parse(json, cls);
    }

    protected static void requireText(String value, String field) throws ServerException {
        if (value == null || value.trim().isEmpty()) {
            throw new ServerException(ErrorCode.EMPTY_FIELD, field);
        }
    }

    /** Приводит направления к единому виду: без пробелов по краям, в нижнем регистре, без повторов. */
    protected static Set<String> normalizeDirections(List<String> directions) throws ServerException {
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

    protected static Participant asParticipant(User user) throws ServerException {
        if (!(user instanceof Participant)) {
            throw new ServerException(ErrorCode.ACCESS_DENIED);
        }
        return (Participant) user;
    }

    protected static Expert asExpert(User user) throws ServerException {
        if (!(user instanceof Expert)) {
            throw new ServerException(ErrorCode.ACCESS_DENIED);
        }
        return (Expert) user;
    }

    protected static boolean intersects(Set<String> a, Set<String> b) {
        for (String s : a) {
            if (b.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
