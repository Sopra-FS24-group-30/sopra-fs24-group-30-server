package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;


    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void saveInitialAchievements(User user){
        AchievementStatus ach = new AchievementStatus(user.getId());
        this.achievementRepository.save(ach);
        achievementRepository.flush();
    }

    public void updateAchievements(AchievementProgress achievementProgress){
        AchievementStatus achievementStatus = this.achievementRepository.findByUserId(achievementProgress.getUserId());
        //GameWebSocketController gameWebSocketController = new GameWebSocketController();
        //updates for baron achievements
        int maxCash = achievementProgress.getMaxAmountCash();
        if(maxCash >= 200){
            achievementStatus.setBaron3(true);
            achievementStatus.setBaron2(true);
            achievementStatus.setBaron1(true);
        }
        else if (maxCash >= 80 ) {
            achievementStatus.setBaron2(true);
            achievementStatus.setBaron1(true);
        }
        else if (maxCash >= 40) {
            achievementStatus.setBaron1(true);
        }
        if(achievementProgress.isWinner()){
            //update for NoMoney achievement
            int amountMoneyWinning = achievementProgress.getCashWhenWinning();
            if(amountMoneyWinning < achievementStatus.getWinLeastAmountMoney()){
                achievementStatus.setWinLeastAmountMoney(amountMoneyWinning);
                if(amountMoneyWinning == 0){
                    achievementStatus.setNoMoney(true);
                }
            }
            //update for No Ultimate
            if(!achievementProgress.isUltimateUsed()){
                achievementStatus.setNoUltimate(true);
            }
            //update for gamer and doingYourBest
            achievementStatus.setLoseStreak(0);
            achievementStatus.incTotalGamesWon();
            achievementStatus.incWinStreak();
            if(achievementStatus.getWinStreak() >= 3){
                achievementStatus.setGamer(true);
            }
        }else {
            //update for gamer and doingYourBest
            achievementStatus.setWinStreak(0);
            achievementStatus.incLoseStreak();
            if(achievementStatus.getLoseStreak() >= 3){
                achievementStatus.setDoingYourBest(true);
            }
        }

        if (achievementProgress.getGameTimer().getElapsedTime() >= 10800){
            achievementStatus.setEndurance1(true);
            achievementStatus.setEndurance2(true);
            achievementStatus.setEndurance3(true);
        }
        else if (achievementProgress.getGameTimer().getElapsedTime() >= 7200){
            achievementStatus.setEndurance2(true);
            achievementStatus.setEndurance1(true);
        }
        else if (achievementProgress.getGameTimer().getElapsedTime() >= 3600){
            achievementStatus.setEndurance1(true);
        }

        this.achievementRepository.save(achievementStatus);
        this.achievementRepository.flush();
    }

}
