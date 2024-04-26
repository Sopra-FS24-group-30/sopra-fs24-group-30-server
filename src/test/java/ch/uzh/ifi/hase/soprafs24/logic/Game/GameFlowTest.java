package ch.uzh.ifi.hase.soprafs24.logic.Game;


import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GameFlowTest {


    //use this for only simpe tests which concern cash, position
    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    //use this for tests that require more infos such as the players having cards and items
    private GameFlow extensiveGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        ArrayList itemNames = new ArrayList();
        itemNames.add("OnlyFansAbo");
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);

            //p.setItemNames();
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    @Test
    public void testTeleportFreshStartTeleportsOthersToStart(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"player\": \"others\",\"field\": \"start\"}");
        gameFlow.updatePositions(jsonObject);
        assertEquals(53L,gameFlow.getPlayer(2).getPosition());
        assertEquals(54L,gameFlow.getPlayer(3).getPosition());
        assertEquals(54L,gameFlow.getPlayer(4).getPosition());
    }

    public void testExchangePickpocketStealRandomItemFromPlayer(){
        //GameFlow gameFlow = gameFlowSetup();

    }

}
