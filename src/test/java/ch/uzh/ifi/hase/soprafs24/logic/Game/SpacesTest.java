package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Constant.GamblingChoice;
import org.hibernate.query.criteria.internal.expression.SimpleCaseExpression;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SpacesTest {

    private Spaces spaces = new Spaces();

    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
            p.setAchievementProgress(new AchievementProgress((long) i));
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

    @Test
    void testCatNamiTeammateLosesCash(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(4)).thenReturn(3);

        spaces.catnami(gameFlow);


        assertEquals(0,gameFlow.getPlayer(3).getCash());
    }

    @Test
    void testBlackLose69Cash(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(0);

        spaces.black(gameFlow);

        assertEquals(31,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testBlackGiveDivideMoneyToAllPLayers(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(2).setCash(500);
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(1);

        spaces.black(gameFlow);

        for(Player player : gameFlow.getPlayers()){
            assertEquals(200,player.getCash());
        }
    }

    @Test
    void testBlackUpdateAllPositions(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(2);

        ArrayList<Long> possiblePositions = new ArrayList<>();
        for(int i=1;i<=4;i++){
            possiblePositions.add((long) i);
            gameFlow.getPlayer(i).setPosition((long) i);
        }

        spaces.black(gameFlow);


        ArrayList<Long> actualPositions = new ArrayList<>();
        for(Player player : gameFlow.getPlayers()){
            assertTrue(possiblePositions.contains(player.getPosition()));
            actualPositions.add(player.getPosition());
        }
        assertEquals(4,actualPositions.size());
    }

    @Test
    void testRedLoseTenCash(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(0);

        spaces.red(gameFlow);

        assertEquals(90,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void testRedAllLoseTenCash(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(1);

        spaces.red(gameFlow);

        for(Player player : gameFlow.getPlayers()){
            assertEquals(90,player.getCash());
        }
    }

    @Test
    void testRedTeleportToStart(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(2);

        spaces.red(gameFlow);

        assertEquals(53L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    void testTeleportToSpace49(){
        GameFlow gameFlow = basicGameFlowSetup();

        spaces.teleportToSpace49(gameFlow);

        assertEquals(49L,gameFlow.getPlayer(1).getPosition());
    }

    @Test
    void testTeleportToSpace13(){
        GameFlow gameFlow = basicGameFlowSetup();

        spaces.teleportToSpace13(gameFlow);

        assertEquals(13L,gameFlow.getPlayer(1).getPosition());
    }
/*
    @Test
    void testSellAllItemsSomeItems(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addItemNames("MagicMushroom");
        gameFlow.getPlayer(1).addItemNames("SuperMagicMushroom");

        //Player sells MagicMushroom and OnlyFansAbo which are worth 5, 7 or 10 cash
        spaces.sellAllItems(gameFlow);

        assertEquals(122,gameFlow.getPlayer(1).getCash());
        assertEquals(0,gameFlow.getPlayer(1).getItemNames().size());
    }
*/
    @Test
    void testMustBuyItemPlayerCanBuyItem(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(15);

        spaces.mustBuyItemOrCard(gameFlow);

        assertEquals(0,gameFlow.getPlayer(1).getCash());
        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals(1,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testMustBuyItemPlayerBuysCard(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(13);

        spaces.mustBuyItemOrCard(gameFlow);

        assertEquals(0,gameFlow.getPlayer(1).getCash());
        assertEquals(1,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals(2,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testStealOthersMoneyAllCanPay(){
        GameFlow gameFlow = basicGameFlowSetup();

        spaces.stealOthersMoney(gameFlow);

        assertEquals(130,gameFlow.getPlayer(1).getCash());
        assertEquals(90,gameFlow.getPlayer(2).getCash());
        assertEquals(90,gameFlow.getPlayer(3).getCash());
        assertEquals(90,gameFlow.getPlayer(4).getCash());
    }

    @Test
    void testStealOthersMoneyNotAllCanPay(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(2).setCash(0);
        gameFlow.getPlayer(3).setCash(5);
        spaces.stealOthersMoney(gameFlow);

        assertEquals(115,gameFlow.getPlayer(1).getCash());
        assertEquals(0,gameFlow.getPlayer(2).getCash());
        assertEquals(0,gameFlow.getPlayer(3).getCash());
        assertEquals(90,gameFlow.getPlayer(4).getCash());
    }

    @Test
    void testGetMushroomPlayerGetsMushroom(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(2)).thenReturn(0);

        spaces.getMushroom(gameFlow);

        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals("MagicMushroom",gameFlow.getPlayer(1).getItemNames().get(1));
    }

    @Test
    void testGetMushroomallOtherPlayersGetMushroom(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(2)).thenReturn(1);

        spaces.getMushroom(gameFlow);

        for(int i=2;i<=4;i++){
            assertEquals(2,gameFlow.getPlayer(i).getItemNames().size());
            assertEquals("MagicMushroom",gameFlow.getPlayer(i).getItemNames().get(1));
        }
    }

    @Test
    void found20MoneyPlayerGets(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(0);

        spaces.found20Money(gameFlow);

        assertEquals(120,gameFlow.getPlayer(1).getCash());
    }

    @Test
    void found20MoneyTeammateGets(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(1);

        spaces.found20Money(gameFlow);

        assertEquals(120,gameFlow.getPlayer(3).getCash());
    }

    @Test
    void found20MoneyNothingHappens(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(2);

        spaces.found20Money(gameFlow);

        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(100,gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testTeleportToRandomOnlyPlayer(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setPosition(1L);
        gameFlow.getPlayer(2).setPosition(2L);
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(4)).thenReturn(1);
        when(spaces.randomInt(2)).thenReturn(1);

        spaces.teleportToRandom(gameFlow);

        assertEquals(2L,gameFlow.getPlayer(1).getPosition());
        assertEquals(2L,gameFlow.getPlayer(2).getPosition());
    }

    @Test
    void testTeleportToRandomExchangePlayers(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setPosition(1L);
        gameFlow.getPlayer(2).setPosition(2L);
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(4)).thenReturn(1);
        when(spaces.randomInt(2)).thenReturn(0);

        spaces.teleportToRandom(gameFlow);

        assertEquals(2L,gameFlow.getPlayer(1).getPosition());
        assertEquals(1L,gameFlow.getPlayer(2).getPosition());
    }

    @Test
    void testGetRandomGetBrotherAndCo(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(0);

        spaces.getRandomStuff(gameFlow);

        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals("TheBrotherAndCo",gameFlow.getPlayer(1).getItemNames().get(1));
        assertEquals(2,gameFlow.getPlayer(3).getItemNames().size());
        assertEquals("TheBrotherAndCo",gameFlow.getPlayer(3).getItemNames().get(1));
    }

    @Test
    void testGetRandomGetTwoCards(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(1);

        spaces.getRandomStuff(gameFlow);

        assertEquals(1,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals(3,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testGetRandomGetGoldItem(){
        GameFlow gameFlow = basicGameFlowSetup();
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(3)).thenReturn(2);

        spaces.getRandomStuff(gameFlow);

        assertEquals(2,gameFlow.getPlayer(1).getItemNames().size());
    }

    @Test
    void testGift10MoneyPlayCanPay(){
        GameFlow gameFlow = basicGameFlowSetup();

        spaces.gift10Money(gameFlow);

        assertEquals(90,gameFlow.getPlayer(1).getCash());
        assertEquals(110,gameFlow.getPlayer(3).getCash());
    }

    @Test
    void testGift10MoneyPlayCantPay(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(7);

        spaces.gift10Money(gameFlow);

        assertEquals(0,gameFlow.getPlayer(1).getCash());
        assertEquals(107,gameFlow.getPlayer(3).getCash());
    }

    @Test
    void testSellAllCardsOneOfEach(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).addCardNames("B26");
        gameFlow.getPlayer(1).addCardNames("S0");
        gameFlow.getPlayer(1).addCardNames("G13");

        spaces.sellAllCards(gameFlow);

        //sold all cards for  total of 5 + 5 + 7 + 10 = 27
        assertEquals(127,gameFlow.getPlayer(1).getCash());
        assertEquals(0,gameFlow.getPlayer(1).getCardNames().size());
    }

    @Test
    void testGetOtherCardsAllHaveCard(){
        GameFlow gameFlow = basicGameFlowSetup();

        spaces.getOthersCards(gameFlow);

        assertEquals(4,gameFlow.getPlayer(1).getCardNames().size());
        assertEquals(0,gameFlow.getPlayer(2).getCardNames().size());
        for(String card : gameFlow.getPlayer(1).getCardNames()){
            assertEquals("B26",card);
        }
    }

    @Test
    void testGetOtherCardsSomeHaveNoCard(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(2).removeCardNames("B26");

        spaces.getOthersCards(gameFlow);

        assertEquals(3,gameFlow.getPlayer(1).getCardNames().size());
        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(95,gameFlow.getPlayer(2).getCash());
    }

    @Test
    void testGetOtherCardsSomeHaveNoCardAndNotEnoughMoney(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(2).removeCardNames("B26");
        gameFlow.getPlayer(2).setCash(3);
        gameFlow.getPlayer(3).removeCardNames("B26");
        gameFlow.getPlayer(3).setCash(0);

        spaces.getOthersCards(gameFlow);

        assertEquals(2,gameFlow.getPlayer(1).getCardNames().size());
        assertEquals(100,gameFlow.getPlayer(1).getCash());
        assertEquals(0,gameFlow.getPlayer(2).getCash());
        assertEquals(0,gameFlow.getPlayer(3).getCash());
    }

    @Test
    void testSurpriseMFTriggersCatNami(){
        GameFlow gameFlow = basicGameFlowSetup();

        ArgumentCaptor<GameFlow> argumentCaptor = ArgumentCaptor.forClass(GameFlow.class);
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(4)).thenReturn(0);

        spaces.surpriseMF(gameFlow);

        verify(spaces).catnami(argumentCaptor.capture());
        assertEquals(gameFlow,argumentCaptor.getValue());
    }

    @Test
    void testSwapCardsOrItemsItemSwapOddTeam(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).removeItemNames("OnlyFansAbo");
        gameFlow.getPlayer(1).addItemNames("MagicMushroom");
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(2)).thenReturn(0);

        spaces.swapCardsOrItems(gameFlow);

        assertEquals(1,gameFlow.getPlayer(1).getItemNames().size());
        assertEquals("OnlyFansAbo",gameFlow.getPlayer(1).getItemNames().get(0));
        assertEquals(1,gameFlow.getPlayer(3).getItemNames().size());
        assertEquals("MagicMushroom",gameFlow.getPlayer(3).getItemNames().get(0));
    }

    @Test
    void testSwapCardsOrItemsCardSwapEvenTeam(){
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(2).removeCardNames("B26");
        gameFlow.getPlayer(2).addCardNames("S0");
        gameFlow.getPlayer(2).addCardNames("G13");
        gameFlow.setTurnPlayerId(2L);
        spaces = Mockito.spy(spaces);
        when(spaces.randomInt(2)).thenReturn(1);

        spaces.swapCardsOrItems(gameFlow);

        assertEquals(2,gameFlow.getPlayer(4).getCardNames().size());
        assertEquals("S0",gameFlow.getPlayer(4).getCardNames().get(0));
        assertEquals("G13",gameFlow.getPlayer(4).getCardNames().get(1));
        assertEquals(1,gameFlow.getPlayer(2).getCardNames().size());
        assertEquals("B26",gameFlow.getPlayer(2).getCardNames().get(0));
    }
}
