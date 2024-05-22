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

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.*;

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
            p.setWinCondition("JackSparrow");
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


    @Test
    public void testCorrectUpdateWinLoseStreak(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        User user2 = simplestUser();
        user2.setId(2L);
        achievementService.saveInitialAchievements(user2);
        AchievementStatus winstreak = new AchievementStatus(1L);
        winstreak.setWinStreak(1);
        winstreak.setTotalGamesWon(1);
        AchievementStatus loseStreak = new AchievementStatus(2L);
        loseStreak.setLoseStreak(1);
        achievementService.saveAChievements(winstreak);
        achievementService.saveAChievements(loseStreak);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).getAchievementProgress().setWinner(true);

        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        achievementService.updateAchievements(gameFlow.getPlayer(2).getAchievementProgress());

        AchievementStatus savedWinStreak = achievementRepository.findByUserId(1L);
        AchievementStatus savedLoseStreak = achievementRepository.findByUserId(2L);
        assertEquals(2,savedWinStreak.getWinStreak());
        assertEquals(2,savedWinStreak.getTotalGamesWon());
        assertEquals(2,savedLoseStreak.getLoseStreak());
        assertEquals(0,savedLoseStreak.getTotalGamesWon());
    }

    @Test
    void integrationTestInitialize(){
        for(int i=1;i<=4;i++){
            User user = new User();
            user.setId((long)i);
            achievementService.saveInitialAchievements(user);
        }

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setCash(300);
        gameFlow.getPlayer(2).setCash(0);
        gameFlow.getPlayer(3).getAchievementProgress().setUltimateUsed(false);

        HashSet<String> winners = new HashSet<>();
        winners.add("2");
        winners.add("3");
        gameFlow.initializeUpdates(winners);

        AchievementStatus savedBaron = achievementRepository.findByUserId(1L);
        AchievementStatus savedNoMoney = achievementRepository.findByUserId(2L);
        AchievementStatus savedNoUltimate = achievementRepository.findByUserId(3L);
        AchievementStatus savedNoBaron = achievementRepository.findByUserId(4L);


        assertTrue(savedBaron.isBaron3());
        assertTrue(savedBaron.isBaron2());
        assertFalse(savedNoBaron.isBaron1());
        assertTrue(savedNoMoney.isNoMoney());
        assertTrue(savedNoUltimate.isNoUltimate());
        assertEquals(1,savedNoMoney.getTotalGamesWon());

    }

    @Test
    public void testCorrectUpdateAchievementsEndurance(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);

        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).getAchievementProgress().getGameTimer().setElapsedTime(180009090909090L);
        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        System.out.println("THIS IS THE TIMER:  " + gameFlow.getPlayer(1).getAchievementProgress().getGameTimer().getElapsedTime());
        assertTrue(saved.isEndurance1());
        assertTrue(saved.isEndurance2());
        assertTrue(saved.isEndurance3());
    }

    @Test
    void testEnduranceTwoCorrectUpdate(){
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        AchievementStatus gamerAchievementStatus = new AchievementStatus(1L);
        gamerAchievementStatus.setTotalGamesWon(0);
        achievementService.saveAChievements(gamerAchievementStatus);

        GameFlow gameFlow = basicGameFlowSetup();
        long a = 7200;
        gameFlow.getPlayer(1).getAchievementProgress().getGameTimer().setElapsedTime(a);
        achievementService.updateAchievements(gameFlow.getPlayer(1).getAchievementProgress());
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertTrue(saved.isEndurance1());
        assertTrue(saved.isEndurance2());
        assertEquals(0,saved.getTotalGamesWon());
    }

    @Test
    public void testJackSparrowWinCondition() {
        GameFlow gameFlow = basicGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        Set<String> winners = new HashSet<>();
        winners.add("1");
        gameFlow.initializeUpdates(winners);
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertEquals(true, players[0].getAchievementProgress().isWinner());
    }

    @Test
    public void checkGameOverMaxTurns() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setTeammateId(3L);
        gameFlow.getPlayer(2).setTeammateId(4L);
        gameFlow.getPlayer(3).setTeammateId(1L);
        gameFlow.getPlayer(4).setTeammateId(2L);
        Player[] players = gameFlow.getPlayers();
        Set<String> winners = new HashSet<>();
        Map<String, Object> mappi = new HashMap<>();
        List<Long> rich = new ArrayList<>();
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        User user1 = simplestUser();
        achievementService.saveInitialAchievements(user1);
        User user2 = simplestUser();
        achievementService.saveInitialAchievements(user2);
        User user3 = simplestUser();
        achievementService.saveInitialAchievements(user3);
        players[0].setUser(user);
        players[1].setUser(user1);
        players[2].setUser(user2);
        players[3].setUser(user3);
        rich.add(1L);
        rich.add(2L);
        Map<String, Object> map = new HashMap<>();
        map.put("winners", null);
        map.put("reason", "null has JackSparrow");
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertEquals(map.toString(), gameFlow.doGameOverMaxTurns(rich).toString());
    }

    @Test
    public void checkGameOverMaxTurns2() {
        GameFlow gameFlow = basicGameFlowSetup();
        gameFlow.getPlayer(1).setTeammateId(3L);
        gameFlow.getPlayer(2).setTeammateId(4L);
        gameFlow.getPlayer(3).setTeammateId(1L);
        gameFlow.getPlayer(4).setTeammateId(2L);
        Player[] players = gameFlow.getPlayers();
        for (Player p : players) {
            p.setWinCondition("Golden");
        }
        Set<String> winners = new HashSet<>();
        Map<String, Object> mappi = new HashMap<>();
        List<Long> rich = new ArrayList<>();
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        User user1 = simplestUser();
        achievementService.saveInitialAchievements(user1);
        User user2 = simplestUser();
        achievementService.saveInitialAchievements(user2);
        User user3 = simplestUser();
        achievementService.saveInitialAchievements(user3);
        players[0].setUser(user);
        players[1].setUser(user1);
        players[2].setUser(user2);
        players[3].setUser(user3);
        rich.add(1L);
        Map<String, Object> map = new HashMap<>();
        map.put("winners", null);
        map.put("reason", "null has maxCash");
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertEquals(map.toString(), gameFlow.doGameOverMaxTurns(rich).toString());
    }

    @Test
    public void checkGameOverMaxTurns3() {
        GameFlow gameFlow = basicGameFlowSetup();
        Player[] players = gameFlow.getPlayers();
        players[0].setTeammateId(3L);
        players[1].setTeammateId(4L);
        players[2].setTeammateId(1L);
        players[3].setTeammateId(2L);
        for (Player p : players) {
            p.setWinCondition("Golden");
        }
        Set<String> winners = new HashSet<>();
        Map<String, Object> mappi = new HashMap<>();
        List<Long> rich = new ArrayList<>();
        User user = simplestUser();
        achievementService.saveInitialAchievements(user);
        User user1 = simplestUser();
        achievementService.saveInitialAchievements(user1);
        User user2 = simplestUser();
        achievementService.saveInitialAchievements(user2);
        User user3 = simplestUser();
        achievementService.saveInitialAchievements(user3);
        players[0].setUser(user);
        players[1].setUser(user1);
        players[2].setUser(user2);
        players[3].setUser(user3);
        rich.add(1L);
        rich.add(2L);
        Map<String, Object> map = new HashMap<>();
        map.put("winners", null);
        map.put("reason", "null has maxCash");
        AchievementStatus saved = achievementRepository.findByUserId(1L);
        assertEquals(map.toString(), gameFlow.doGameOverMaxTurns(rich).toString());
    }



}
