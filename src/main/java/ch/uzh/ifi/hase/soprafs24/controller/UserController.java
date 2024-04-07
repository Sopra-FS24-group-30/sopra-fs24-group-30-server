package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    private final UserService UserService;
    private final AchievementService achievementService;

    UserController(UserService UserService, AchievementService achievementService) {
        this.UserService = UserService;
        this.achievementService = achievementService;
    }

    @PostMapping("/User")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody()
    public UserPostDTO createUser(@RequestBody UserPostDTO UserPostDTO){
        User newUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(UserPostDTO);
        User generatedUser = this.UserService.createUser(newUser);
        this.achievementService.saveInitialAchievements(generatedUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(generatedUser);
    }

}
