package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Rating;

import java.util.List;

public interface RatingDao {

    /** Добавляет оценку или заменяет существующую оценку этого эксперта для этой заявки. */
    void put(Rating rating);

    Rating get(int applicationId, String expertLogin);

    void delete(int applicationId, String expertLogin) throws ServerException;

    List<Rating> getByApplication(int applicationId);

    List<Rating> getByExpert(String expertLogin);
}
