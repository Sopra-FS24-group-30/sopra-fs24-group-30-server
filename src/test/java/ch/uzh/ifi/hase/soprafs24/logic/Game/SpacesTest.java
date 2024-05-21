package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.logic.Game.Constant.GamblingChoice;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SpacesTest {

    private Spaces spaces = new Spaces();

    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
            //p.setAchievementProgress(new AchievementProgress((long) i), new GameWebSocketController.GameTimer());
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            p.addItemNames("OnlyFansAbo");
            p.addCardNames("B26");
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    @Test
    void testBlueSpaceAddsFourMoney(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces.blue(gameFlow);
        assertEquals(104,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testItemSpaceGivesItem(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces.item(gameFlow);
        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testCardSpaceGivesCard(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces.card(gameFlow);
        assertEquals(2,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testGamblingItemsWin(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.ITEM);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(1);

            spaces.gambling(gameFlow);
        }

        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testGamblingItemsLose(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.ITEM);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(2);

            spaces.gambling(gameFlow);
        }

        assertEquals(0,gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testGamblingCardsWin(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.CARD);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(1);

            spaces.gambling(gameFlow);
        }

        assertEquals(2,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testGamblingCardsLose(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.CARD);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(2);

            spaces.gambling(gameFlow);
        }

        assertEquals(0,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testGamblingCashLose(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.CASH);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(2);

            spaces.gambling(gameFlow);
        }

        assertEquals(0,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testGamblingCashWin(){
        GameFlow gameFlow = basicGameFlowSetup();

        try(MockedStatic<GamblingChoice> gambling = mockStatic(GamblingChoice.class)){
            gambling.when(GamblingChoice::getGamblingChoice).thenReturn(GamblingChoice.CASH);
            gambling.when(GamblingChoice::values).thenReturn(new GamblingChoice[] {GamblingChoice.ITEM,GamblingChoice.CARD,GamblingChoice.CASH});
            spaces = Mockito.spy(spaces);
            when(spaces.randomInt(Mockito.anyInt())).thenReturn(1);

            spaces.gambling(gameFlow);
        }

        assertEquals(200,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testCatNamiAddCash(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(Mockito.anyInt())).thenReturn(0);

        spaces.catnami(gameFlow);

        assertEquals(169,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testCatNamiExchangeWinCondWithTeammate(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setWinCondition("X");
        gameFlow.getPlayer(3).setWinCondition("Y");
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(Mockito.anyInt())).thenReturn(1);

        spaces.catnami(gameFlow);

        assertEquals("Y",gameFlow.getPlayer(1).getWinCondition());
        assertEquals("X",gameFlow.getPlayer(3).getWinCondition());
    }

    @Test
    void testCatNamiExchnageWinConditionWithEnemyOddTeam(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setWinCondition("X");
        gameFlow.getPlayer(2).setWinCondition("Y");
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(4)).thenReturn(2);
        when(spaces.randomInt(2)).thenReturn(0);

        spaces.catnami(gameFlow);


        assertEquals("Y",gameFlow.getPlayer(1).getWinCondition());
        assertEquals("X",gameFlow.getPlayer(2).getWinCondition());

    }

}
