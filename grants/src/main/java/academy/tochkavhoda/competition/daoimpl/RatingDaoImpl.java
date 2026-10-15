package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Rating;

import java.util.List;

public class RatingDaoImpl implements RatingDao {

    private final Database database = Database.getInstance();

    @Override
    public void put(Rating rating) {
        database.putRating(rating);
    }

    @Override
    public Rating get(int applicationId, String expertLogin) {
        return database.getRating(applicationId, expertLogin);
    }

    @Override
    public void delete(int applicationId, String expertLogin) throws ServerException {
        database.deleteRating(applicationId, expertLogin);
    }

    @Override
    public List<Rating> getByApplication(int applicationId) {
        return database.getRatingsByApplication(applicationId);
    }

    @Override
    public List<Rating> getByExpert(String expertLogin) {
        return database.getRatingsByExpert(expertLogin);
    }
}
