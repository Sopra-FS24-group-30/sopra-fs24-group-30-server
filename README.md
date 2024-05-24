# Mario After Party 30

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

## Game Wiki

The aim of the Game is to either pass the Goal, while having fulfilled your Win Condition, or by having the highest amount of Coins by the End of 20 Turns. If one Person wins, their Teammate automatically wins aswell.

During a Players Turn there are 3 Phases.

1. (Optional) Playing an Item or using ones Ultimate Attack.
2. Rolling the Dice or using a Card.
3. Movement Phase, where everything except for Junctions is done automatically for the Player.

### Win Conditions

### Usables

Usables are stuff that the Players can use during their Turn, which may either benefit them, or hinder their opponents.<br>
Usables are split into 3 categorys. Ultimate Attacks, Items and Cards.<br>
The Ultimate Attack, or Items can be used exclusively in Phase 1, the Cards in Phase 2.<br>
Using Usables is optional, Phase 1 can be skipped, and in Phase 2 a Dice can be rolled.<br>
Usables can be obtained by landing on special Spaces, or stealing them from other Players.

#### Ultimate Attack (Phase 1)

Each Player has an Ultimate Attack, which can be used once per Game, at the beginning of ones Turn, instead of using an Item.

| Ultimate Name | Effect |
|---|---|
| The Big Shuffle | The Win-Conditions of all Players get shuffled (It is possible to receive the one you originally had) |
| Pickpocket | Steal half of the Coins of all other players (including your teammate). |
| Fresh start :) | All other Players get teleported back to their starting Space. |
| /tp | Move to a random Space on the board (fun). |
| Nothing (Maybe you should've taken another Card?) | Nothing happens (still prevents using an Item). |
<!-- | Chameleon | Use the effect of any Item in the game. | -->
<!-- | Stop? | The game terminates after 5 more turns. (Round Counter gets set to 15 after this turn ends) | -->

#### Items (Phase 1)

Each Player can use one Item per Turn.<br>
Just like Items, Cards are split into <span style="color:#ffe600">Gold</span>, <span style="color:#AAAAAA">Silver</span> and <span style="color:#d97504">Bronze</span> ones, which is an indication of its general power level, but is purely cosmetic.<br>
Some Items cause the Player to automatically skip the second Phase.

| Item Name | Effect | Notes |
|---|---|---|
| Magic Mushroom | This turn you roll 2 dice. If you roll doubles: +10 Coins. | Skips Phase 2. |
| The uncle of your sister's cousin, has a brother-in-law, who once worked at Facebook | Use this item to enter a gate. This item gets used automatically at a gate, if you choose to pass the gate. | Cannot be used during Phase 1. |
| Peace I'm out | Teleport to a random other player. | |
| Ice cream treasure chest  | Steal a random card from a player of your choosing. | |
| OwO what's this | You receive a random card. | |
| Super Magic Mushroom | This turn you roll 3 dice. If you roll triplets: +30 Coins. | Skips Phase 2. |
| Treasure chest | Steal a random Item from a player of your choice. | |
| Meow you in particular | The goal moves to a different spot. | |
| Steering a submarine using an Xbox Controller | Every player (including yourself) loses 5 Coins. | |
| Ultra Magic Mushroom | This turn you roll 4 dice. If you roll quadruplets: +69 Coins. | Skips Phase 2. |
| Best Trade deal in the history of Trade deals maybe ever | Give another player of your choosing all of your cards, and steal all of their items in return. | |
| (Almost) all your Items are belong to mine | Steal 4 random items from a player of your choice. | |
| Only-Fans Subscription | Steal 7 Coins from every other player (even your Teammate) | |
| Dino Chicky Nuggie | −20 Coins, your ultimate becomes usable again. | |
<!-- | Two (Regular) Mushrooms | This turn you move twice as many spaces. | -->
<!-- | Stick | If you pass a player this turn, steal 15 Coins from them. | -->
<!-- | Meow it I'm out | Switch places with a random other player. | -->
<!-- | Bad Wifi | Chose a player, they get muted for 1 turn (2 if it was your teammate) | -->
<!-- | I am confusion | This turn you move backwards instead of forwards. | -->
<!-- | Golden Snitch | A Player of your choice has to roll the Big Oops Roulette. +10 Coins if it was your teammate. | -->

#### Cards (Phase 2)

Instead of Rolling a Player may instead play a Card, and move a certain amount of Spaces.<br>
Just like Items, Cards are split into <span style="color:#ffe600">Gold</span>, <span style="color:#AAAAAA">Silver</span> and <span style="color:#d97504">Bronze</span> ones.<br>

- <span style="color:#ffe600">Gold</span> Cards allow you to choose one of several numbers and move that many Spaces.  
- <span style="color:#AAAAAA">Silver</span> Cards have a single number, and have you move that many Spaces.
- <span style="color:#d97504">Bronze</span> Cards have multiple numbers on them, and you randomly move as many Spaces as one of those numbers.  
  
The following Cards exist for each category:

| Category | Cards |
|---|---|
| <span style="color:#ffe600">Gold</span> | 1/3, 2/6, 4/5, 0/4, 3/7, 1/2/5/6 |
| <span style="color:#AAAAAA">Silver</span> | 0, 1, 2, 3, 4, 5, 6, 7 |
| <span style="color:#d97504">Bronze</span> | 1/4, 2/6, 3/5, 1/3/5, 2/4/6, 1/2/3, 4/5/6, 0/7 |

### Spaces (Phase 3)

There are 

## Authors and Acknoledgement

(Marius)

## Keyboard Shortcuts

The following shortcuts are available on the Board.

| Key pressed | Effect |
|---|---|
| F1 | Shows a very helpful help message. |
| M | Mutes onself in the Voice Chat. |
| , | Zooms the board in. |
| . | Zooms the board out. |
| R | Resets the zoom level of the Board. |
| $ | Toggles the Board skin. |
| C | Changes the skins of the Players to the standard palette (<span style="color:#ff555d">Red</span>, <span style="color:#fff155">Yellow</span>, <span style="color:#82ff55">Green</span>, <span style="color:#55d9ff">Blue</span>) |
| Shift+C | Changes the skins of the Players to the alternative palette (<span style="color:#9500e5">Purple</span>, <span style="color:#ff8db2">Pink</span>, <span style="color:#ff8701">Orange</span>, <span style="color:#ffffff">White</span>) |
| X | Toggles the visibility of Status Messages (e.g. on which Space a Player landed). |
| Return | Toggles an Overlay, showing which way Players move. |
| Esc | Disables said Overlay. |

## License

(Marius)

## Layout/Design

(Marius)
