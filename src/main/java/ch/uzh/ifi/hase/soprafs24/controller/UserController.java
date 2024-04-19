package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService UserService;
    private final AchievementService achievementService;
    private final GameService gameService;

    /*
    -----------------------------------------------------------------------------------------------
    User
    -----------------------------------------------------------------------------------------------
     */

    public UserController(UserService UserService, AchievementService achievementService, GameService gameService) {
        this.UserService = UserService;
        this.achievementService = achievementService;
        this.gameService = gameService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    //TODO add security here
    private UserPostDTO login(@RequestBody UserPostDTO userPostDTO){
        User loginUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = this.UserService.login(loginUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(user);
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    private UserPostDTO createUser(@RequestBody UserPostDTO UserPostDTO){
        User newUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(UserPostDTO);
        User generatedUser = this.UserService.createUser(newUser);
        this.achievementService.saveInitialAchievements(generatedUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(generatedUser);
    }

    @PutMapping("/profile/{userid}/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void editProfile(@RequestBody UserPutDTO userPutDTO, @PathVariable Long userid) {
        User user = UserService.findUserWithId(userid);
        User updates = DTOMapper.INSTANCE.convertUserPutDTOtoUser(userPutDTO);
        if (updates == null) {
            return;
        }
        User updatedUser = UserService.edit(user, updates);
        System.out.println(updatedUser);
    }

    @GetMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private UserGetDTO getUser(@PathVariable Long id){

        User foundUser = this.UserService.findUserWithId(id);

        return DTOMapper.INSTANCE.convertUserToUserGetDTO(foundUser);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = UserService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertUserToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    /*
    -----------------------------------------------------------------------------------------------
    Game
    -----------------------------------------------------------------------------------------------
     */

    @GetMapping("/games") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.OK)
    public List<GameGetDTO> getAllGames() {
        // fetch all games in the internal representation
        List<Game> games = gameService.getGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();
        // convert each game to the API representation
        for (Game game : games) {
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        }
        return gameGetDTOs;
    }

    @GetMapping("/game/{gameID}/status")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean gameStatus(@PathVariable String gameId){
        return false;
    }

    @PostMapping("/create/game/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    private String createGame(@PathVariable String playerId){
        Long lobbyID = gameService.createGame(playerId);
        return String.valueOf(lobbyID);
    }

    @PutMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private void game(){
        this.UserService.startGame();
    }

    @PostMapping("/games/setUp") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.CREATED)
    public GameGetDTO setUpGameGame(@RequestBody GamePostDTO gamePostDTO) {
        // create game
        Game createdGame = gameService.setUpGame(gamePostDTO);
        // convert internal representation of game back to API
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(createdGame);
    }

    @GetMapping("/games/{id}") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.OK)
    public GameGetDTO getGame(@PathVariable Long id) {
        Game game = gameService.getGame(id);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @PutMapping("/game/join/{gameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void joinGame(@PathVariable String gameID, @RequestBody UserPostDTO userPostDTO){
        User user = UserService.findUser(DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO).getUsername());
    }

}
