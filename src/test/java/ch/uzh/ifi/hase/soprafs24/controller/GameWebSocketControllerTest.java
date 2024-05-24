package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import  ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import  ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import org.mockito.*;
@SpringBootTest
public class GameWebSocketControllerTest {

    @Mock
    private GameManagementService gameManagementService;

    //----------------------------------------Adapt gameFlow Status------------------------------------------------------------//

    private GameFlow basicGameFlowSetup() {
        GameFlow gameFlow = new GameFlow();
        for (int i = 1; i <= 4; i++) {
            Player p = new Player();
            p.setUserId((long) i);
            p.setAchievementProgress(new AchievementProgress((long) i));
            p.setPlayerId((long) i);
            p.setUltimate("PickPocket");
            p.setUltActive(true);
            p.setCash(100);
            p.setPosition(30L);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    private GameFlow basicGameFlowSetupCards() {
        GameFlow gameFlow = new GameFlow();
        for (int i = 1; i <= 4; i++) {
            Player p = new Player();
            p.setUserId((long) i);
            p.setAchievementProgress(new AchievementProgress((long) i));
            p.setPlayerId((long) i);
            p.setUltimate("PickPocket");
            p.setUltActive(true);
            p.setCash(100);
            p.setPosition(30L);
            p.setWinCondition("Golden");
            gameFlow.addPlayer(p);
        }
        gameFlow.setGameBoard();
        gameFlow.setTurnPlayerId(1L);
        gameFlow.setGameId(1L);

        return gameFlow;
    }

    private String onlyFansSub = "{\"used\":\"OnlyFansSub\",\"choice\":{}}";
    private String pickPocket = "{\"used\":\"PickPocket\",\"choice\":{}}";
    private String freshStart = "{\"used\":\"FreshStart\",\"choice\":{}}";
    private static String silverOne = "{\"used\":\"S1\",\"choice\":{}}";


    @Test
    void testUsingItemUpdatesItemUsed() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleItems(onlyFansSub, 1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltUsed() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltDisabled() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleUltimate(freshStart, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
    }

    @Test
    void testItemDoesNotTriggerAfterUltimate() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setUltimate("FreshStart");
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleUltimate(freshStart, 1L);
        GameWebSocketController.handleItems(onlyFansSub, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
        //check that onlyFans was not used on players
        assertEquals(100, gameFlow.getPlayer(1).getCash());
        assertEquals(100, gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testUltimateCantBeTriggeredTwice() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleUltimate(pickPocket, 1L);
        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
        //check that pickpocket was used once
        assertEquals(250, gameFlow.getPlayer(1).getCash());
        assertEquals(50, gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testCantUseUltimateAfterItem() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleItems(onlyFansSub, 1L);
        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testCardUpdatesStatusCardUsed() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L, gameFlow);

        try (MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)) {
            mockedStatic.when(() -> GameWebSocketController.handleCardPosition(any(), anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.addGameFlow(anyLong(), any()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.getGameFlow(anyLong()))
                    .thenCallRealMethod();

            mockedStatic.when(() -> GameWebSocketController.newPlayer(any(), anyLong()))
                    .thenAnswer(invocation -> null);
            GameWebSocketController.handleCardPosition(silverOne, 1L);
        }

        assertTrue(gameFlow.isCardDiceUsed());
    }

    @Test
    void testDiceUpdatesStatusCardsUsed() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L, gameFlow);
        gameFlow.setMovesLeft(1);

        try (MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)) {
            mockedStatic.when(() -> GameWebSocketController.diceWalk(anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.addGameFlow(anyLong(), any()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.getGameFlow(anyLong()))
                    .thenCallRealMethod();


            mockedStatic.when(() -> GameWebSocketController.rollOneDice(anyLong()))
                    .thenAnswer(invocation -> null);
            mockedStatic.when(() -> GameWebSocketController.newPlayer(any(), anyLong()))
                    .thenAnswer(invocation -> null);
            GameWebSocketController.diceWalk(1L);
        }

        assertTrue(gameFlow.isCardDiceUsed());
    }

    @Test
    void testDiceUpdatesStatusCardsUsedCantUseUltOrItemAfter() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L, gameFlow);
        gameFlow.setMovesLeft(1);

        try (MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)) {
            mockedStatic.when(() -> GameWebSocketController.diceWalk(anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.addGameFlow(anyLong(), any()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.getGameFlow(anyLong()))
                    .thenCallRealMethod();


            mockedStatic.when(() -> GameWebSocketController.rollOneDice(anyLong()))
                    .thenAnswer(invocation -> null);
            mockedStatic.when(() -> GameWebSocketController.newPlayer(any(), anyLong()))
                    .thenAnswer(invocation -> null);
            GameWebSocketController.diceWalk(1L);
        }

        GameWebSocketController.handleItems(onlyFansSub, 1L);
        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertTrue(gameFlow.isCardDiceUsed());
        assertEquals(1, gameFlow.getPlayer(1).getItemNames().size());
        assertTrue(gameFlow.getPlayer(1).isUltActive());
    }

    @Test
    void testDiceUpdatesStatusCardsUsedCantUseDiceOrCardAfter() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L, gameFlow);
        gameFlow.setMovesLeft(1);

        try (MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)) {
            mockedStatic.when(() -> GameWebSocketController.diceWalk(anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.addGameFlow(anyLong(), any()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.getGameFlow(anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.move(anyLong()))
                    .thenCallRealMethod();

            mockedStatic.when(() -> GameWebSocketController.rollOneDice(anyLong()))
                    .thenAnswer(invocation -> null);
            mockedStatic.when(() -> GameWebSocketController.newPlayer(any(), anyLong()))
                    .thenAnswer(invocation -> null);
            //this results in new position 28
            GameWebSocketController.diceWalk(1L);
        }

        //if dice and cards are not triggered the position should stay the same
        GameWebSocketController.diceWalk(1L);
        GameWebSocketController.handleCardPosition(silverOne, 1L);

        assertTrue(gameFlow.isCardDiceUsed());
        assertEquals(28L, gameFlow.getPlayer(1).getPosition());
    }

    @Test
    void testCardMoveNewPlayerUpdatesStatus() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleCardPosition(silverOne, 1L);

        assertFalse(gameFlow.isCardDiceUsed());

    }

    @Test
    void testNextPlayerReleasesStatusItem() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());

        Map<String, Object> dummyMap = new HashMap<>();
        GameWebSocketController.newPlayer(dummyMap, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).isItemultused());
    }


    @Test
    void testRemoveUsedItemFromPlayer() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleItems(onlyFansSub, 1L);

        assertEquals(0, gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testRemoveUsedCardFromPlayer() {
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).addCardNames("S1");
        GameWebSocketController.addGameFlow(1L, gameFlow);

        GameWebSocketController.handleCardPosition(silverOne, 1L);

        assertEquals(0, gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void TestGETgameFLow() {
        GameFlow gameFlow = basicGameFlowSetup();
        HashMap<String, Object> map = new HashMap<>();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        GameWebSocketController.getGameFlow(1L);
        assertEquals(GameWebSocketController.getGameFlow(1L), gameFlow);
    }

    @Test
    void TESTcheckGoalGameOver() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(2).setPosition(27L);
        gameFlow.getPlayer(3).setPosition(27L);
        gameFlow.getPlayer(4).setPosition(27L);
        Player[] players = gameFlow.getPlayers();
        gameFlow.setGameBoard();
        GameBoard gameBoard = gameFlow.getGameBoard();
        List<Long> listi = new ArrayList<>();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        Assertions.assertThrows(NullPointerException.class, () -> {
            gameFlow.checkGoalGameOver("BlueGoal", players[0], listi, 1, 1, gameBoard.getSpaces());
        });
    }

    @Test
    void TESTcaseJunction() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(2).setPosition(27L);
        gameFlow.getPlayer(3).setPosition(27L);
        gameFlow.getPlayer(4).setPosition(27L);
        Player[] players = gameFlow.getPlayers();
        gameFlow.setGameBoard();
        Map<String, Object> map = new HashMap<>();
        List<Long> listi = new ArrayList<>();
        List<GameBoardSpace> gameBoard = gameFlow.getGameBoard().getSpaces();
        GameBoardSpace gameBoardSpaceWithId28 = null;
        for (GameBoardSpace space : gameBoard) {
            if (space.getSpaceId() == 28L) {
                gameBoardSpaceWithId28 = space;
                break;
            }


        }
        assertEquals(map, gameFlow.caseJunction(players[0], gameBoardSpaceWithId28, 1, 1, "BlueGoal", 27L, listi));
    }

    @Test
    void TESTcaseGate() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(2).setPosition(27L);
        gameFlow.getPlayer(3).setPosition(27L);
        gameFlow.getPlayer(4).setPosition(27L);
        Player[] players = gameFlow.getPlayers();
        gameFlow.setGameBoard();
        Map<String, Object> map = new HashMap<>();
        List<Long> listi = new ArrayList<>();
        List<GameBoardSpace> gameBoard = gameFlow.getGameBoard().getSpaces();
        players[0].addItemNames("TheBrotherAndCo");
        GameBoardSpace gameBoardSpaceWithId28 = null;
        for (GameBoardSpace space : gameBoard) {
            if (space.getSpaceId() == 27L) {
                gameBoardSpaceWithId28 = space;
                break;
            }


        }
        GameBoardSpace finalGameBoardSpaceWithId28 = gameBoardSpaceWithId28;
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            gameFlow.caseGate(players[0], finalGameBoardSpaceWithId28, 1, 1, "BlueGoal", 27L, listi);
        });
    }

    @Test
    void TESTgetCurrGame(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        HashMap<Long, Game> map = new HashMap<>();
        HashMap<Long, Game> realmap = new HashMap<>();
        Game game = new Game();
        game.setId(1L);
        map.put(1L, game);
        realmap.put(1L, game);
        GameWebSocketController.addGame(1L, game);
        GameWebSocketController.setCurrGame(map);
        assertEquals(game, GameWebSocketController.getCurrGame(1L));
        GameWebSocketController.removeGame(1L);
        assertNotEquals(game, GameWebSocketController.getCurrGame(1L));

    }

    @Test
    void TESTgetCurrGame2(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        HashMap<Long, Game> map = new HashMap<>();
        HashMap<Long, Game> realmap = new HashMap<>();
        Game game = new Game();
        game.setId(1L);
        map.put(1L, game);
        realmap.put(1L, game);
        GameWebSocketController.addGame(1L, game);
        GameWebSocketController.setCurrGame(map);
        assertEquals(game, GameWebSocketController.getCurrGame(1L));
        GameWebSocketController.removeGame(1L);
        assertNotEquals(game, GameWebSocketController.getCurrGame(1L));

    }

    @Test
    void TESTgetGameFlow(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        HashMap<Long, GameFlow> map1 = new HashMap<>();
        Game game = new Game();
        map1.put(1L, gameFlow);
        GameWebSocketController.setGameFlow(map1);
        GameWebSocketController.removeGameFlow(1L);
        assertNotEquals(gameFlow, GameWebSocketController.getGameFlow(1L));

    }

    @Test
    public void testEndGame() {
        Long gameId = 1L;
        Game game = new Game();
        game.setId(gameId);
        game.setStatus(GameStatus.PLAYING);
        GameWebSocketController.addGame(gameId, game);
        GameWebSocketController.endGame(gameId);
        assertEquals(GameStatus.NOT_PLAYING.toString(), game.getStatus().toString());
    }


/*
    @Test
    public void TESTuseRandomUsable2() {
        JSONObject choices1 = new JSONObject("{\"type\": \"item\", \"amount\": 1}");
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);
        gameFlow.useRandomUsable(choices1);


    }

 */
}