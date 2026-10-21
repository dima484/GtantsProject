package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.ApplicationResult;
import academy.tochkavhoda.competition.domain.models.Application;

import java.util.ArrayList;

/** Преобразование модели заявки в результат сервиса. */
final class ApplicationMapper {

    private ApplicationMapper() {
    }

    static ApplicationResult toResult(Application a, Integer rating) {
        return new ApplicationResult(a.getId(), a.getTitle(), a.getDescription(),
                new ArrayList<>(a.getDirections()), a.getAmount(), a.getOwner().getFirmName(), rating);
    }
}
