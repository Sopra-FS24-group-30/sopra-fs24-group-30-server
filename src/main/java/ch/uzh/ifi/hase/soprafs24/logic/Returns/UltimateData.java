package ch.uzh.ifi.hase.soprafs24.logic.Returns;//NOSONAR

import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;

public class UltimateData {

    String name;
    Boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void prepareData(String ultimateName, Boolean isActive){
        this.name = ultimateName;
        this.active = isActive;
    }

    public void prepareDataForCurrentPlayer(GameFlow gameFlow){
        name = gameFlow.getActivePlayer().getUltimate();
        active = gameFlow.getActivePlayer().isUltActive();
    }
}
