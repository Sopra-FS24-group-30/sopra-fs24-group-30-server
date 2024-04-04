package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "GAMEBOARDSPACE")
public class GameBoardSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    private String spaceType;
    private Double xCoordinate;
    private Double yCoordinate;
    private Boolean playerON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameBoard_id", nullable = true) // Allowing null for cases without a gameBoard
    private GameBoard gameBoard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prev1Space_id", referencedColumnName = "spaceId")
    private GameBoardSpace prev1Space;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prev2Space_id", referencedColumnName = "spaceId")
    private GameBoardSpace prev2Space;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "next1Space_id", referencedColumnName = "spaceId")
    private GameBoardSpace next1Space;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "next2Space_id", referencedColumnName = "spaceId")
    private GameBoardSpace next2Space;

    // Constructor for initial setup without relational fields
    public GameBoardSpace(Long spaceId, String spaceType, Double xCoordinate, Double yCoordinate) {
        this.spaceId = spaceId;
        this.spaceType = spaceType;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    // Default constructor required by JPA
    public GameBoardSpace() {}

    // Getters and Setters

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public Double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
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

    public GameBoardSpace getPrev1Space() {
        return prev1Space;
    }

    public void setPrev1Space(GameBoardSpace prev1Space) {
        this.prev1Space = prev1Space;
    }

    public GameBoardSpace getPrev2Space() {
        return prev2Space;
    }

    public void setPrev2Space(GameBoardSpace prev2Space) {
        this.prev2Space = prev2Space;
    }

    public GameBoardSpace getNext1Space() {
        return next1Space;
    }

    public void setNext1Space(GameBoardSpace next1Space) {
        this.next1Space = next1Space;
    }

    public GameBoardSpace getNext2Space() {
        return next2Space;
    }

    public void setNext2Space(GameBoardSpace next2Space) {
        this.next2Space = next2Space;
    }
}
