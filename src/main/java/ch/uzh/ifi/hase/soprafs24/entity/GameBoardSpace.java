package ch.uzh.ifi.hase.soprafs24.entity;

public class GameBoardSpace {
    private Long spaceId;
    private String spaceType;
    private Double xCoordinate;
    private Double yCoordinate;
    private Boolean playerON;
    private GameBoardSpace prev1Space;
    private GameBoardSpace prev2Space;
    private GameBoardSpace next1Space;
    private GameBoardSpace next2Space;

    public GameBoardSpace(Long spaceId, String spaceType, Double xCoordinate, Double yCoordinate){
        this.spaceId = spaceId;
        this.spaceType = spaceType;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Long getSpaceId(){
        return spaceId;
    }
    public void setSpaceId(Long spaceId){
        this.spaceId = spaceId;
    }
    public String getSpaceType(){
        return spaceType;
    }
    public void setSpaceType(String spaceType){
        this.spaceType = spaceType;
    }
    public Double getXCoordinate(){
        return xCoordinate;
    }
    public void setXCoordinate(Double xCoordinate){
        this.xCoordinate = xCoordinate;
    }
    public Double getYCoordinate(){
        return yCoordinate;
    }
    public void setYCoordinate(Double yCoordinate){
        this.yCoordinate = yCoordinate;
    }
    public Boolean getPlayerON() {
        return playerON;
    }
    public void setPlayerON(Boolean playerON) {
        this.playerON = playerON;
    }
    public GameBoardSpace getPrev1Space(){
        return prev1Space;
    }
    public void setPrev1Space(GameBoardSpace prev1Space){
        this.prev1Space = prev1Space;
    }
    public GameBoardSpace getPrev2Space(){
        return prev2Space;
    }
    public void setPrev2Space(GameBoardSpace prev2Space){
        this.prev2Space = prev2Space;
    }
    public GameBoardSpace getNext1Space(){
        return next1Space;
    }
    public void setNext1Space(GameBoardSpace next1Space) {
        this.next1Space = next1Space;
    }
    public GameBoardSpace getNext2Space(){
        return next2Space;
    }
    public void setNext2Space(GameBoardSpace next2Space){
        this.next2Space = next2Space;
    }
}
