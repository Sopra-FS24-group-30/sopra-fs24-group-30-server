package ch.uzh.ifi.hase.soprafs24.logic.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.*;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
@SpringBootTest
public class GameFlowTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Mock
    private static SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private GameWebSocketController gameWebSocketController;
    @Mock
    private GameManagementService gameManagementService;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private JSONObject dinoChickyParas = new JSONObject("{\"player\": \"current\", \"cash\":  \"-20\"}");

    //use this for only simpe tests which concern cash, position
    private GameFlow basicGameFlowSetup() {
        GameFlow gameFlow = new GameFlow();
        for (int i = 1; i <= 4; i++) {
            Player p = new Player();
            p.setUserId((long) i);
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
    private GameFlow extensiveGameFlowSetup() {
        GameFlow gameFlow = new GameFlow();
        gameFlow.setGameBoard();
        for (int i = 1; i <= 4; i++) {

            ArrayList<String> itemNames = new ArrayList();
            itemNames.add("OnlyFansAbo");
            Player p = new Player();
            p.setUserId((long) i);
            p.setAchievementProgress(new AchievementProgress((long) i));
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            p.setWinCondition("Golden");
            p.setItemNames(itemNames);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    @Test
    public void testTeleportFreshStartTeleportsOthersToStart() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"player\": \"others\",\"field\": \"start\"}");
        gameFlow.updatePositions(jsonObject);
        assertEquals(53L, gameFlow.getPlayer(2).getPosition());
        assertEquals(54L, gameFlow.getPlayer(3).getPosition());
        assertEquals(54L, gameFlow.getPlayer(4).getPosition());
    }

    @Test
    public void testPayAbsoluteOnlyFansAbo() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"-7\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(121, gameFlow.getPlayer(1).getCash());
        assertEquals(93, gameFlow.getPlayer(2).getCash());
        assertEquals(93, gameFlow.getPlayer(3).getCash());
        assertEquals(93, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteEverything() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"teammate\":\"everything\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(200, gameFlow.getPlayer(1).getCash());
        assertEquals(100, gameFlow.getPlayer(2).getCash());
        assertEquals(0, gameFlow.getPlayer(3).getCash());
        assertEquals(100, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteAllPlayers() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"-5\",\"all\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(105, gameFlow.getPlayer(1).getCash());
        assertEquals(100, gameFlow.getPlayer(2).getCash());
        assertEquals(100, gameFlow.getPlayer(3).getCash());
        assertEquals(100, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteTeammateEnemy() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(100, gameFlow.getPlayer(1).getCash());
        assertEquals(95, gameFlow.getPlayer(2).getCash());
        assertEquals(105, gameFlow.getPlayer(3).getCash());
        assertEquals(95, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteTeammateIdOverFlow() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.setTurnPlayerId(3L);
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(105, gameFlow.getPlayer(1).getCash());
        assertEquals(95, gameFlow.getPlayer(2).getCash());
        assertEquals(100, gameFlow.getPlayer(3).getCash());
        assertEquals(95, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayAbsoluteEnemyPlayerEvenTeam() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.setTurnPlayerId(2L);
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"enemy\":\"-5\",\"teammate\":\"5\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(95, gameFlow.getPlayer(1).getCash());
        assertEquals(100, gameFlow.getPlayer(2).getCash());
        assertEquals(95, gameFlow.getPlayer(3).getCash());
        assertEquals(105, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayPickPocket() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"relative\",\"amount\": {\"others\":\"-50\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);
        assertEquals(250, gameFlow.getPlayer(1).getCash());
        assertEquals(50, gameFlow.getPlayer(2).getCash());
        assertEquals(50, gameFlow.getPlayer(3).getCash());
        assertEquals(50, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testExchangeTreasureChest() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"2\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");
        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        expectedItemsPlayer3.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1, gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getItemNames());
        assertEquals(expectedItemsPlayer3, gameFlow.getPlayer(3).getItemNames());

    }

    @Test
    public void testExchangeTreasureChestAllItems() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"2\",\"type\": \"item\",\"selection\": \"all\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");
        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        expectedItemsPlayer3.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1, gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getItemNames());
        assertEquals(expectedItemsPlayer3, gameFlow.getPlayer(3).getItemNames());

    }

    @Test
    public void testTeleportUltimate() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"choice\"}");
        JSONObject choices = new JSONObject("{\"field\": \"40\"}");
        gameFlow.setChoices(choices);
        gameFlow.updatePositions(paras);

        assertEquals(40L, gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testTeleportRandomPlayer() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"randomPlayer\"}");
        gameFlow.getPlayer(1).setPosition(10L);

        gameFlow.updatePositions(paras);

        assertEquals(30L, gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testTeleportUltimateFixedValue() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject paras = new JSONObject("{\"player\": \"current\",\"field\": \"4\"}");

        gameFlow.updatePositions(paras);

        assertEquals(4L, gameFlow.getPlayer(1).getPosition());
    }

    @Test
    public void testExchangeTreasureChestChoiceItem() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject choices = new JSONObject("{\"playerId\": \"2\",\"items\":  [\"OnlyFansAbo\"]}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"choice\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedItemsPlayer1 = new ArrayList<>();
        expectedItemsPlayer1.add("OnlyFansAbo");
        expectedItemsPlayer1.add("OnlyFansAbo");

        assertEquals(expectedItemsPlayer1, gameFlow.getPlayer(1).getItemNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getItemNames());

    }

    @Test
    public void testExchangeTreasureChestChoiceCards() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\",\"cards\":  [\"B35\"]}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"choice\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1, gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getCardNames());

    }


    @Test
    public void testMagicMushroom() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameFlow spyGame = Mockito.spy(gameFlow);
        JSONObject paras = new JSONObject("{\"dice\": \"2\",\"bonusCount\":2,\"money\": 10}");

        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(5);
        dice.add(4);
        doReturn(dice).when(spyGame).throwDice(2);
        doReturn(Collections.emptyMap()).when(spyGame).move(9, 30L);

        spyGame.givePlayerDice(paras);
        assertEquals(100, spyGame.getPlayer(1).getCash());
        verify(spyGame).move(9, 30L);
    }

    @Test
    public void testSuperMagicMushroomWithBonus() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameFlow spyGame = Mockito.spy(gameFlow);
        JSONObject paras = new JSONObject("{\"dice\": \"3\",\"bonusCount\":3,\"money\": 30}");

        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(4);
        dice.add(4);
        dice.add(4);
        doReturn(dice).when(spyGame).throwDice(3);
        doReturn(Collections.emptyMap()).when(spyGame).move(12, 30L);

        spyGame.givePlayerDice(paras);
        assertEquals(130, spyGame.getPlayer(1).getCash());
        verify(spyGame).move(12, 30L);
    }

    @Test
    public void testUltraMagicMushroomWithBonus() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameFlow spyGame = Mockito.spy(gameFlow);
        JSONObject paras = new JSONObject("{\"dice\": \"4\",\"bonusCount\":4,\"money\": 69}");

        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(4);
        dice.add(4);
        dice.add(4);
        dice.add(4);
        doReturn(dice).when(spyGame).throwDice(4);
        doReturn(Collections.emptyMap()).when(spyGame).move(16, 30L);

        spyGame.givePlayerDice(paras);
        assertEquals(169, spyGame.getPlayer(1).getCash());
        verify(spyGame).move(16, 30L);
    }

    @Test
    public void testExchangeTreasureChestallCards() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\"}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"all\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1, gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getCardNames());
    }

    @Test
    public void testExchangeTreasureChestRandomCard() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.getPlayer(2).addCardNames("B35");
        JSONObject choices = new JSONObject("{\"playerId\": \"2\"}");
        gameFlow.setChoices(choices);
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"card\",\"selection\": \"random\",\"amount\": 1}}");
        gameFlow.exchange(jsonObject);
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        expectedCardsPlayer1.add("B35");

        assertEquals(expectedCardsPlayer1, gameFlow.getPlayer(1).getCardNames());
        assertEquals(new ArrayList<String>(), gameFlow.getPlayer(2).getCardNames());
    }

    @Test
    public void testPayPositivePickPocket() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"relative\",\"amount\": {\"others\":\"givenAmount\",\"current\":\"50\"}}");
        gameFlow.updateMoney(jsonObject);

        assertEquals(150, gameFlow.getPlayer(1).getCash());
        assertEquals(100, gameFlow.getPlayer(2).getCash());
        assertEquals(100, gameFlow.getPlayer(3).getCash());
        assertEquals(100, gameFlow.getPlayer(4).getCash());
    }

    @Test
    public void testPayPositiveAbsolutePickPocket() {
        GameFlow gameFlow = basicGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"type\": \"absolute\",\"amount\": {\"others\":\"50\",\"current\":\"givenAmount\"}}");
        gameFlow.updateMoney(jsonObject);

        assertEquals(100, gameFlow.getPlayer(1).getCash());
        assertEquals(150, gameFlow.getPlayer(2).getCash());
        assertEquals(150, gameFlow.getPlayer(3).getCash());
        assertEquals(150, gameFlow.getPlayer(4).getCash());
    }


    @Test
    public void testgiveCard() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject jsonObject = new JSONObject("{\"player\": \"current\",\"card1\": \"random\"}");
        HashMap<Integer, ArrayList<String>> itemChoices = new HashMap<>();
        gameFlow.givePlayerCardRand(jsonObject);

        int expectedItemsPlayer10 = 1;
        ArrayList<String> itemNames = new ArrayList();
        ;

        ArrayList<String> expectedItemsPlayer3 = new ArrayList<>();
        ArrayList<String> expectedCardsPlayer1 = new ArrayList<>();
        assertEquals(expectedItemsPlayer10, gameFlow.getPlayer(1).getCardNames().size());
        //assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
    }



    @Test
    public void ExchangePlayerPositions() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject args = new JSONObject("{\"player\": \"current\",\"field\": \"randomPlayer\"}");
        Player[] players = gameFlow.getPlayers();
        String playerSpecialId = args.getString("player");
        ArrayList<Integer> playersToUpdate = new ArrayList<>((int) (long) gameFlow.getTurnPlayerId());
        long first_position = players[(int) (long) gameFlow.getTurnPlayerId() - 1].getPosition();
        players[(int) (long) gameFlow.getTurnPlayerId()].setPosition(53L);
        players[(int) (long) gameFlow.getTurnPlayerId() + 1].setPosition(53L);
        players[(int) (long) gameFlow.getTurnPlayerId() + 2].setPosition(53L);
        gameFlow.exchangePositions(args);

        assertEquals(53L, players[(int) (long) gameFlow.getTurnPlayerId() - 1].getPosition());
        //assertEquals(new ArrayList<String>(),gameFlow.getPlayer(2).getItemNames());
    }


    @Test
    public void ReduceMoneyPLayers() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        JSONObject args = new JSONObject("{}");
        Player[] players = gameFlow.getPlayers();
        ;
        ArrayList<Integer> playersToUpdate = new ArrayList<>((int) (long) gameFlow.getTurnPlayerId());
        gameFlow.reduceMoneyALL(args);
        assertEquals(95, players[(int) (long) gameFlow.getTurnPlayerId() - 1].getCash());

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
        players[2].addItemNames("WhatsThis");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchangeAll();
        assertEquals(2, players[2].getCardNames().size());
        assertEquals(3, players[0].getItemNames().size());
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("WhatsThis");
        ArrayList<String> expectedCards = new ArrayList<>();
        expectedCards.add("S2");
        expectedCards.add("S5");
        assertEquals(expectedItemsPlayer10, players[0].getItemNames());
        assertEquals(expectedCards, players[2].getCardNames());
    }

    @Test
    public void exchangeAll() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        JSONObject jsonObject = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"card\",\"selection\": \"random\", \"amount\": 100}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 100}}");
        players[0].setTeammateId(2L);
        players[1].setTeammateId(1L);
        players[2].setTeammateId(4L);
        players[3].setTeammateId(3L);
        players[0].setWinCondition("JackSparrow");
        players[1].setWinCondition("JackSparrow");
        players[2].setWinCondition("JackSparrow");
        players[3].setWinCondition("JackSparrow");
        players[0].addCardNames("S5");
        players[0].addCardNames("S2");
        players[0].addCardNames("S0");
        players[0].addCardNames("S1");
        players[2].addItemNames("WhatsThis");
        players[2].addItemNames("WhatsThis");
        players[2].addItemNames("WhatsThis");

        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchange(jsonObject);
        //assertEquals(2, players[2].getCardNames().size());
        //assertEquals(3, players[0].getItemNames().size());
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("WhatsThis");
        expectedItemsPlayer10.add("WhatsThis");
        expectedItemsPlayer10.add("WhatsThis");

        ArrayList<String> expectedCards = new ArrayList<>();
        expectedCards.add("S2");
        expectedCards.add("S5");
        expectedCards.add("S1");
        expectedCards.add("S0");
        Collections.sort(expectedItemsPlayer10);
        Collections.sort(players[0].getItemNames());
        Collections.sort(expectedCards);
        Collections.sort(players[2].getCardNames());
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
        players[2].addItemNames("MeowYou");
        players[2].addItemNames("ImOut");
        players[2].addItemNames("UltraMagicMushroom");
        JSONObject param = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 4}}");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchange(param);
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("MeowYou");
        expectedItemsPlayer10.add("ImOut");
        expectedItemsPlayer10.add("UltraMagicMushroom");
        ArrayList<String> expectedCards = new ArrayList<>();
        assertEquals(expectedItemsPlayer10.size(), players[0].getItemNames().size());
        assertEquals(0, players[2].getItemNames().size());
    }

    @Test
    public void allyouritemsLESS4() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[2].addItemNames("ImOut");
        players[2].addItemNames("UltraMagicMushroom");
        JSONObject param = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 4}}");
        JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
        gameFlow.setChoices(choices1);
        gameFlow.exchange(param);
        ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("OnlyFansAbo");
        expectedItemsPlayer10.add("ImOut");
        expectedItemsPlayer10.add("UltraMagicMushroom");
        ArrayList<String> expectedCards = new ArrayList<>();
        assertEquals(expectedItemsPlayer10.size(), players[0].getItemNames().size());
        assertEquals(0, players[2].getItemNames().size());
    }

    /*
        @Test
        public void timersWork() {
            GameFlow gameFlow = extensiveGameFlowSetup();
            Player[] players = gameFlow.getPlayers();
            players[2].addItemNames("ImOut");
            players[2].addItemNames("UltraMagicMushroom");
            JSONObject param = new JSONObject("{\"give\": {\"player\": \"current\",\"type\": \"\",\"selection\": \"\", \"amount\": 0}, \"get\": {\"player\": \"choice\",\"type\": \"item\",\"selection\": \"random\",\"amount\": 4}}");
            JSONObject choices1 = new JSONObject("{\"playerId\": \"3\"}");
            gameFlow.setChoices(choices1);
            gameFlow.exchange(param);
            ArrayList<String> expectedItemsPlayer10 = new ArrayList<>();
            expectedItemsPlayer10.add("OnlyFansAbo");
            expectedItemsPlayer10.add("OnlyFansAbo");
            expectedItemsPlayer10.add("ImOut");
            expectedItemsPlayer10.add("UltraMagicMushroom");
            ArrayList<String> expectedCards = new ArrayList<>();
        }
    */
    @Test
    public void richestPlayer() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[0].setCash(1000);
        List<Long> playerList = new ArrayList<>();
        playerList.add(1L);
        assertEquals(playerList, gameFlow.findMostCash(players));
    }

    @Test
    public void richestPlayer2() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[3].setCash(1000);
        List<Long> playerList = new ArrayList<>();
        playerList.add(4L);
        assertEquals(playerList, gameFlow.findMostCash(players));
        assertEquals(4, players.length);
    }

    @Test
    public void nextPLayerinLine() {
        GameFlow gameflow = extensiveGameFlowSetup();
        gameflow.setTurnPlayerId(1L);
        Map<String, Object> nextPlayer = gameflow.nextPlayer();
        Map<String, Object> retour = new HashMap<>();
        retour.put("currentTurn", gameflow.getCurrentTurn());
        retour.put("activePlayer", gameflow.getTurnPlayerId().toString());
        assertEquals(retour, nextPlayer);

    }

    @Test
    public void nextPLayerinLine2() {
        GameFlow gameflow = extensiveGameFlowSetup();
        gameflow.setTurnPlayerId(4L);
        Map<String, Object> nextPlayer = gameflow.nextPlayer();
        Map<String, Object> retour = new HashMap<>();
        retour.put("currentTurn", gameflow.getCurrentTurn());
        retour.put("activePlayer", gameflow.getTurnPlayerId().toString());
        assertEquals(retour, nextPlayer);

    }

    @Test
    public void nextPLayerinLine3() {
        GameFlow gameflow = extensiveGameFlowSetup();
        gameflow.setTurnCounter(16);
        gameflow.setCurrentTurn(2);
        Map<String, Object> nextPlayer = gameflow.nextPlayer();
        Map<String, Object> retour = new HashMap<>();
        retour.put("currentTurn", gameflow.getCurrentTurn());
        retour.put("activePlayer", gameflow.getTurnPlayerId().toString());
        assertEquals(retour, nextPlayer);
        assertEquals(2, gameflow.getCurrentTurn());

    }

    /*

     */
    @Test
    public void IsgameId() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.setGameId(1L);
        assertEquals(1L, gameFlow.getGameId());
    }

    @Test
    public void MovesLeft() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.setMovesLeft(3);
        assertEquals(3, gameFlow.getMovesLeft());
    }

    @Test
    public void TestTurnCounter() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        gameFlow.setTurnCounter(3);
        assertEquals(3, gameFlow.getTurnCounter());
    }

    @Test
    public void TestWinMsg() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Map<String, Object> retour = new HashMap<>();
        Map<String, Object> winMsg = new HashMap<>();
        winMsg.put("You won the game!", "PLayer");
        gameFlow.setWinMsg(winMsg);
        retour.put("You won the game!", "PLayer");
        assertEquals(retour, gameFlow.getWinMsg());

    }

    /*

     */
    @Test
    public void TestCardPosition2() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        GameWebSocketController.addGameFlow(gameFlow.getGameId(), gameFlow);
        Player[] players = gameFlow.getPlayers();
        players[0].setPosition(27L);
        JSONObject card = Getem.getCards().get("G13");
        JSONObject choices = new JSONObject();
        choices.put("count", "1");
        gameFlow.setChoices(choices);
        gameFlow.updateCardPositions(card, gameFlow.getChoices().getInt("count"));
        assertEquals(28L, players[0].getPosition());

    }

    @Test
    public void TestCardPosition3() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        GameWebSocketController.addGameFlow(gameFlow.getGameId(), gameFlow);
        Player[] players = gameFlow.getPlayers();
        players[0].setPosition(27L);
        JSONObject card = Getem.getCards().get("B123");
        JSONObject choices = new JSONObject();
        gameFlow.updateCardPositions(card, -1);
        long position = players[0].getPosition();
        assertTrue(position == 28L || position == 29L || position == 30L || position == 53L);

    }

    @Test
    void testDinoChickyNuggyMakesUltUsableCanPay() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setUltActive(false);
        gameFlow.getPlayer(1).setUltimate("PickPocket");

        gameFlow.rechargeUlt(dinoChickyParas);

        assertEquals(80, gameFlow.getPlayer(1).getCash());
        assertTrue(gameFlow.getPlayer(1).isUltActive());
    }

    @Test
    void testDinoChickyNuggyMakesUltUsableCantPay() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setUltActive(false);
        gameFlow.getPlayer(1).setUltimate("PickPocket");
        gameFlow.getPlayer(1).setCash(7);

        gameFlow.rechargeUlt(dinoChickyParas);

        assertEquals(0, gameFlow.getPlayer(1).getCash());
        assertTrue(gameFlow.getPlayer(1).isUltActive());
    }

    @Test
    public void startGame() {
        Game game = new Game();
        game.setId(1L);
        game.startGame();
        assertEquals(1, game.getRoundNum());
    }

    @Test
    public void nextRound() {
        Game game = new Game();
        game.setId(1L);
        game.startGame();
        game.nextRound();
        assertEquals(2, game.getRoundNum());
    }

    @Test
    public void TESTsetRound() {
        Game game = new Game();
        game.setId(1L);
        game.setRoundNum(2);
        assertEquals(2, game.getRoundNum());
    }

    @Test
    public void TESTgameBoard() {
        Game game = new Game();
        GameBoard gameBoard = new GameBoard();
        game.setId(1L);
        game.setGameBoard(gameBoard);
        assertEquals(gameBoard, game.getGameBoard());
    }

    @Test
    public void GameBoardTEST() {
        GameBoardSpace gameBoard = new GameBoardSpace();
        gameBoard.setuniqueId(1L);
        assertEquals(1L, gameBoard.getuniqueId());
    }

    @Test
    public void GameBoardCoordTEST() {
        GameBoardSpace gameBoardSpace = new GameBoardSpace();
        gameBoardSpace.setxCoord(3.5);
        gameBoardSpace.setyCoord(4.5);
        assertEquals(3.5, gameBoardSpace.getxCoord());
        assertEquals(4.5, gameBoardSpace.getyCoord());
    }

    @Test
    public void PlayerCoord() {
        GameBoardSpace gameBoardSpace = new GameBoardSpace();
        gameBoardSpace.setPlayerON(true);
        assertEquals(true, gameBoardSpace.getPlayerON());
    }

    @Test
    public void TestGameBoardSpace21() {
        GameBoardSpace gameBoardSpace = new GameBoardSpace();
        GameBoard gameBoard = new GameBoard();
        gameBoardSpace.setGameBoard(gameBoard);
        assertEquals(gameBoard, gameBoardSpace.getGameBoard());
    }

    @Test
    public void TestGameBoard() {
        GameBoard gameBoard = new GameBoard();
        Game game = new Game();
        gameBoard.setGame(game);
        assertEquals(game, gameBoard.getGame());
    }

    @Test
    public void setGameBoard() {
        GameBoard gameBoard = new GameBoard();
        Game game = new Game();
        gameBoard.setId(1L);
        assertEquals(1L, gameBoard.getId());
    }

    @Test
    public void TESTGameBoardStatus() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setStatus(GameBoardStatus.ACTIVE);
        assertEquals(GameBoardStatus.ACTIVE, gameBoard.getStatus());
    }

    @Test
    public void TestCHECKWinCONDI() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] player = gameFlow.getPlayers();
        player[0].setWinCondition("Unlucky");
        player[0].setLostCash(100);
        gameFlow.checkWinCondition(player[0]);
    }

    @Test
    public void testDicey() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        ArrayList<Integer> result = gameFlow.throwDice(1);
        assertTrue(result.get(0) >= 1 && result.get(0) <= 6);
    }

    @Test
    public void testGetPlayers2() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        ArrayList<Integer> result = gameFlow.throwDice(2);
        assertTrue(result.get(0) >= 1 && result.get(0) <= 6);
        assertTrue(result.get(1) >= 1 && result.get(1) <= 6);
    }

    @Test
    public void testBoardGoal() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        GameBoard gameBoard = new GameBoard();

        assertEquals(10L, gameFlow.findGoal(gameBoard.getSpaces()));

    }

    @Test
    public void testBoardGoalj() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        GameBoard gameBoard = new GameBoard();
        Map<String, Long> response = new HashMap<>();

        Map<String, Long> BoardGoal = gameFlow.setBoardGoal(gameBoard.getSpaces());
        response.put("goal", 10L);
        assertNotEquals(response, BoardGoal);

    }

    @Test
    public void testBoardGoalji() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        GameBoard gameBoard = new GameBoard();
        Map<String, Long> response = new HashMap<>();
        gameFlow.changeGoalPosition();
        assertEquals(10L, gameFlow.findGoal(gameBoard.getSpaces()));

    }

    @Test
    public void Junction(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        gameFlow.setHadJunction(true);
        LocalDateTime currentDateTime = LocalDateTime.now();
        gameFlow.setStartTime(currentDateTime);
        assertEquals(true, gameFlow.getHadJunction());
        assertEquals(currentDateTime, gameFlow.getStartTime());
    }

    @Test
    public void TESTtoItem(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        Map<String, Object> response = new HashMap<>();
        response.put("items", players[0].getItemNames());
        response.put("cards", players[0].getCardNames());
        assertNotEquals(response, gameFlow.toItem(players[0]));
    }

    @Test
    public void TESTtoMoney(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        response.put("newAmountofMoney", 110);
        response.put("changeAmountOfMoney", 10);
        String playerId = Long.toString(players[0].getPlayerId());
        result.put(playerId, response);
        players[0].setCash(100);
        Map<String, Object> lmao = gameFlow.toMoney(players[0], 10);
        assertNotEquals(response, lmao);
    }

    @Test
    public void TESTupdateTurns(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        JSONObject choices1 = new JSONObject("{\"newTurnNumber\": \"4\"}");
        gameFlow.updateTurns(choices1);
        assertEquals(4, gameFlow.getCurrentTurn());
    }

    @Test
    public void TESTshuffle() {
        JSONObject choices1 = new JSONObject("{\"type\": \"card\"}");
        GameFlow gameFlow = extensiveGameFlowSetup();
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameFlow.shuffle(choices1);
        });


    }

    @Test
    public void TESTshuffle2() {
        JSONObject choices1 = new JSONObject("{\"type\": \"ultimates\"}");
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[0].setWinCondition("Shiny");
        players[1].setWinCondition("Shiny");
        players[2].setWinCondition("Shiny");
        players[3].setWinCondition("Shiny");
        gameFlow.shuffle(choices1);
        assertEquals("Shiny", players[0].getWinCondition());


    }

    @Test
    public void TESTuseRandomUsable() {
        JSONObject choices1 = new JSONObject("{\"type\": \"card\", \"amount\": 2}");
        GameFlow gameFlow = extensiveGameFlowSetup();
        Assertions.assertThrows(RuntimeException.class, () -> {
            gameFlow.useRandomUsable(choices1);
        });

    }

    @Test
    public void TestToJunction() {
        JSONObject choices1 = new JSONObject("{\"type\": \"card\", \"amount\": 2}");
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        Long currSpace = players[0].getPosition();
        List<String> nextUnlock = new ArrayList<>();
        List<String> nextLock = new ArrayList<>();
        nextUnlock.add("10L");
        nextLock.add("20L");
        Map<String, Object> response = new HashMap<>();
        response.put("nextLockedSpaces", nextLock);
        response.put("nextUnlockedSpaces", nextUnlock);
        response.put("playerId", players[0].getPlayerId());
        response.put("currentSpace", currSpace);
        assertEquals(response.toString(), gameFlow.toJunction(players[0], currSpace, nextUnlock, nextLock).toString());

    }
/*
    @Test
    public void TESTuseRandomUsable(){
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        JSONObject choices1 = new JSONObject("{\"type\": \"item\",\"amount\": 4}");
        gameFlow.useRandomUsable(choices1);
        assertEquals(2, players[0].getItemNames().size());
    }
*/


}










/*
    @Test
    public void moveFunc() {
        GameFlow gameFlow = extensiveGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        for (Player player: players) {
            player.setPosition(21L);
            player.setWinCondition("Golden");
        }
        Map<String, Object> retour = gameFlow.move(2, gameFlow.getPlayer(1).getPosition());
        assertEquals(4, players.length);

    }
/*
    @Test
    public void GameOverMaxTurns() {
        GameFlow gameflow = extensiveGameFlowSetup();
        Player[] players = gameflow.getPlayers();
        Map<String, Object> mappi = new HashMap<>();
        Set<String> winners = new HashSet<>();
        Set<String> winnersUsername = new HashSet<>();
        List<String> reason = new ArrayList<>();
        players[0].setTeammateId(2L);
        players[0].setCash(1000);
        List<Long> playerList = new ArrayList<>();
        playerList.add(players[0]);
        gameflow.doGameOverMaxTurns(playerList);
        Map<String, Object> nextPlayer = gameflow.nextPlayer();
        Map<String, Object> retour = new HashMap<>();
        retour.put("winners", gameflow.getCurrentTurn());
        retour.put("reason", gameflow.getTurnPlayerId().toString());
        assertEquals(retour, nextPlayer);
        assertEquals(2, gameflow.getCurrentTurn());

    }

*/



