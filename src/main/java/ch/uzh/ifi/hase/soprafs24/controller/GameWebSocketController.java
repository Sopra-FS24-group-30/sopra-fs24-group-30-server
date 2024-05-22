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
            p.setUserId((long) i);
            p.setAchievementProgress(new AchievementProgress((long) i), new GameTimer());
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
        handleUltimate("{\"ultimateUsed\": \"Chameleon\",\"choice\": {}}",123456L);
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
        private boolean isRunning;

        public GameTimer() {
            timer = new Timer();
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
            isRunning = false;
        }

        public void startTimer() {
            if (!isRunning) {
                isRunning = true;
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        elapsedTime = System.currentTimeMillis() - startTime;
                    }
                }, 0, 1000); // Update every second
            }
        }


        public long getElapsedTime() {
            return elapsedTime; // Return elapsed time in seconds
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime; // Return elapsed time in seconds
        }

        // Check if the elapsed time exceeds the maximum time
        public boolean maxTimeReached(long maxTimeInSeconds) {
            return (getElapsedTime()/1000) >= maxTimeInSeconds;
        }

        public void stopTimer() {
            if (isRunning) {
                timer.cancel();
                isRunning = false;
                timer = new Timer(); // Reset the timer so it can be started again
            }
        }

        public boolean isTimerRunning() {
            return isRunning;
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

    @MessageMapping("/game/{gameId}/board/items")
    public static void handleItems(JSONObject jsonObject, @DestinationVariable("gameId") Long gameId){
        //System.out.println(msg);
        GameFlow gameFlow = gameFlows.get(gameId);
        if(gameFlow.isItemultused()){
            sendError("you already used an item or Ultimate this turn",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        if(gameFlow.isCardDiceUsed()){
            sendError("can't use item after a card or dice was used",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        //extract infos
        String itemName = jsonObject.getString("used");
        String effectName;
        JSONObject effectParas;
        JSONObject choices = jsonObject.getJSONObject("choice");
        gameFlow.setChoices(choices);
        //get the effect with paras
        HashMap<String, JSONObject> items = Getem.getItems();
        JSONObject effectComplete = items.get(itemName);
        effectName = effectComplete.keys().next();
        effectParas = effectComplete.getJSONObject(effectName);
        //remove the item from the players hand
        gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).removeItemNames(itemName);
        UsableData usableData = UsableData.prepateData(gameFlow);
        returnUsables(usableData,gameId);

        gameFlow.setItemultused(true);
        handleEffects(effectName,effectParas, gameId);
    }

    @MessageMapping("/game/{gameId}/board/ultimate")
    public static void handleUltimate(JSONObject jsonObject, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        if(gameFlow.isItemultused()){
            sendError("you already used an item or Ultimate this turn",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        if(gameFlow.isCardDiceUsed()){
            sendError("can't use item after a card or dice was used",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        if(!gameFlow.getActivePlayer().isUltActive()){
            sendError("you already used your ultimate, recharge with Dino Chicky Nuggie",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        //extract Info from message
        String usable = jsonObject.getString("used");
        JSONObject choices = jsonObject.getJSONObject("choice");
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
        UltimateData ultimateData = new UltimateData();
        ultimateData.prepareDataForCurrentPlayer(gameFlow);
        returnUltToPlayer(ultimateData,gameId,gameFlow.getActivePlayer().getUserId());
        gameFlow.setItemultused(true);
        handleEffects(effectName,effectParas,gameId);
    }

    public static void sendError(String error, Long gameId, Long userId){
        String userIdString = userId.toString();
        String destination = "/queue/board/" + gameId + "/error";
        messagingTemplate.convertAndSendToUser(userIdString,destination,error);
    }

    @MessageMapping("/game/{gameId}/board/test")
    public static void tes(String msg, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        gameFlow.getPlayer(1).addItemNames("MagicMushroom");
        UsableData usableData = UsableData.prepateData(gameFlow);
        System.out.println(gameFlow.getPlayer(1).getItemNames().toString());
        System.out.println("giving usables");
        returnUsables(usableData,gameId);
    }


    public static void handleEffects(String effect, JSONObject effectParas, Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        switch (effect){
            case "updateMoney":
                gameFlow.updateMoney(effectParas);
                break;
            case "exchange":
                gameFlow.exchange(effectParas);
                break;
            case "givePlayerDice":
                gameFlow.givePlayerDice(effectParas);
                break;
            case "updatePositions":
                gameFlow.updatePositions(effectParas);
                break;
            case "shuffle":
                gameFlow.shuffle(effectParas);
                break;
            case "updateTurns":
                gameFlow.updateTurns(effectParas);
                break;
            case "useRandomUsable":
                gameFlow.useRandomUsable(effectParas);
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
                throw new RuntimeException("the defined effect: " + effect + " does not exist");
        }
    }

    @MessageMapping("/game/join")
    public  void joinGame(String msg) {
        System.out.println(msg);
        Map<String, String> message = gameManagementService.manualParse(msg);

        Long gameId = Long.valueOf(message.get("gameId"));
        String userId = message.get("userId");
        boolean joined = gameManagementService.joinGame(gameId, userId);

        Map <String, Object> response = new HashMap<>();
        response.put("gameId", gameId);
        if (joined){
            response.put("joined", joined);
        }else{
            response.put("joined", false);
        }
        System.out.println("Joining");

        String destination = "/queue/gameJoined";
        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }

    @MessageMapping("/game/{gameId}/lobby")
    public void lobby(@DestinationVariable Long gameId){
        List<Object> response = gameManagementService.lobbyPlayers(gameId);

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
        GameFlow gameFlow = new GameFlow();
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

        if(response.get("status").equals("READY")){
            System.out.println("Attempting to send the playerIds");
            sendPlayerId(gameId);
        }

        String destination = "/topic/game/status/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    public void sendPlayerId(Long gameId){
        String destination = "/queue/game/PlayerId";

        Game game = gameManagementService.findGame(gameId);
        System.out.println(game);
        List<Player> playerList = game.getactive_Players();
        System.out.println(playerList);

        for(Player player: playerList){
            Map<String, String> response = new HashMap<>();
            response.put("playerId", String.valueOf(player.getPlayerId()));
            String userId = String.valueOf(player.getUserId());
            System.out.println("Sending the playerId of" + userId);
            messagingTemplate.convertAndSendToUser(userId, destination, response);
        }
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
    }

    @MessageMapping("/game/{gameId}/wincondition")
    public void getWincondition(@DestinationVariable Long gameId, @Payload Map<String, String> userIdMap){
        System.out.println("Request for wincondition");
        String userId = userIdMap.get("userId");

        String wincondition = gameManagementService.getWincondition(gameId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("Wincondition", wincondition);

        String destination = "/queue/game/" + gameId +"/wincondition";

        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }

    @MessageMapping("/game/{gameId}/ultimateAttack")
    public void getUltimateAttack(@DestinationVariable Long gameId, @Payload Map<String, String> userIdMap){
        System.out.println("Request for ultimate");
        String userId = userIdMap.get("userId");
        String ultimateAttack = gameManagementService.getUltimateAttack(gameId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("UltimateAttack", ultimateAttack);

        String destination = "/queue/game/" + gameId + "/ultimate";
        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }

    @MessageMapping("/game/{gameId}/playerAtLP")
    public void playersAtLoadingPage(@DestinationVariable Long gameId, @Payload Map<String, String> player){
        String playerName = player.get("username");
        gameManagementService.changePlayerStatus(gameId, playerName, PlayerStatus.READY);
        gameManagementService.setGameReady(gameId);
    }

    @MessageMapping("/game/{gameId}/board/start")
    public void startGame(@DestinationVariable Long gameId, @Payload String userId){
        System.out.println("board message got received");
        HashMap<String, Object> response = new HashMap<>();
        Map<String, Object> players = gameManagementService.getInformationPlayers(gameId);

        response.put("players", players);

        GameFlow gameFlow = gameFlows.get(gameId);
        gameFlow.setGameId(gameId);
        gameFlow.setGameBoard();
        gameFlow.setCurrentTurn(1);
        int startingPlayer = (int) (Math.random() * 4 + 1);
        gameFlow.setTurnPlayerId((long) startingPlayer);
        gameFlows.put(gameId, gameFlow);

        List<String> turnOrder = gameManagementService.getTurnOrder(gameFlow.getTurnPlayerId());

        response.put("TurnOrder", turnOrder);
        String destination = "/topic/game/" + gameId + "/board/start";

        messagingTemplate.convertAndSend(destination, response);
        gameManagementService.changeGameStatus(gameId, GameStatus.PLAYING);
        setupFront(gameId);
        firstPlayerTurn(gameId);
    }

    public void firstPlayerTurn(Long gameId){
        GameFlow gameFlow = getGameFlow(gameId);
        Map<String, Object> response = new HashMap<>();
        response.put("currentTurn", gameFlow.getCurrentTurn());
        response.put("activePlayer", gameFlow.getTurnPlayerId());

        String destination = "/topic/game/" + gameId + "/board/newActivePlayer";
        messagingTemplate.convertAndSend(destination, response);
    }

    public void setupFront(Long gameId){
        GameFlow gameFlow = getGameFlow(gameId);

        CashData cashdata = new CashData(gameFlow);
        String destinationCash = "/topic/game/" + gameId + "/board/money";
        messagingTemplate.convertAndSend(destinationCash, cashdata);

        String destinationMove = "/topic/game/" + gameId + "/board/move";
        String destinationUltimate = "/queue/game/" + gameId + "/board/ultimative";
        for (Player p : gameFlow.getPlayers()){
            String pUserId = p.getUserId().toString();

            ArrayList<Long> moveit = new ArrayList<>();
            moveit.add(p.getPosition());
            MoveData moveData = new MoveData("teleport");
            moveData.setPlayerSpaceMovesColour(p.getPlayerId().intValue(), moveit, 0, null);
            Map<String, Object> aha = moveData.getPlayerMoveMap(p.getPlayerId().intValue());

            gameFlow.checkWinCondition(p);

            Map<String, Object> ultimap = Map.of("name", p.getUltimate(), "active", false);

            messagingTemplate.convertAndSend(destinationMove, aha);
            messagingTemplate.convertAndSendToUser(pUserId, destinationUltimate, ultimap);
        }

        Map<String, Long> goalData = gameFlow.setBoardGoal(gameFlow.getGameBoard().getSpaces());
        String destinationGoal = "/topic/game/" + gameId + "/board/goal";
        messagingTemplate.convertAndSend(destinationGoal, goalData);
    }

    @MessageMapping("/game/{gameId}/board/cards")
    public void handleCardPosition(@Payload Map<String, String> payload, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        String selectedCard = payload.get("usableUsed");
        if(gameFlow.isCardDiceUsed()){
            sendError("you already used a card or rolled the dice this turn",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        int count = -123;

        if (payload.get("choice")!= null){
            JSONObject choiceJson = new JSONObject(payload.get("choice"));
            Map<String, Object> choice = choiceJson.toMap();
            // Extract the count value from the choice map
            count = (int) choice.getOrDefault("count", -123);
        }
        JSONObject card = Getem.getCards().get(selectedCard);
        String destination = "/topic/game/" + gameId + "/board/move";
        //remove card from players hand and send new data to frontend
        gameFlow.getActivePlayer().removeCardNames(selectedCard);
        UsableData usableData = new UsableData();
        usableData.prepateData(gameFlow);
        returnUsables(usableData,gameId);

        gameFlow.setCardDiceUsed(true);
        messagingTemplate.convertAndSend(destination, gameFlow.updateCardPositions(card, count));
    }

    @MessageMapping("/game/{gameId}/board/dice")
    public static void diceWalk(@DestinationVariable Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        if(gameFlow.isCardDiceUsed()){
            sendError("you already used a card or rolled the dice this turn",gameId,gameFlow.getActivePlayer().getUserId());
            return;
        }
        System.out.println("Received message and now getting dice: ");
        rollOneDice(gameId);
        gameFlow.setCardDiceUsed(true);
        move(gameId);
    }

    @MessageMapping("/game/{gameId}/board/junction")
    public void contJunction(@DestinationVariable Long gameId, @Payload Map<String,String> payload){
        long selectedSpace =  Long.parseLong(payload.get("choice"));
        String destination = "/topic/game/" + gameId + "/board/move";
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), selectedSpace));
    }

    public static void rollOneDice(Long gameId) { //one die throw
        Map<String, Object> response = new HashMap<>();
        GameFlow gameFlow = gameFlows.get(gameId);
        List<Integer> dice = gameFlow.throwDice(1);
        gameFlow.setMovesLeft(dice.get(0));
        response.put("results", dice);
        String destination = "/topic/game/" + gameId + "/board/dice" ;
        messagingTemplate.convertAndSend(destination, response);
    }

    public static void move(Long gameId){
        String destination = "/topic/game/" + gameId + "/board/move";
        GameFlow gameFlow = gameFlows.get(gameId);
        int movis = gameFlow.getMovesLeft();
        Long posis = gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).getPosition();
        Map<String, Object> massage = gameFlow.move(movis, posis);
        messagingTemplate.convertAndSend(destination, massage);
    }

    public static void returnJunction(Map<String, Object> chooseJunctionMsg, Long gameId, Long userId){
        String destination = "/queue/game/" + gameId + "/board/junction";
        messagingTemplate.convertAndSendToUser(userId.toString(), destination, chooseJunctionMsg);
    }

    public static void changeGoal(List<GameBoardSpace> spaces, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/goal";
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.setBoardGoal(spaces));
    }

    public static void newPlayer(Map<String, Object> nextTurnMsg, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/newActivePlayer";
        GameFlow gameFlow = gameFlows.get(gameId);
        gameFlow.setCardDiceUsed(false);
        gameFlow.setItemultused(false);
        messagingTemplate.convertAndSend(destination, nextTurnMsg);
    }

    public static void specItem(Map<String, Object> getItemMsg, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/usables";
        messagingTemplate.convertAndSend(destination, getItemMsg);
    }

    public static void winCondiProgress(Map<String, Object> winCondiUpdate, Long userId, Long gameId){
        String destination = "/queue/game/" + gameId + "/board/winCondition";
        messagingTemplate.convertAndSendToUser(userId.toString(), destination, winCondiUpdate);
    }

    public static void returnMoney(Map<String, Object> cashmsg, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/money";
        messagingTemplate.convertAndSend(destination, cashmsg);
    }

    public static void returnMoney(CashData cashData, Long gameId) {
        String destination = "/topic/game/" + gameId + "/board/money"; //NOSONAR
        messagingTemplate.convertAndSend(destination,cashData);
    }

    public static void returnMoves(MoveData moveData, Long gameId) {
        String destination = "/topic/game/" + gameId + "/board/move";
        messagingTemplate.convertAndSend(destination, moveData);
    }

    public static void returnMoves(Map<String, Object> moveData, Long gameId) {
        String destination = "/topic/game/" + gameId + "/board/move";
        messagingTemplate.convertAndSend(destination, moveData);
    }

    public static void returnUsables(UsableData usableData, Long gameId) {
        String destination = "/topic/game/" + gameId + "/board/usables";
        System.out.println("returning to usables");
        messagingTemplate.convertAndSend(destination, usableData);
    }

    public static void returnDice(DiceData diceData, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/dice";
        messagingTemplate.convertAndSend(destination, diceData);
    }

    public static void endGame(Long gameId){

//        GameManagementService.changeGameStatus(allGames.get(gameId), GameStatus.NOT_PLAYING);
        Map<String, String> endGameMsg = new HashMap<>(Map.of("status", GameStatus.NOT_PLAYING.toString()));
        String destination = "/topic/game/" + gameId + "/board/gameEnd";
        messagingTemplate.convertAndSend(destination, endGameMsg);
    }

    @MessageMapping("/game/{gameId}/ranking")
    public void gameRank(@DestinationVariable Long gameId){
        Map<String, Object> winMsg = getGameFlow(gameId).getWinMsg();
        String destination = "/topic/game/" + gameId + "/ranking";
        messagingTemplate.convertAndSend(destination, winMsg);
    }

    public static void returnUltToPlayer(UltimateData ultimateData, Long gameId, Long userId){
        String destination = "/queue/game/" + gameId + "/board/ultimative";
        messagingTemplate.convertAndSendToUser(userId.toString(),destination,ultimateData);
    }

    public static void returnTurnActive(TurnActiveData turnActiveData, Long gameId){
        String destination = "/topic/game/" + gameId + "/board/newActivePlayer";
        messagingTemplate.convertAndSend(destination,turnActiveData);
    }
}