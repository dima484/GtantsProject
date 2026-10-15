package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.model.Application;

import java.util.ArrayList;

/** Преобразование модели заявки в DTO ответа (общее для нескольких сервисов). */
final class ApplicationMapper {

    private ApplicationMapper() {
    }

    static ApplicationDtoResponse toDto(Application a, Integer rating) {
        return new ApplicationDtoResponse(a.getId(), a.getTitle(), a.getDescription(),
                new ArrayList<>(a.getDirections()), a.getAmount(), a.getOwner().getFirmName(), rating);
    }
}
