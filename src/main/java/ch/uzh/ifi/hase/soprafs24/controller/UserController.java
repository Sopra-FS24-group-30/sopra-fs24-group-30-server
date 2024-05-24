package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@CrossOrigin(origins = "https://sopra-fs24-group-30-client.oa.r.appspot.com")
public class UserController {
    private final UserService userService;
    private final AchievementService achievementService;

    public UserController(UserService userService, AchievementService achievementService) {
        this.userService = userService;
        this.achievementService = achievementService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    //TODO add security here NOSONAR
    private UserPostDTO login(@RequestBody UserPostDTO userPostDTO){ //NOSONAR
        User loginUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = this.userService.login(loginUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(user);
    }

    @PutMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@PathVariable Long userId) {
        this.userService.logout(userId);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    private UserPostDTO createUser(@RequestBody UserPostDTO UserPostDTO){//NOSONAR
        User newUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(UserPostDTO);
        User generatedUser = this.userService.createUser(newUser);
        this.achievementService.saveInitialAchievements(generatedUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(generatedUser);
    }

    @PutMapping("/profile/{userid}/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editProfile(@RequestBody UserPutDTO userPutDTO, @PathVariable Long userid) {
        userService.edit(userPutDTO, userid);//NOSONAR
    }

    @GetMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    private UserGetDTO getUser(@PathVariable Long id){//NOSONAR

        User foundUser = this.userService.findUserWithId(id);
        return DTOMapper.INSTANCE.convertUserToUserGetDTO(foundUser);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertUserToUserGetDTO(user));
        }
        return userGetDTOs;
    }
}
