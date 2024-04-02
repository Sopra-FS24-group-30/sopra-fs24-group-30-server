package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;


    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void saveInitialAchievements(Player player){
        AchievementStatus ach = new AchievementStatus();
        ach.setPlayerId(player.getId());
        ach.setFirst(false);
        ach.setFirstProgress(0);
        this.achievementRepository.save(ach);
        achievementRepository.flush();
    }

}
