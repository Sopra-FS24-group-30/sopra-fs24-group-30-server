package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

public class GamePostDTO {

    private LocalDate creationDate;

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
