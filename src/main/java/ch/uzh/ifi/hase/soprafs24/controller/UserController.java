package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserInformation;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/profile/{userid}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO profile(@PathVariable Long userid){
      User user = userService.profile(userid);
      if (user == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User could not be found");
      }
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PostMapping("/user/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO login(@RequestBody UserPutDTO userPutDTO){
    User loginUser = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    User user = userService.login(loginUser);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PutMapping("/profile/{userid}/edit")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public UserGetDTO editProfile(@RequestBody UserPutDTO userPutDTO, @PathVariable Long userid){
    User user = userService.profile(userid);
    User updates = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    if (updates == null){return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);}
    User updatedUser = userService.edit(user, updates);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
  }


  @PutMapping("/game")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logout(@RequestBody UserPutDTO userPutDTO){
    userService.logout(DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO));
  }

  @PutMapping("/status/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void status(@PathVariable String username){
    userService.status(username);
  }

}
