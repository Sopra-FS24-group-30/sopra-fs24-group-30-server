package ch.uzh.ifi.hase.soprafs24.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "GAMEBOARDSPACE")
public class GameBoardSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    private Double xCoord;
    private Double yCoord;
    private Boolean playerON;
    private String color;
    private Boolean isGoal;
    private String onSpace;
    private String overSpace;
    //possible error beacuse of the Element leads to non-entitiy elements
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String>next;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String>prev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameBoard_id", nullable = true) // Allowing null for cases without a gameBoard
    private GameBoard gameBoard;





    // Constructor for initial setup without relational fields
    public GameBoardSpace(Long spaceId, Double xCoord, Double yCoord) {
        this.spaceId = spaceId;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    // Default constructor required by JPA
    public GameBoardSpace() {}

    // Getters and Setters

    public Long getSpaceId() {
        return spaceId;
    }
    public List<String> getNext() {
        return next;
    }
    public List<String> getPrev() {
        return prev;
    }

    public void setNext(List<String> next) {
        this.next = next;
    }

    public String getOnSpace() {
        return onSpace;
    }
    public String getOverSpace() {
        return overSpace;
    }
    public void setOnSpace(String onSpace) {
        this.onSpace = onSpace;
    }
    public void setOverSpace(String overSpace) {
        this.overSpace = overSpace;
    }

    public void setPrev(List<String> prev) {
        this.prev = prev;
    }

    public String getColor() {
        return color;
    }

    public Boolean getIsGoal() {
        return isGoal;
    }

    public void setIsGoal(Boolean isGoal) {
        this.isGoal = isGoal;
    }

    public void setColor(String color) {
        this.color = color;
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }


}
