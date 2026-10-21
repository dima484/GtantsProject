# Конкурс грантов (сервер в оперативной памяти)

Сборка и тесты: `mvn test`

## Архитектура (пакет `academy.tochkavhoda.competition`)

```
api                      слой представления
  Server.java            json -> DTO запроса -> сервис -> DTO ответа -> ServerResponse
  dto/requests           DTO запросов (ни от чего не зависят)
  dto/responses          ServerResponse, DTO ответов со статическими from(...Result)
application              слой приложения (бизнес-логика, без json и DTO)
  services               UserService, ApplicationService, RatingService, ContestService
  commands               входные данные сервисов (NewApplication)
  results                результаты сервисов (ApplicationResult, GrantResult, ...)
domain                   ядро
  Database.java          коллекции в памяти (синглтон)
  models                 User, Participant, Expert, Application, Rating, ...
  exceptions             ServerException, ErrorCode
```

Зависимости только внутрь: api -> application -> domain. Domain не знает ни об application, ни об api;
сервисы не знают про json и DTO; DTO запросов не знают ни о чем.

## Методы Server
| Метод | Кто | Запрос |
|---|---|---|
| registerParticipant(json) | все | firstName, lastName, firmName, login, password |
| registerExpert(json) | все | firstName, lastName, directions[], login, password |
| login(json) | все | login, password -> token |
| logout(token), leave(token) | любой | - |
| getParticipants(token, json) | любой | directions[] (необязательно) |
| addApplication / addApplications(token, json) | участник | title, description, directions[], amount (или applications[]) |
| cancelApplication(token, json) | участник | applicationId |
| getMyApplications(token) | участник | - |
| getApplications(token, json) | эксперт | directions[] (необязательно, только из своих) |
| rate / removeRating(token, json) | эксперт | applicationId, rating (1..5) |
| getRatedApplications(token) | эксперт | - |
| finishContest(json) | момент X | fund, threshold |
| getResults(token) | любой | - |

## Принятые решения
- Логин общий для участников и экспертов; логин >= 5 символов (буквы, цифры, `_`), пароль >= 8 символов, буква и цифра.
- Направления приводятся к нижнему регистру. Эксперт может оценивать заявку, если у неё есть общее с ним направление.
- Повторный login выдает новый токен, старый перестает действовать.
- После `finishContest` подача, отмена заявок и оценки запрещены. Заявки без оценок в итогах не участвуют.
- Заявка со средней оценкой <= threshold и все следующие за ней не финансируются.
- Новый `new Server()` очищает базу.
