package ch.uzh.ifi.hase.soprafs24.logic.Game;


import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GameFlowTest {


    //use this for only simpe tests which concern cash, position
    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
            p.setAchievementProgress(new AchievementProgress((long) i));
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
        for(int i=1; i<=4; i++){

            ArrayList<String> itemNames = new ArrayList();
            itemNames.add("OnlyFansAbo");
            Player p = new Player();
            p.setUserId((long)i);
            p.setAchievementProgress(new AchievementProgress((long) i));
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            p.setItemNames(itemNames);
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
    @Test
    public void testPayAbsoluteOnlyFansAbo(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"-7\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(121,gameFlow.getPlayer(1).getCash());
        assertEquals(93,gameFlow.getPlayer(2).getCash());
        assertEquals(93,gameFlow.getPlayer(3).getCash());
        assertEquals(93,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayPickPocket(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"relative\",\"amount\": {\"others\":\"-50\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(250,gameFlow.getPlayer(1).getCash());
        assertEquals(50,gameFlow.getPlayer(2).getCash());
        assertEquals(50,gameFlow.getPlayer(3).getCash());
        assertEquals(50,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testExchangeTreasureChest(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"2\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 1}}");
        HashMap<Integer,ArrayList<String>> itemChoices = new HashMap<>();
        gameFlow.exchange(jsonObject,itemChoices);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");
        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        expectedItemsPlayer3.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1,gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
        assertEquals(expectedItemsPlayer3,gameFlow.getPlayer(3).getItemNames());

    }


}
