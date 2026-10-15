package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

import java.util.List;

public interface ApplicationDao {

    int insert(Application application);

    Application get(int id) throws ServerException;

    void delete(int id);

    List<Application> getAll();
}
