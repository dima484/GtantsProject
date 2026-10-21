package academy.tochkavhoda.competition.domain.exceptions;

public enum ErrorCode {
    INVALID_JSON("Некорректный формат запроса"),
    EMPTY_FIELD("Обязательное поле не заполнено"),
    INVALID_LOGIN("Логин должен содержать не менее 5 символов: букв, цифр или знаков подчеркивания"),
    WEAK_PASSWORD("Пароль должен содержать не менее 8 символов, среди них хотя бы одну букву и одну цифру"),
    LOGIN_ALREADY_USED("Этот логин уже используется"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    INVALID_TOKEN("Недействительный токен"),
    ACCESS_DENIED("Операция недоступна для этого пользователя"),
    INVALID_DIRECTIONS("Список направлений должен быть непустым и не содержать пустых значений"),
    FOREIGN_DIRECTION("Направление не входит в число направлений эксперта"),
    INVALID_AMOUNT("Запрашиваемая сумма должна быть положительной"),
    INVALID_RATING("Оценка должна быть от 1 до 5"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    RATING_NOT_FOUND("Оценка для этой заявки не найдена"),
    CONTEST_CLOSED("Подача заявок и оценок завершена"),
    CONTEST_NOT_FINISHED("Итоги конкурса еще не подведены"),
    CONTEST_ALREADY_FINISHED("Итоги конкурса уже подведены"),
    INVALID_FUND("Фонд должен быть положительным, а порог оценки - в диапазоне от 0 до 5");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
