package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR


import java.util.List;

public class Player {

    private Long playerId;
    private String playerName;
    private int cash;
    private Long position;

    private List<Card> cards;
    private List<Item> items;
    private Ultimate ultimate;
    private WinCondition winCondition;

    private Long userId;
    private Long teammateId;
    private Long gameBoardId;

    private int landYellow;
    private int landCat;


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

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public Ultimate getUltimate() {
        return ultimate;
    }

    public void setUltimate(Ultimate ultimate) {
        this.ultimate = ultimate;
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

    public WinCondition getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(WinCondition winCondition) {
        this.winCondition = winCondition;
    }

    public boolean getCanWin() {
        return winCondition.checkWinConditionMet(this);
    }
}
