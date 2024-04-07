package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository UserRepository;


    @Autowired
    public UserService(@Qualifier("UserRepository") UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }
    public User createUser(User newUser){
        newUser.setCreationDate(LocalDate.now());
        newUser.setToken(UUID.randomUUID().toString());
        AchievementStatus ach = new AchievementStatus();
        this.UserRepository.save(newUser);
        UserRepository.flush();
        return newUser;
    }

}
