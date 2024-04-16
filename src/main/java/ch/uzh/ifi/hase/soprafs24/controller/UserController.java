package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
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

    public UserController(UserService UserService, AchievementService achievementService, GameService gameService) {
        this.UserService = UserService;
        this.achievementService = achievementService;
        this.gameService = gameService;
    }


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

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    //TODO add security here
    private void login(@RequestHeader ("username")String username, @RequestHeader("password") String password, HttpServletResponse response){
        String token = this.UserService.getUserToken(username,password);
        response.addHeader("token",token);

    }


    @GetMapping("/game/{gameID}/status")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean gameStatus(@PathVariable String gameID){
        return false;
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

    @GetMapping("/create/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private String createGame(){

        return this.UserService.getLobbyId();
    }

    @PutMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private boolean game(@RequestBody String lobbyId, @RequestBody ArrayList<Long> playerIds){

        boolean success = this.UserService.createGame(lobbyId,playerIds);
        return success;
    }

    @PutMapping("/profile/{userid}/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO editProfile(@RequestBody UserPutDTO userPutDTO, @PathVariable Long userid) {
        User user = userService.profile(userid);
        User updates = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        if (updates == null) {
            return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        }
        User updatedUser = userService.edit(user, updates);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);

    @PutMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private void game(){
        this.UserService.startGame();
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

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    private UserGetDTO getUser(@PathVariable Long id){

        User foundUser = this.UserService.findUserWithId(id);

        return DTOMapper.INSTANCE.convertUserToUserGetDTO(foundUser);
    }

    @PutMapping("/game/join/{gameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void joinGame(@PathVariable String gameID, @RequestBody UserPostDTO userPostDTO){
        User user = userService.findUser(DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO).getUsername());
    }

}
