package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import java.util.Set;

public class GameBoardPostDTO {

    private Long id;

    private Set<GameBoardSpace> spaces;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<GameBoardSpace> getSpaces() {
        return spaces;
    }

    public void setSpaces(Set<GameBoardSpace> spaces) {
        this.spaces = spaces;
    }
}