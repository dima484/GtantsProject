package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.ContestResult;

public interface ContestDao {

    boolean isFinished();

    void setResult(ContestResult result);

    ContestResult getResult();

    /** Удаляет все данные (участников, экспертов, заявки, оценки, итоги). */
    void clear();
}
