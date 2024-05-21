package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetBean {
    private static AchievementService achievementService;
    private static AchievementRepository achievementRepository;


    @Autowired
    public void setAchievementService(AchievementService service) {
        GetBean.achievementService = service;
    }
    public static AchievementService getAchievementService(){
        return achievementService;
    }

    @Autowired
    public void setAchievementRepository(AchievementRepository repository) {
        GetBean.achievementRepository = repository;
    }
    public static AchievementRepository getAchievementRepository(){
        return achievementRepository;
    }


}
