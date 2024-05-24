# Sopra Group 30 Readme

## Introduction

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
(Ta)
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

## Items, Cards, Spaces, Effects, etc.
(Marius)
## Authors and acknoledgement
(Marius)
## Keybinds
(Marius)
## License
(Marius)
## Layout/Design
(Marius)