package ch.uzh.ifi.hase.soprafs24.logic.Returns;//NOSONAR

import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * this class is only to be used if you update all the players
 */


/**
 * this class is only to be used if you update all the players
 */

public class CashData {

    @JsonProperty("1")
    private PlayerCash player1 = new PlayerCash();
    @JsonProperty("2")
    private PlayerCash player2 = new PlayerCash();
    @JsonProperty("3")
    private PlayerCash player3 = new PlayerCash();
    @JsonProperty("4")
    private PlayerCash player4 = new PlayerCash();

    public CashData(){
        //needed to not break old code
    }

    public CashData(GameFlow gameFlow){
        player1setNewAmountOfMoney(gameFlow.getPlayer(1).getCash());
        player2setNewAmountOfMoney(gameFlow.getPlayer(2).getCash());
        player3setNewAmountOfMoney(gameFlow.getPlayer(3).getCash());
        player4setNewAmountOfMoney(gameFlow.getPlayer(4).getCash());
        player1setChangeAmountOfMoney(0);
        player2setChangeAmountOfMoney(0);
        player3setChangeAmountOfMoney(0);
        player4setChangeAmountOfMoney(0);
    }

    public void setPlayersNewCash(int cash1, int cash2, int cash3, int cash4){
        player1.setNewAmountOfMoney(cash1);
        player2.setNewAmountOfMoney(cash2);
        player3.setNewAmountOfMoney(cash3);
        player4.setNewAmountOfMoney(cash4);
    }

    public void setPlayersChangeAmount(int cash1, int cash2, int cash3, int cash4){
        player1.setChangeAmountOfMoney(cash1);
        player2.setChangeAmountOfMoney(cash2);
        player3.setChangeAmountOfMoney(cash3);
        player4.setChangeAmountOfMoney(cash4);
    }

    public CashData setupCashDataCurrent(GameFlow gameFlow){
        CashData cashData = new CashData();
        cashData.player2setNewAmountOfMoney(gameFlow.getPlayer(2).getCash());
        cashData.player3setNewAmountOfMoney(gameFlow.getPlayer(3).getCash());
        cashData.player1setNewAmountOfMoney(gameFlow.getPlayer(1).getCash());
        cashData.player4setNewAmountOfMoney(gameFlow.getPlayer(4).getCash());
        cashData.player1setChangeAmountOfMoney(0);
        cashData.player2setChangeAmountOfMoney(0);
        cashData.player4setChangeAmountOfMoney(0);
        cashData.player3setChangeAmountOfMoney(0);
        return cashData;
    }

    public void setPlayerAmount(int playerId, int cashChange){
        switch (playerId){//NOSONAR
            case 1:
                player1.setChangeAmountOfMoney(cashChange);
                break;
            case 2:
                player2.setChangeAmountOfMoney(cashChange);
                break;
            case 3:
                player3.setChangeAmountOfMoney(cashChange);
                break;
            case 4:
                player4.setChangeAmountOfMoney(cashChange);
                break;
        }
    }

    public void setPlayerAmountAndUpdate(int playerId, int newCash, int cashChange){
        switch (playerId){//NOSONAR
            case 1:
                player1.setNewAmountOfMoney(newCash);
                player1.setChangeAmountOfMoney(cashChange);
                break;
            case 2:
                player2.setNewAmountOfMoney(newCash);
                player2.setChangeAmountOfMoney(cashChange);
                break;
            case 3:
                player3.setNewAmountOfMoney(newCash);
                player3.setChangeAmountOfMoney(cashChange);
                break;
            case 4:
                player4.setNewAmountOfMoney(newCash);
                player4.setChangeAmountOfMoney(cashChange);
                break;
        }
    }

    //userId and positive
    public HashMap<Long,Integer> checkNegativeChanges(){//NOSONAR
        HashMap<Long,Integer> negativeUpdates = new HashMap<>();
        if(player1.getChangeAmountOfMoney() < 0){
            negativeUpdates.put(1L,player1.getChangeAmountOfMoney());
        }
        if(player2.getChangeAmountOfMoney() < 0){
            negativeUpdates.put(2L,player2.getChangeAmountOfMoney());
        }
        if(player3.getChangeAmountOfMoney() < 0){
            negativeUpdates.put(3L,player3.getChangeAmountOfMoney());
        }
        if(player4.getChangeAmountOfMoney() < 0){
            negativeUpdates.put(4L,player4.getChangeAmountOfMoney());
        }
        return negativeUpdates;
    }

    public void player1setNewAmountOfMoney(int cash){
        player1.setNewAmountOfMoney(cash);
    }

    public void player2setNewAmountOfMoney(int cash){
        player2.setNewAmountOfMoney(cash);
    }

    public void player3setNewAmountOfMoney(int cash){
        player3.setNewAmountOfMoney(cash);
    }

    public void player4setNewAmountOfMoney(int cash){
        player4.setNewAmountOfMoney(cash);
    }

    public void player1setChangeAmountOfMoney(int cash){
        player1.setChangeAmountOfMoney(cash);
    }

    public void player2setChangeAmountOfMoney(int cash){
        player2.setChangeAmountOfMoney(cash);
    }

    public void player3setChangeAmountOfMoney(int cash){
        player3.setChangeAmountOfMoney(cash);
    }

    public void player4setChangeAmountOfMoney(int cash){
        player4.setChangeAmountOfMoney(cash);
    }

}