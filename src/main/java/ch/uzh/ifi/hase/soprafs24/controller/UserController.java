package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
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
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;


@RestController
//TODO: Celine whut the fuck? macht kaputt sobald in production nöd?
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final AchievementService achievementService;
    private final GameService gameService;
    private final GameManagementService gameManagementService;

    /*
    -----------------------------------------------------------------------------------------------
    User
    -----------------------------------------------------------------------------------------------
     */

    public UserController(UserService userService, AchievementService achievementService, GameService gameService, GameManagementService gameManagementService) {
        this.userService = userService;
        this.achievementService = achievementService;
        this.gameService = gameService;
        this.gameManagementService = gameManagementService;
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
    @ResponseBody
    public boolean gameStatus(@PathVariable String gameId){
        return false;
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

    @PostMapping("/games/setUp") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.CREATED)
    public Game createGame(String playerString, @RequestBody UserPostDTO userPostDTO) {
        Map<String, String> playerDict = new HashMap<>();
        playerDict.put("playerId", playerString);
        Long gameId = gameManagementService.createGame(playerDict.get("playerId"));
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));
        Game game = gameManagementService.findGame(gameId);
        int currentPlayerCount = game.getactive_Players().size();
        User user = userService.findUser(DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO).getUsername());
        Player player = gameService.createPlayerForGame(user, currentPlayerCount);
        game.addNEWPlayer(player);
        return game;
    }

    @GetMapping("/games/{id}") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.OK)
    public GameGetDTO getGame(@PathVariable Long id) {
        System.out.println("This is working");
        Game game = gameManagementService.findGame(id);
        System.out.println("This worked!!!");
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @PutMapping("/game/join/{gameID}")
    @ResponseStatus(HttpStatus.OK)
    public Player joinGame(@PathVariable String gameID, @RequestBody UserPostDTO userPostDTO){
        Game game = gameManagementService.findGame(Long.parseLong(gameID));
        int currentPlayerCount = game.getactive_Players().size();
        System.out.println(game);
        User user = userService.findUser(DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO).getUsername());//NOSONAR
        System.out.println(user);
        Player player = gameService.createPlayerForGame(user, currentPlayerCount);
        System.out.println(player);
        game.addNEWPlayer(player);
        System.out.println("These are the active players after:");
        System.out.println(game.getactive_Players());
        return player;
    }

    @PutMapping("/game/{gameID}/teammate/{playerID}/{teammateID}")
    @ResponseStatus(HttpStatus.OK)
    public Game chooseTeammate(@PathVariable String gameID,@PathVariable Long playerID,@PathVariable Long teammateID){
        if (!playerID.equals(1L)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player ID. Only host can choose teammate.");
        }
        Game game = gameManagementService.findGame(Long.parseLong(gameID));
        int currentPlayerCount = game.getactive_Players().size();
        System.out.println(playerID);
        Player player1 = findPlayerById(game, playerID);
        Player player2 = findPlayerById(game, teammateID);
        player1.setTeammateId(teammateID);
        player2.setTeammateId(playerID);



        List<Player> unpairedPlayers = game.getactive_Players().stream()
                .filter(p -> p.getTeammateId() == null || p.getTeammateId() == 0)
                .collect(Collectors.toList());

        if (unpairedPlayers.size() == 2) {
            unpairedPlayers.get(0).setTeammateId(unpairedPlayers.get(1).getPlayerId());
            unpairedPlayers.get(1).setTeammateId(unpairedPlayers.get(0).getPlayerId());
        }
        return game;
    }

    public Player findPlayerById(Game game, Long playerID) {
        for (Player player : game.getactive_Players()) {
            System.out.println("Checking player ID: " + player.getPlayerId() + " against " + playerID);

            if (player.getPlayerId().equals(playerID)) {
                return player;
            }
        }
        return null; // Return null or throw an exception if the player is not found
    }

}
