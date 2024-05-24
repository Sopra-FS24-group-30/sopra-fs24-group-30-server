# Mario After Party

## Introduction

*Four people, four friends, two vs. two – who will win?*<br>
Mario After Party is an online board game, where you play in teams of two against each other. Your goal as a team? Beat the other one! But how?
The simplest way is by gaining money by walking over the goal. Whoever has the highest amount of money at the end, wins the game, but winning that way is boring...
Therefore, each player gets a unique WinCondition at the beginning. You can throw a die during your turn and move closer to the goal. 
Once you or your teammate reaches the goal with the fulfilled WinCondition, the game terminates.

In addition to the WinCondition, each player gets a unique Ultimate that can be used once over the course of the game. They can modify the game state in your favor. Or not. 
Anyways... you can also collect usables throughout the game and use them during your turn. Usables have a less significant effect than Ultimates. 
But same as Ultimates, usables might be beneficial for you, or they might not.
Really, just be prepared for anything that can happen to your Money, your collected usables, or even You.

With that being said, find yourself three friends and enjoy the game [here](https://sopra-fs24-group-30-client.oa.r.appspot.com/register) :)<br>
Click [here](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-client) if you want to check out the Client side.

## Technologies

### Voice Chat API

### Websockets

We integrated websockets in our web game, to ensure real-time communication between the client and the server.
The, by SPRING boot provided, websockets create a state or a connection between the client and backend. 
Compared to REST APIs which needs to send a request to get a response from the backend, by using websockets it is possible to
send responses from the backend without necessarilly being called upon. Furthermore, the user can receive userscpecific information
which is not only important to send different winconditions to different clients, but also because strengthens our system.
The websockets are being configured as soon as the server is being started. As soon as the user either creates or joins a game, he is being
connected with the websockets, and his session as well as user ID is being safed in a hashmap in the backend, to send user specific requests.

## High-level components

The Server consists of several components that are working together:

- ### Controller
    We have two main controllers for handeling the communication between client and server.
    The [UserController](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java) 
    manages the REST communication. This way, users can log in and see other peoples profiles.
    The [GameWebSocketController](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameWebSocketController.java)
    manages the websocket communication. This way, the players can interact in real-time.

- ### Service
    There is a [UserService](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java) for User specific tasks such as login and logout.
    The [GameService](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java) and 
    [GameManagementService](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java) handle game related functionalities such as creating and setting up the game.
    The [AchievementService](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/AchievementService.java) will save the Achievements to the corresponding User.

- ### Logic
    This is the main component of our game. Everything game related happens in here. The [GameFlow](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/GameFlow.java)
    will keep track of the game progress and update its state accordingly. A [Player](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Player.java)
    stores its own information, keeping track of their WinConditionProgress for example, and also link to the [User](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java)entity
    for updating their achievements via server.

## Launch and Deployment

(Carlos)

## Illustrations

(Céline)

## Roadmap

If you love coding and want to make this awesome game more awesome this list of possible features offers some inspiration.

- ### Round Timer

    In a round based multiplayer game there is always the problem of long wait times. A Round timer can solve this problem, guaranteeing that
    each player stays engaged. Keep in mind to make it so the user can adapt the timer, so everyone can enjoy the game in their own way.

- ### Gamcode sharing and Friends

  As of now to play together the players need to connect over some external source to share the game code. It is desirable, that the game can be played independently
  of external connections. This can be achieved by letting the host of the game choose players by name to add to the game. Like this people can share their usernames
  when they are together, split up and still play the game together.

  Adding a friendslist removes the downside of having to remember the username of your friends while improving user experience. Concretely the functionalities to
  join a friend which is hosting a game or adding a friend to a game with a single click would have a great impact on the usability of the game.

- ### Items & Ultimates

    The game is yet limited to usage of items, which have a single choice. Giving the option of selecting multiple players e.g. could lead to
  new and possibly entertaining items.

    For insipration you can have a look [here](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-client/blob/main/Infos.md).
  
    To understand how items are implemented have a look at
  - [GameWebSocketController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameWebSocketController.java)
    - here the items arrive from the frontend, the message is processed and then an effect is triggered with the handleEffects method.
    - look for the handleUltimate and handleItems methods
  - [GameFlow](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/GameFlow.java)
    - here the effects are evaluated and the status of the game gets updated
    - the methods are called by the handleEffects method of the GameWebSocketController. If you add a completely new effect make sure to add it there.
  - [Items](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/items.json) & [Ultimates](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/ultimates.json)
    - here are all the defined items and ultimates

## Items, Cards, Spaces, Effects, etc

(Marius)

## Authors and acknoledgement

(Marius)

## Keybinds

| Key pressed | Effect |
|---|---|
| M | Mutes onself in the Voice Chat. |
| , | Zooms the board in. |
| . | Zooms the board out. |
| R | Resets the zoom level of the Board. |
| $ | Toggles the Board skin. |
| C | Changes the skins of the Players to the following palette: 🩵 💚 💛 ❤️ |
| Shift+C | Changes the skins of the Players to the following palette: 🩷 💜 🧡 🤍 |
| X | Toggles the visibility of Status Messages (e.g. on which Space a Player landed). |
| Return | Toggles an Overlay, showing which way Players move. |
| Esc | Disables said Overlay. |

## License

(Marius)

## Layout/Design

(Marius)
