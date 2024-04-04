package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Set;

public class GameBoardGetDTO {

    private Long id;
    private String token;
    private LocalDate creationDate;
    private GameBoardStatus status;
    private Set<GameBoardSpace> spaces;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public GameBoardStatus getStatus() {
        return status;
    }

    public void setStatus(GameBoardStatus status) {
        this.status = status;
    }

    public Set<GameBoardSpace> getSpaces() {
        return spaces;
    }
    public void setSpaces(Set<GameBoardSpace> spaces) {
        this.spaces = spaces;
    }
}