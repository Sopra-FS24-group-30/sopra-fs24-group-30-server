package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private static GameWebSocketController gameWebSocketController;

    @BeforeAll
    public static void setupTimer(){
    }

    private User simplestUser(){
        User user = new User();
        user.setId(1L);
        return user;
    }

    private GameFlow basicGameFlowSetup(){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setUserId((long)i);
            p.setAchievementProgress(new AchievementProgress((long) i), new GameWebSocketController.GameTimer());
            p.getAchievementProgress().setGameTimer(new GameWebSocketController.GameTimer());
            p.setPlayerId((long) i);
            p.setCash(15);
            p.setPosition(30L);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);


        return gameFlow;
    }

    @Test
    public void testInitialSaveCorrect(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        AchievementStatus saved = achievementRepository.findByUserId(1L);

        assertEquals(1L,saved.getUserId());
        assertEquals(0,saved.getWinStreak());
        assertEquals(0,saved.getTotalGamesWon());
        assertEquals(1000,saved.getWinLeastAmountMoney());
        assertEquals(0,saved.getLoseStreak());
        assertFalse(saved.isGamer());
        assertFalse(saved.isBaron2());
        assertFalse(saved.isEndurance3());
        assertFalse(saved.isNoUltimate());

    }

    @Test
    public void testCorrectUpdateBaron1(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(40);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isBaron1());
        assertFalse(saved.isBaron2());
        assertFalse(saved.isBaron3());
    }

    @Test
    public void testCorrectUpdateAchievementsBaron2(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(150);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isBaron1());
        assertTrue(saved.isBaron2());
        assertFalse(saved.isBaron3());
    }

    @Test
    public void testCorrectUpdateAchievementsBaron3(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(200);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isBaron1());
        assertTrue(saved.isBaron2());
        assertTrue(saved.isBaron3());
    }

    @Test
    public void testCorrectUpdateNoMoneyAndWinner(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(0);
        gameFlow.getPlayer(1).getAchievementProgress().setWinner(true);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isNoMoney());
        assertEquals(1,saved.getTotalGamesWon());
    }

    @Test
    public void testCorrectUpdateNoUltimate(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).getAchievementProgress().setUltimateUsed(false);
        gameFlow.getPlayer(1).getAchievementProgress().setWinner(true);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isNoUltimate());
        assertEquals(1,saved.getTotalGamesWon());
    }

    @Test
    public void testCorrectUpdateGamer(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        AchievementStatus gamerAchievementStatus = new AchievementStatus(1L);
        gamerAchievementStatus.setWinStreak(2);
        gamerAchievementStatus.setTotalGamesWon(2);
        achievementService.saveAChievements(gamerAchievementStatus);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).getAchievementProgress().setWinner(true);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isGamer());
        assertEquals(3,saved.getWinStreak());
    }

    @Test
    public void testCorrectUpdateDoingYourBest(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        AchievementStatus gamerAchievementStatus = new AchievementStatus(1L);
        gamerAchievementStatus.setTotalGamesWon(0);
        gamerAchievementStatus.setLoseStreak(2);
        achievementService.saveAChievements(gamerAchievementStatus);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).getAchievementProgress().setWinner(false);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isDoingYourBest());
        assertEquals(3,saved.getLoseStreak());
        assertEquals(0,saved.getTotalGamesWon());
    }


}
