package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ContestDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.ContestResult;

public class ContestDaoImpl implements ContestDao {

    private final Database database = Database.getInstance();

    @Override
    public boolean isFinished() {
        return database.isContestFinished();
    }

    @Override
    public void setResult(ContestResult result) {
        database.setContestResult(result);
    }

    @Override
    public ContestResult getResult() {
        return database.getContestResult();
    }

    @Override
    public void clear() {
        database.clear();
    }
}
