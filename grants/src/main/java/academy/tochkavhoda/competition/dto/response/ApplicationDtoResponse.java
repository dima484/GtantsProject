package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ApplicationDtoResponse {

    private int id;
    private String title;
    private String description;
    private List<String> directions;
    private long amount;
    private String firmName;
    private Integer rating;

    public ApplicationDtoResponse() {
    }

    public ApplicationDtoResponse(int id, String title, String description, List<String> directions, long amount, String firmName, Integer rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
        this.firmName = firmName;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
