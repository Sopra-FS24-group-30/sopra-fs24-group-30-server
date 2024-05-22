package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;

public class UltimateData {

    String ultimateName;
    Boolean isActive;

    public String getUltimateName() {
        return ultimateName;
    }

    public void setUltimateName(String ultimateName) {
        this.ultimateName = ultimateName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void prepareData(String ultimateName, Boolean isActive){
        this.ultimateName = ultimateName;
        this.isActive = isActive;
    }

    public void prepareDataForCurrentPlayer(GameFlow gameFlow){
        ultimateName = gameFlow.getActivePlayer().getUltimate();
        isActive = gameFlow.getActivePlayer().isUltActive();
    }
}
