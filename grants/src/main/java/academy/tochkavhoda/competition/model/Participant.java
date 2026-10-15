package academy.tochkavhoda.competition.model;

/** Участник конкурса (фирма, претендующая на грант). Данные после регистрации не меняются. */
public class Participant extends User {

    private final String firmName;

    public Participant(String firstName, String lastName, String firmName, String login, String password) {
        super(firstName, lastName, login, password);
        this.firmName = firmName;
    }

    public String getFirmName() {
        return firmName;
    }
}
