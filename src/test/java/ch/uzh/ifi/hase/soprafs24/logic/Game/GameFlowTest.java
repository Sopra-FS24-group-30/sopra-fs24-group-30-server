package ch.uzh.ifi.hase.soprafs24.logic.Game;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController.GameTimer;


import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest
public class GameFlowTest {


    //use this for only simpe tests which concern cash, position
    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
            p.setAchievementProgress(new AchievementProgress((long) i), new GameTimer());
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
            p.setAchievementProgress(new AchievementProgress((long) i, new GameTimer()), new GameTimer());
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
    public void testPayAbsoluteEverything(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"teammate\":\"everything\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(200,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
        assertEquals(0,gameFlow.getPlayer(3).getCash());
        assertEquals(100,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteAllPlayers(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"-5\",\"all\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(105,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
        assertEquals(100,gameFlow.getPlayer(3).getCash());
        assertEquals(100,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteTeammateEnemy(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(95,gameFlow.getPlayer(2).getCash());
        assertEquals(105,gameFlow.getPlayer(3).getCash());
        assertEquals(95,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteTeammateIdOverFlow(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.setTurnPlayerId(3L);
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(105,gameFlow.getPlayer(1).getCash());
        assertEquals(95,gameFlow.getPlayer(2).getCash());
        assertEquals(100,gameFlow.getPlayer(3).getCash());
        assertEquals(95,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteEnemyPlayerEvenTeam(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.setTurnPlayerId(2L);
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(95,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
        assertEquals(95,gameFlow.getPlayer(3).getCash());
        assertEquals(105,gameFlow.getPlayer(4).getCash());
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
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");
        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        expectedItemsPlayer3.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1,gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
        assertEquals(expectedItemsPlayer3,gameFlow.getPlayer(3).getItemNames());

    }

    @Test
    public void testExchangeTreasureChestAllItems(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"2\",\"type\": \"item\",\"selection\": \"all\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");
        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        expectedItemsPlayer3.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1,gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
        assertEquals(expectedItemsPlayer3,gameFlow.getPlayer(3).getItemNames());

    }

    @Test
    public void testTeleportUltimate(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"choice\"}");
        JSONObject choices = new JSONObject("{\"field\": \"40\"}");
        gameFlow.setChoices(choices);
        gameFlow.updatePositions(paras);

        assertEquals(40L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testTeleportRandomPlayer(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"randomPlayer\"}");
        gameFlow.getPlayer(1).setPosition(10L);

        gameFlow.updatePositions(paras);

        assertEquals(30L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testTeleportUltimateFixedValue(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"4\"}");

        gameFlow.updatePositions(paras);

        assertEquals(4L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testExchangeTreasureChestChoiceItem(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject choices = new JSONObject("{\"playerId\": \"2\",\"items\":  [\"OnlyFansAbo\"]}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"choice\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1,gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());

    }

    @Test
    public void testExchangeTreasureChestChoiceCards(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\",\"cards\":  [\"B35\"]}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"choice\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1,gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getCardNames());

    }

    @Test
    public void testExchangeTreasureChestallCards(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\"}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"all\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1,gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getCardNames());
    }

    @Test
    public void testExchangeTreasureChestRandomCard(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\"}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"random\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1,gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getCardNames());
    }

    @Test
    public void testPayPositivePickPocket(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"relative\",\"amount\": {\"others\":\"givenAmount\",\"current\":\"50\"}}");
        gameFlow.updateMoney(jsonObject);

        assertEquals(150,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
        assertEquals(100,gameFlow.getPlayer(3).getCash());
        assertEquals(100,gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayPositiveAbsolutePickPocket(){
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"50\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);

        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(150,gameFlow.getPlayer(2).getCash());
        assertEquals(150,gameFlow.getPlayer(3).getCash());
        assertEquals(150,gameFlow.getPlayer(4).getCash());
    }



    @Test
    public void testgiveCard(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"player\": \"current\",\"card1\": \"random\"}");
        HashMap<Integer,ArrayList<String>> itemChoices = new HashMap<>();
        System.out.println(gameFlow.getPlayer(1).getCardNames().size());
        gameFlow.givePlayerCardRand(jsonObject);
        System.out.println(gameFlow.getPlayer(1).getCardNames().size());

        int expectedItemsPlayer10 = 1;
        ArrayList<String> itemNames = new ArrayList();;

        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        assertEquals(expectedItemsPlayer10, gameFlow.getPlayer(1).getCardNames().size());
        //assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
    }

    @Test
    public void testgiveCardChoice(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"player\": \"current\",\"card\": \"choice\"}");
        HashMap<Integer,ArrayList<String>> itemChoices = new HashMap<>();
        System.out.println(gameFlow.getPlayer(1).getCardNames().size());
        JSONObject choices1 = new JSONObject("{\"card\": \"S2\"}");
        gameFlow.setChoices(choices1);
        String cardvalue = gameFlow.getChoices().getString("card");
        gameFlow.givePlayerCardChoice(jsonObject);

        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("S2");
        ArrayList<String> itemNames = new ArrayList();;

        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        assertEquals(expectedItemsPlayer10, gameFlow.getPlayer(1).getCardNames());
        //assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
    }

    @Test
    public void ExchangePlayerPositions() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject args = new JSONObject("{\"player\": \"current\",\"field\": \"randomPlayer\"}");
        Player[] players = gameFlow.getPlayers();
        String playerSpecialId = args.getString("player");
        ArrayList<Integer> playersToUpdate = new ArrayList<>((int) (long) gameFlow.getTurnPlayerId());
        long first_position = players[(int) (long) gameFlow.getTurnPlayerId()-1].getPosition();
        players[(int) (long) gameFlow.getTurnPlayerId()].setPosition(53L);
        players[(int) (long) gameFlow.getTurnPlayerId()+1].setPosition(53L);
        players[(int) (long) gameFlow.getTurnPlayerId()+2].setPosition(53L);
        gameFlow.exchangePositions(args);

        assertEquals(53L, players[(int) (long) gameFlow.getTurnPlayerId()-1].getPosition());
        //assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
    }


    @Test
    public void ReduceMoneyPLayers() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject args = new JSONObject("{}");
        Player[] players = gameFlow.getPlayers();;
        ArrayList<Integer> playersToUpdate = new ArrayList<>((int) (long) gameFlow.getTurnPlayerId());
        gameFlow.reduceMoneyALL(args);
        assertEquals(95, players[(int) (long) gameFlow.getTurnPlayerId()-1].getCash());

    }


    @Test
    public void exchangeAllfunc() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[0].setTeammateId(2L);
        players[1].setTeammateId(1L);
        players[2].setTeammateId(4L);
        players[3].setTeammateId(3L);
        players[0].setWinCondition("JackSparrow");
        players[1].setWinCondition("JackSparrow");
        players[2].setWinCondition("JackSparrow");
        players[3].setWinCondition("JackSparrow");
        players[0].addCardNames("S2");
        players[0].addCardNames("S5");
        players[2].addItemNames("OwoWhatisThis");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchangeAll();
        assertEquals(2, players[2].getCardNames().size());
        assertEquals(3, players[0].getItemNames().size());
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OwoWhatisThis");
        ArrayList<String> expectedCards = new ArrayList<>();
        expectedCards.add("S2");
        expectedCards.add("S5");
        assertEquals(expectedItemsPlayer10, players[0].getItemNames());
        assertEquals(expectedCards, players[2].getCardNames());
    }

    @Test
    public void allyouritems() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[0].setTeammateId(2L);
        players[1].setTeammateId(1L);
        players[2].setTeammateId(4L);
        players[3].setTeammateId(3L);
        players[0].setWinCondition("JackSparrow");
        players[1].setWinCondition("JackSparrow");
        players[2].setWinCondition("JackSparrow");
        players[3].setWinCondition("JackSparrow");
        players[0].addCardNames("S2");
        players[0].addCardNames("S5");
        players[2].addItemNames("MeowYOU");
        players[2].addItemNames("MeowImOut");
        players[2].addItemNames("UltraMagicMushroom");
        JSONObject param = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 4}}");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchange(param);
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("MeowYOU");
        expectedItemsPlayer10.add("MeowImOut");
        expectedItemsPlayer10.add("UltraMagicMushroom");
        ArrayList<String> expectedCards = new ArrayList<>();
        assertEquals(expectedItemsPlayer10.size(), players[0].getItemNames().size());
        assertEquals(0, players[2].getItemNames().size());
    }

    @Test
    public void allyouritemsLESS4() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[2].addItemNames("MeowImOut");
        players[2].addItemNames("UltraMagicMushroom");
        JSONObject param = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 4}}");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchange(param);
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("MeowImOut");
        expectedItemsPlayer10.add("UltraMagicMushroom");
        ArrayList<String> expectedCards = new ArrayList<>();
        assertEquals(expectedItemsPlayer10.size(), players[0].getItemNames().size());
        assertEquals(0, players[2].getItemNames().size());
    }





}
