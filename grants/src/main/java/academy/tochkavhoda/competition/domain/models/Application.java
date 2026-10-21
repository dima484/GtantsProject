package academy.tochkavhoda.competition.domain.models;

import java.util.*;

public class Application {

    private int id;
    private Participant owner;
    private String title;
    private String description;
    private Set<String> directions;
    private long amount;

    public Application() {
    }

    public Application(int id, Participant owner, String title, String description, Set<String> directions, long amount) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Participant getOwner() {
        return owner;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDirections() {
        return directions;
    }

    public void setDirections(Set<String> directions) {
        this.directions = directions;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
