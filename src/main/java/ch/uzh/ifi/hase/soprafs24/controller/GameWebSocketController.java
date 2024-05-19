package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

@Controller
public class GameWebSocketController {


    /*

    public static void main(String[] args){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            ArrayList<String> itemNames = new ArrayList<>();
            itemNames.add("OnlyFansAbo");
            p.addItemNames(itemNames);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);
        gameFlow.setGameId(123456L);
        gameFlows.put(123456L,gameFlow);
        handleItems("{\"itemUsed\": \"OnlyFansAbo\"}",123456L);
        System.out.println("player 1");
        System.out.println("cash: " + gameFlow.getPlayer(1).getCash());
        System.out.println("items" + gameFlow.getPlayer(1).getItemNames());
        System.out.println("player 2");
        System.out.println("cash: " + gameFlow.getPlayer(2).getCash());
        System.out.println("items" + gameFlow.getPlayer(2).getItemNames());
    }

     */
    public static class GameTimer {
        private Timer timer;
        private long startTime;
        private long elapsedTime;

        public GameTimer() {
            timer = new Timer();
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
        }

        public void startTimer() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }
            }, 0, 1000); // Update every second
        }

        public long getElapsedTime() {
            return elapsedTime / 1000; // Return elapsed time in seconds
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime; // Return elapsed time in seconds
        }

        // Check if the elapsed time exceeds the maximum time
        public boolean maxTimeReached(long maxTimeInSeconds) {
            return getElapsedTime() >= maxTimeInSeconds;
        }

        public void stopTimer() {
            timer.cancel();
        }
    }

    // Create a GameTimer instance for each game
    private static Map<Long, GameTimer> gameTimers = new HashMap<>();

    public static Map<Long, GameTimer> getGameTimers() {
        return gameTimers;
    }

    private static SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate){
        GameWebSocketController.messagingTemplate = messagingTemplate;
    }

    @Autowired
    private GameManagementService gameManagementService;

    //
    public static GameTimer getGameTimerById(Long gameId){
        return gameTimers.get(gameId);
    }

    //saving the current Game at the beginning
    private static HashMap<Long,Game> allGames = new HashMap<>();
    private static HashMap<Long,GameFlow> gameFlows = new HashMap<>();

    public static Game getCurrGame(Long lobbyId) {
        return allGames.get(lobbyId);
    }

    public static void setCurrGame(HashMap<Long,Game> currentGame) {
        allGames = currentGame;
    }

    public static void addGame(Long lobbyId, Game game){
        allGames.put(lobbyId,game);
    }

    public static void removeGame(Long lobbyId){
        allGames.remove(lobbyId);
    }

    //TODO: Setup the game

    public static GameFlow getGameFlow(Long lobbyId){
        return gameFlows.get(lobbyId);
    }

    public static void setGameFlow(HashMap<Long,GameFlow> currentGameFlow) {
        gameFlows = currentGameFlow;
    }

    public static void addGameFlow(Long lobbyId, GameFlow gameFlow){
        gameFlows.put(lobbyId,gameFlow);
    }

    public static void removeGameFlow(Long lobbyId){
        gameFlows.remove(lobbyId);
    }


    @MessageMapping("/game/create")
    public void createGame(String playerString) {
        Map<String, String> playerDict = gameManagementService.manualParse(playerString);
        String userId = playerDict.get("playerId");//NOSONAR

        System.out.println(userId);

        Long gameId = GameManagementService.createGame(userId);//NOSONAR
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));//NOSONAR

        String destination = "/queue/gameCreated";
        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }



    @MessageMapping("/board/item/{gameId}")
    public static void handleItems(String msg, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        //extract Info from message
        JSONObject jsonObject = new JSONObject(msg);
        String itemName = jsonObject.getString("itemUsed");
        String effectName;
        JSONObject effectParas;
        JSONObject choices = jsonObject.getJSONObject("choices");
        gameFlow.setChoices(choices);
        //get the effect with paras
        HashMap<String, JSONObject> items = Getem.getItems();
        JSONObject effectComplete = items.get(itemName);
        effectName = effectComplete.keys().next();
        effectParas = effectComplete.getJSONObject(effectName);
        //remove the item from the players hand
        gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).removeItemNames(itemName);

        handleEffects(effectName,effectParas, gameId);
    }

    @MessageMapping("/board/ultimate/{gameId}")
    public static void handleUltimate(String msg, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        //extract Info from message
        JSONObject jsonObject = new JSONObject(msg);
        String usable = jsonObject.getString("ultimateUsed");
        JSONObject choices = jsonObject.getJSONObject("choices");
        gameFlow.setChoices(choices);
        String effectName;
        JSONObject effectParas;
        //get the effect with paras
        HashMap<String, JSONObject> items = Getem.getUltimates();
        JSONObject effectComplete = items.get(usable);
        effectName = effectComplete.keys().next();
        effectParas = effectComplete.getJSONObject(effectName);
        //set the ultimate to disabled
        gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).setUltActive(false);

        handleEffects(effectName,effectParas,gameId);
    }

    public static void handleEffects(String effect, JSONObject effectParas, Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        switch (effect){
            case "updateMoney":
                gameFlow.updateMoney(effectParas);
                break;
            case "exchange":
                //TODO: insert choices here
                gameFlow.exchange(effectParas);
                break;
            case "givePlayerDice":
                gameFlow.givePlayerDice(effectParas);
                break;
            case "updatePositions":
                gameFlow.updatePositions(effectParas);
                break;
            case "givePlayerCardRand":
                gameFlow.givePlayerCardRand(effectParas);
                break;
            case "givePlayerCardChoice":
                gameFlow.givePlayerCardChoice(effectParas);
                break;
            case "exchangePositions":
                gameFlow.exchangePositions(effectParas);
                break;
            case "reduceMoneyALL":
                gameFlow.reduceMoneyALL(effectParas);
                break;
            case "changeGoalPosition":
                gameFlow.changeGoalPosition(effectParas);
                break;
            case "exchangeAll":
                gameFlow.exchangeAll();
                break;
            default:
                throw new RuntimeException("the defined effect does not exist");
        }
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/gameJoined")
    public  Map<String, Object> joinGame(String msg) {
        System.out.println(msg);
        Map<String, String> message = gameManagementService.manualParse(msg);

        Long gameId = Long.valueOf(message.get("gameId"));
        String userId = message.get("playerId");
        boolean joined = gameManagementService.joinGame(gameId, userId);

        Map <String, Object> response = new HashMap<>();
        response.put("gameId", gameId);
        if (joined){
            response.put("joined", joined);
        }else{
            response.put("joined", false);
        }
        System.out.println("Joining");
        return response;
    }

    @MessageMapping("/game/{gameId}/lobby")
    public void lobby(@DestinationVariable Long gameId){
        List<String> response = gameManagementService.lobbyPlayers(gameId);

        String destination = "/topic/players/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/gameReady")
    public void gameReady(@DestinationVariable Long gameId){
        Map<String, Object> response = new HashMap<>();
        if (gameManagementService.getPlayersInGame(gameId).size() == 4){
            response.put("gameReady", true);
        } else{
            response.put("gameReady", false);
        }
        GameFlow gameFlow = gameFlows.get(gameId);
        List<Player> players = allGames.get(gameId).getactive_Players();
        for(Player player : players){
            GameTimer timer = player.getAchievementProgress().getGameTimer();
            timer.startTimer();
            gameFlow.addPlayer(player);
        }
        GameTimer timer = new GameTimer();
        timer.startTimer();
        gameTimers.put(gameId, timer);
        gameFlows.put(gameId,gameFlow);

        String destination = "/topic/gameReady/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/leave")
    public void leaveGame(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        String playerId = message.get("playerId");

        gameManagementService.leaveGame(gameId, playerId);
    }

    @MessageMapping("/game/{gameId}/setUp")
    public void setUpGame(@DestinationVariable Long gameId){
        gameManagementService.changeGameStatus(gameId, GameStatus.SETUP);
    }

    @MessageMapping("/game/{gameId}/status")
    public void gameStatus(@DestinationVariable Long gameId){
        System.out.println("Get Status");

        GameStatus status = gameManagementService.getGameStatus(gameId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        System.out.println(response);

        String destination = "/topic/game/status/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/players")
    public void getPlayers(@DestinationVariable Long gameId, @Payload Map<String, Object> payload){
        System.out.println("getPlayers");

        String hostName = (String) payload.get("host");
        System.out.println(hostName);

        List<Player> players = gameManagementService.getActivePlayers(gameId);
        System.out.println(players);
        List<String> playerNames = new ArrayList<>();

        for(Player p: players){
            String name = p.getPlayerName();
            playerNames.add(name);
        }

        playerNames.remove(hostName);
        Map<String, Object> response = new HashMap<>();
        response.put("players", playerNames);

        String destination = "/topic/game/players/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/setTeammate")
    public void setTeammates(@DestinationVariable Long gameId, @Payload Map<String, Object> payload){
        Game game = gameManagementService.findGame(gameId);
        String player1 = (String) payload.get("host");
        String player2 = (String) payload.get("teammate");

        gameManagementService.setTeams(game, player1, player2);

        GameFlow gameFlow = new GameFlow();
        gameFlow.setGameId(gameId);
        gameFlow.setGameBoard(gameId);
        gameFlow.setCurrentTurn(1);
        gameFlow.setTurnPlayerId((long)(Math.random()*4+1));

    }

    @MessageMapping("/board/dice/{gameId}")
    public static void diceWalk(@DestinationVariable Long gameId){
        rollOneDice(gameId);
        move(gameId);
    }

    @MessageMapping("/game/{gameId}/playerAtLP")
    public void playersAtLoadingPage(@DestinationVariable Long gameId, @Payload Map<String, String> player){
        String playerName = player.get("username");
        gameManagementService.changePlayerStatus(gameId, playerName, PlayerStatus.READY);
        gameManagementService.setGameReady(gameId);
    }

    @MessageMapping("/game/{gameId}/board/start")
    public void startGame(@DestinationVariable Long gameId){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> players = gameManagementService.getInformationPlayers(gameId);

        response.put("turn order", players.keySet());
        response.put("players", players);
        String destination = "/topic/game/" + gameId +"/board/start";

        messagingTemplate.convertAndSend(destination, response);
        gameManagementService.changeGameStatus(gameId, GameStatus.PLAYING);
    }

    @MessageMapping("/board/cards/{gameId}")
    public void handleCardPosition(@Payload Map<String, String> payload, @DestinationVariable("gameId") Long gameId){
        String selectedCard = payload.get("usableUsed");
        int count = -123;

        if (payload.get("choice")!= null){
            JSONObject choiceJson = new JSONObject(payload.get("choice"));
            Map<String, Object> choice = choiceJson.toMap();
            // Extract the count value from the choice map
            count = (int) choice.getOrDefault("count", -123);
        }
        JSONObject card = Getem.getCards().get(selectedCard);
        String destination = "/topic/board/cards/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.updateCardPositions(card, count));
    }


    @MessageMapping("/board/junction/{gameId}")
    public void contJunction(@DestinationVariable Long gameId, @Payload Map<String, Long> payload){
        long selectedSpace = payload.get("selectedSpace");
        String destination = "/topic/board/junction/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), selectedSpace));
    }

    public static void rollOneDice(Long gameId) { //one die throw
        Map<String, Object> response = new HashMap<>();
        GameFlow gameFlow = gameFlows.get(gameId);
        List<Integer> dice = gameFlow.throwDice(1);
        gameFlow.setMovesLeft(dice.get(0));
        response.put("results", dice);
        String destination = "/topic/board/dice/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    public static void move(Long gameId){
        String destination = "/topic/board/move/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), gameFlow.getPlayers()[(int)(long)(gameFlow.getTurnPlayerId())].getPosition()));
    }

    public static void juncMove(Map<String, Object> partialMoveMsg, Long gameId){
        String destination = "/topic/board/move/" + gameId;
        messagingTemplate.convertAndSend(destination, partialMoveMsg);
    }

    public static void juncJunc(Map<String, Object> chooseJunctionMsg, Long gameId, Long playerId){
        String destination = "/topic/board/junction/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, chooseJunctionMsg);
    }

    public static void changeGoal(List<GameBoardSpace> spaces, Long gameId){
        String destination = "/topic/board/goal/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.setBoardGoal(spaces));
    }

    public static void newPlayer(Map<String, Object> nextTurnMsg, Long gameId){
        String destination = "/topic/board/newActivePlayer/" + gameId;
        messagingTemplate.convertAndSend(destination, nextTurnMsg);
    }

    public static void endy(Map<String, Object> endGameMsg, Long gameId){
        String destination = "/topic/board/gameEnd/" + gameId;
        List<Player> players = allGames.get(gameId).getactive_Players();
        for(Player player : players){
            GameTimer timer = player.getAchievementProgress().getGameTimer();
            timer.stopTimer();
        }
        messagingTemplate.convertAndSend(destination, endGameMsg);
    }

    public static void specItem(Map<String, Object> getItemMsg, Long gameId){
        String destination = "/topic/board/usable/" + gameId;
        messagingTemplate.convertAndSend(destination, getItemMsg);
    }

    public static void winCondiProgress(Map<String, Object> winCondiUpdate, Long playerId, Long gameId){
        String destination = "/topic/board/winCondition/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, winCondiUpdate);
    }

    public static void changeCash(Map<String, Object> cashmsg, Long gameId){
        String destination = "/topic/board/money/" + gameId;
        messagingTemplate.convertAndSend(destination, cashmsg);
    }

    public static void returnMoney(CashData cashData, Long gameId) {
        String destination = "/topic/board/money" + gameId; //NOSONAR
        messagingTemplate.convertAndSend(destination,cashData);
    }

    public static void returnMoves(MoveData moveData, Long gameId) {
        String destination = "/topic/board/move" + gameId;
        messagingTemplate.convertAndSend(destination, moveData);
    }

    public static void returnUsables(UsableData usableData, Long gameId) {
        String destination = "/topic/board/usables" + gameId;
        messagingTemplate.convertAndSend(destination, usableData);
    }

    public static void returnDice(DiceData diceData, Long gameId){
        String destination = "/topic/board/dice" + gameId;
        messagingTemplate.convertAndSend(destination, diceData);
    }
}