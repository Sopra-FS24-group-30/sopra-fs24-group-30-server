package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Spaces;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class GameWebSocketControllerTest {

    //----------------------------------------Adapt gameFlow Status------------------------------------------------------------//

    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
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

    private GameFlow basicGameFlowSetupCards(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
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

    private String onlyFansAbo = "{\"used\":\"OnlyFansSub\",\"choice\":{}}";
    private String pickPocket = "{\"used\":\"PickPocket\",\"choice\":{}}";
    private String freshStart = "{\"used\":\"FreshStart\",\"choice\":{}}";
    private static String silverOne = "{\"used\":\"S1\",\"choice\":{}}";


    @Test
    void testUsingItemUpdatesItemUsed(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L,gameFlow);


        GameWebSocketController.handleItems(onlyFansAbo,1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltUsed(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L,gameFlow);
        GameWebSocketController.handleUltimate(pickPocket,1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltDisabled(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleUltimate(freshStart,1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
    }

    @Test
    void testItemDoesNotTriggerAfterUltimate(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setUltimate("FreshStart");
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleUltimate(freshStart,1L);
        GameWebSocketController.handleItems(onlyFansAbo,1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
        //check that onlyFans was not used on players
        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testUltimateCantBeTriggeredTwice(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleUltimate(pickPocket,1L);
        GameWebSocketController.handleUltimate(pickPocket,1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
        //check that pickpocket was used once
        assertEquals(250,gameFlow.getPlayer(1).getCash());
        assertEquals(50,gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testCantUseUltimateAfterItem(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleItems(onlyFansAbo, 1L);
        GameWebSocketController.handleUltimate(pickPocket,1L) ;

        assertTrue(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testCardUpdatesStatusCardUsed(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L,gameFlow);

        try(MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)){
            mockedStatic.when(() -> GameWebSocketController.handleCardPosition(any(), anyLong()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.addGameFlow(anyLong(), any()))
                    .thenCallRealMethod();
            mockedStatic.when(() -> GameWebSocketController.getGameFlow(anyLong()))
                    .thenCallRealMethod();

            mockedStatic.when(() -> GameWebSocketController.newPlayer(any(), anyLong()))
                    .thenAnswer(invocation -> null);
            GameWebSocketController.handleCardPosition(silverOne,1L);
        }

        assertTrue(gameFlow.isCardDiceUsed());
    }

    @Test
    void testDiceUpdatesStatusCardsUsed(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L,gameFlow);
        gameFlow.setMovesLeft(1);

        try(MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)){
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
    void testDiceUpdatesStatusCardsUsedCantUseUltOrItemAfter(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L,gameFlow);
        gameFlow.setMovesLeft(1);

        try(MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)){
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

        GameWebSocketController.handleItems(onlyFansAbo,1L);
        GameWebSocketController.handleUltimate(pickPocket,1L);

        assertTrue(gameFlow.isCardDiceUsed());
        assertEquals(1,gameFlow.getPlayer(1).getItemNames().size());
        assertTrue(gameFlow.getPlayer(1).isUltActive());
    }

    @Test
    void testDiceUpdatesStatusCardsUsedCantUseDiceOrCardAfter(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L,gameFlow);
        gameFlow.setMovesLeft(1);

        try(MockedStatic<GameWebSocketController> mockedStatic = mockStatic(GameWebSocketController.class)){
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
        GameWebSocketController.handleCardPosition(silverOne,1L);

        assertTrue(gameFlow.isCardDiceUsed());
        assertEquals(28L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    void testCardMoveNewPlayerUpdatesStatus(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).setPosition(27L);
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleCardPosition(silverOne,1L);

        assertFalse(gameFlow.isCardDiceUsed());

    }
    @Test
    void testNextPlayerReleasesStatusItem() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleUltimate(pickPocket, 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());

        Map<String,Object> dummyMap = new HashMap<>();
        GameWebSocketController.newPlayer(dummyMap,1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).isItemultused());
    }


    @Test
    void testRemoveUsedItemFromPlayer(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("OnlyFansSub");
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleItems(onlyFansAbo,1L);

        assertEquals(0,gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testRemoveUsedCardFromPlayer(){
        GameFlow gameFlow = basicGameFlowSetupCards();
        gameFlow.getPlayer(1).addCardNames("S1");
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleCardPosition(silverOne,1L);

        assertEquals(0,gameFlow.getPlayer(1).getCardNames().size());
    }
}