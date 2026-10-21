package academy.tochkavhoda.competition.domain.models;

import java.util.Collections;
import java.util.Set;

/** Эксперт, оценивающий заявки. Данные после регистрации не меняются. */
public class Expert extends User {

    private final Set<String> directions;

    public Expert(String firstName, String lastName, Set<String> directions, String login, String password) {
        super(firstName, lastName, login, password);
        this.directions = Collections.unmodifiableSet(directions);
    }

    public Set<String> getDirections() {
        return directions;
    }
}
