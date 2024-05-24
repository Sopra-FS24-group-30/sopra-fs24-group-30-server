package ch.uzh.ifi.hase.soprafs24.logic.Returns;//NOSONAR

import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;

public class TurnActiveData {

    Integer currentTurn;
    String activePlayer;

    public Integer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
    }

    public static TurnActiveData prepareData(GameFlow gameFlow){
        TurnActiveData turnActiveData = new TurnActiveData();
        turnActiveData.setCurrentTurn(gameFlow.getCurrentTurn());
        turnActiveData.setActivePlayer(gameFlow.getTurnPlayerId().toString());
        return turnActiveData;
    }
}
