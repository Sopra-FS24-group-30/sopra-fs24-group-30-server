package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
            p.setAchievementProgress(new AchievementProgress((long) i), new GameWebSocketController.GameTimer());
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

    @Test
    void testUsingItemUpdatesItemUsed(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("OnlyFansAbo");
        GameWebSocketController.addGameFlow(1L,gameFlow);


        GameWebSocketController.handleItems("{\"itemUsed\":\"OnlyFansAbo\",\"choices\":{}}",1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltUsed(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L,gameFlow);
        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"PickPocket\",\"choices\":{}}",1L);

        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
    }

    @Test
    void testUsingUltimateUpdatesUltDisabled(){
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"PickPocket\",\"choices\":{}}",1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
    }

    @Test
    void testItemDoesNotTriggerAfterUltimate(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setUltimate("FreshStart");
        gameFlow.getPlayer(1).addItemNames("OnlyFansAbo");
        GameWebSocketController.addGameFlow(1L,gameFlow);

        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"FreshStart\",\"choices\":{}}",1L);
        GameWebSocketController.handleItems("{\"itemUsed\":\"OnlyFansAbo\",\"choices\":{}}",1L);

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

        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"PickPocket\",\"choices\":{}}",1L);
        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"PickPocket\",\"choices\":{}}",1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());
        //check that pickpocket was used once
        assertEquals(250,gameFlow.getPlayer(1).getCash());
        assertEquals(50,gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testNextPlayerReleasesStatusItem() {
        GameFlow gameFlow = basicGameFlowSetup();
        GameWebSocketController.addGameFlow(1L, gameFlow);


        GameWebSocketController.handleUltimate("{\"ultimateUsed\":\"PickPocket\",\"choices\":{}}", 1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).getPlayer(1).isUltActive());
        assertTrue(GameWebSocketController.getGameFlow(1L).isItemultused());

        Map<String,Object> dummyMap = new HashMap<>();
        GameWebSocketController.newPlayer(dummyMap,1L);

        assertFalse(GameWebSocketController.getGameFlow(1L).isItemultused());
    }
}