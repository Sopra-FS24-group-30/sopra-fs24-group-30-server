package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


@RestController
public class UserController {
    private final UserService UserService;
    private final AchievementService achievementService;

    UserController(UserService UserService, AchievementService achievementService) {
        this.UserService = UserService;
        this.achievementService = achievementService;
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    //TODO add security here
    private void login(@RequestHeader ("username")String username, @RequestHeader("password") String password, HttpServletResponse response){
        String token = this.UserService.getUserToken(username,password);
        response.addHeader("token",token);

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody()
    private UserPostDTO createUser(@RequestBody UserPostDTO UserPostDTO){
        User newUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(UserPostDTO);
        User generatedUser = this.UserService.createUser(newUser);
        this.achievementService.saveInitialAchievements(generatedUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(generatedUser);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private UserGetDTO getUser(@PathVariable Long id){

        User foundUser = this.UserService.findUserWithId(id);

        return DTOMapper.INSTANCE.convertUserToUserGetDTO(foundUser);
    }

    @GetMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private String lobbyId(){

        return this.UserService.getLobbyId();
    }

    @PutMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private boolean game(@RequestBody String lobbyId, @RequestBody ArrayList<Long> playerIds){

        //boolean success = this.UserService.createGame(lobbyId,playerIds);
        //return success;
        return true;
    }
}
