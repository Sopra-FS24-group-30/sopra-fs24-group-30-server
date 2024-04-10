package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "GAMEBOARDSPACE")
public class GameBoardSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // uniquely identify each instance of space, e.g. (222201) for game 2222 space 01
    private Long uniqueId;

    private Long spaceId;

    private Double xCoord;

    private Double yCoord;

    private Boolean playerON;

    private String color;

    private Boolean isGoal;

    private String onSpace;

    private String overSpace;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String>next;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String>prev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameBoard_id")
    @JsonIgnore
    private GameBoard gameBoard;

    public GameBoardSpace() {

    }

    public Long getuniqueId() {
        return uniqueId;
    }

    public void setuniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Double getxCoord() {
        return xCoord;
    }

    public void setxCoord(Double xCoord) {
        this.xCoord = xCoord;
    }

    public Double getyCoord() {
        return yCoord;
    }

    public void setyCoord(Double yCoord) {
        this.yCoord = yCoord;
    }

    public Boolean getPlayerON() {
        return playerON;
    }

    public void setPlayerON(Boolean playerON) {
        this.playerON = playerON;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsGoal() {
        return isGoal;
    }

    public void setIsGoal(Boolean isGoal) {
        this.isGoal = isGoal;
    }

    public String getOnSpace() {
        return onSpace;
    }

    public void setOnSpace(String onSpace) {
        this.onSpace = onSpace;
    }

    public String getOverSpace() {
        return overSpace;
    }

    public void setOverSpace(String overSpace) {
        this.overSpace = overSpace;
    }

    public List<String> getNext() {
        return next;
    }

    public void setNext(List<String> next) {
        this.next = next;
    }

    public List<String> getPrev() {
        return prev;
    }

    public void setPrev(List<String> prev) {
        this.prev = prev;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
