package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR


import java.util.ArrayList;
import ch.uzh.ifi.hase.soprafs24.entity.User;

public class Player {

    private Long playerId;
    private String playerName;
    private int cash;
    private Long position;
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<String> cardNames = new ArrayList<>();
    private transient User user;

    private String ultimate;
    private String winCondition;

    private Long userId;
    private Long teammateId;
    private Long gameBoardId;

    private int landYellow;
    private int landCat;
    private int passGoal;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public void addCash(int amount){
        this.cash = this.cash + amount;
    }

    public ArrayList<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(ArrayList<String> itemNames) {
        this.itemNames = itemNames;
    }

    public void addItemNames(String itemName){
        itemNames.add(itemName);
    }

    public void addItemNames(ArrayList<String> itemName){
        this.itemNames.addAll(itemName);
    }

    public void removeItemNames(String itemName){
        this.itemNames.remove(itemName);
    }

    public void removeItemNames(ArrayList<String> itemName){
        itemNames.removeAll(itemName);
    }


    public ArrayList<String> getCardNames() {
        return cardNames;
    }

    public void setCardNames(ArrayList<String> cardNames) {
        this.cardNames = cardNames;
    }

    public void addCardNames(String cardName){
        cardNames.add(cardName);
    }

    public void addCardNames(ArrayList<String> cardName){
        cardNames.addAll(cardName);
    }

    public void removeCardNames(String cardName){
        cardNames.remove(cardName);
    }

    public void removeCardNames(ArrayList<String> cardName){
        cardNames.removeAll(cardName);
    }


    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getUltimate() {
        return ultimate;
    }

    public void setUltimate(String ultimate) {
        this.ultimate = ultimate;
    }

    public String getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(String winCondition) {
        this.winCondition = winCondition;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTeammateId() {
        return teammateId;
    }

    public void setTeammateId(Long teammateId) {
        this.teammateId = teammateId;
    }

    public Long getGameBoardId() {
        return gameBoardId;
    }

    public void setGameBoardId(Long gameBoardId) {
        this.gameBoardId = gameBoardId;
    }

    public int getLandYellow() {
        return landYellow;
    }

    public void setLandYellow(int landYellow) {
        this.landYellow = landYellow;
    }

    public int getLandCat() {
        return landCat;
    }

    public void setLandCat(int landCat) {
        this.landCat = landCat;
    }

    public int getPassGoal() {
        return passGoal;
    }

    public void setPassGoal(int passGoal) {
        this.passGoal = passGoal;
    }

    public boolean getCanWin() {
        return WinConditionUltimate.checkWinConditionMet(this);
    }
}