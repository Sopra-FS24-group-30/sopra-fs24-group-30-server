package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR


import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

public class Player {

    private Long playerId;
    private String playerName;
    private int cash;
    private Long position;
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<String> cardNames = new ArrayList<>();
    private transient User user;
    private PlayerStatus status;

    private String ultimate;
    private String winCondition;

    private Long userId;
    private Long teammateId;
    private Long gameBoardId;

    private int landYellow;
    private int landCat;
    private int passGoal;
    private boolean canWinner;

    private boolean ultActive;
    private AchievementProgress achievementProgress;


    public Player(){

    }
    public Player(Long userId){
        this.userId = userId;
        achievementProgress = new AchievementProgress(userId);
    }

    public AchievementProgress getAchievementProgress() {
        return achievementProgress;
    }

    public void setAchievementProgress(AchievementProgress achievementProgress) {
        this.achievementProgress = achievementProgress;
    }

    public boolean isUltActive() {
        return ultActive;
    }

    public void setUltActive(boolean ultActive) {
        this.ultActive = ultActive;
        if(!this.ultActive){
            achievementProgress.setUltimateUsed(true);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setStatus(PlayerStatus status){this.status = status; }

    public PlayerStatus getStatus(){return status; }

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
        if(this.cash > achievementProgress.getMaxAmountCash()){
            achievementProgress.setMaxAmountCash(this.cash);
        }
    }

    public void addCash(int amount){
        setCash(this.cash + amount);

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

    public void addLandYellow() {
        this.landYellow = this.landYellow + 1;
    }

    public int getLandCat() {
        return landCat;
    }

    public void setLandCat(int landCat) {
        this.landCat = landCat;
    }

    public void addLandCat() {
        this.landCat = this.landCat + 1;
    }

    public int getPassGoal() {
        return passGoal;
    }

    public void setPassGoal(int passGoal) {
        this.passGoal = passGoal;
    }

    public void addPassGoal() {
        this.passGoal = this.passGoal + 1;
    }

    public boolean getCanWin() {
        return WinConditionUltimate.checkWinConditionMet(this);
    }

    public boolean getCanWinner() {
        return canWinner;
    }

    public void setCanWinner(boolean canWinner) {
        this.canWinner = canWinner;
    }
}