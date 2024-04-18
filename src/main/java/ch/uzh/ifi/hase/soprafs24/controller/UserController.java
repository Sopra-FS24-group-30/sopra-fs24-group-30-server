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
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final AchievementService achievementService;
    private final GameService gameService;

    /*
    -----------------------------------------------------------------------------------------------
    User
    -----------------------------------------------------------------------------------------------
     */

    public UserController(UserService userService, AchievementService achievementService, GameService gameService) {
        this.userService = userService;
        this.achievementService = achievementService;
        this.gameService = gameService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    //TODO add security here NOSONAR
    private UserPostDTO login(@RequestBody UserPostDTO userPostDTO){ //NOSONAR
        User loginUser = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = this.userService.login(loginUser);
        return DTOMapper.INSTANCE.convertUserToUserPostDTO(user);
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
        User user = userService.findUserWithId(userid);
        User updates = DTOMapper.INSTANCE.convertUserPutDTOtoUser(userPutDTO);
        if (updates == null) {
            return;
        }
        User updatedUser = userService.edit(user, updates);//NOSONAR
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
    public boolean gameStatus(@PathVariable String gameID){
        return false;
    }

    @PostMapping("/create/game")
    @ResponseStatus(HttpStatus.OK)
    private String createGame(){//NOSONAR
        return this.userService.getLobbyId();
    }

    @PutMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    private boolean game(@RequestBody String lobbyId, @RequestBody ArrayList<Long> playerIds){//NOSONAR
        return this.userService.createGame(lobbyId,playerIds);
    }

    @PutMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    private void game(){//NOSONAR
        this.userService.startGame();
    }

    @PostMapping("/games") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.CREATED)
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {
        // create game
        Game createdGame = gameService.createGame(gamePostDTO);
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
    public void joinGame(@PathVariable String gameID, @RequestBody UserPostDTO userPostDTO){
        User user = userService.findUser(DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO).getUsername());//NOSONAR
    }

}
