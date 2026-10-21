package academy.tochkavhoda.competition.application.commands;

import java.util.*;

public class NewApplication {

    private String title;
    private String description;
    private List<String> directions;
    private long amount;

    public NewApplication() {
    }

    public NewApplication(String title, String description, List<String> directions, long amount) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
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

    public List<String> getDirections() {
        return directions;
    }

    public void setDirections(List<String> directions) {
        this.directions = directions;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
