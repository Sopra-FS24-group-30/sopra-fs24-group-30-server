# Mario After Party 30

## Table of Contents

- [Introduction](#introduction)
- [Technologies](#technologies)
  - [Voice Chat API](#voice-chat-api)
  - [Websockets](#websockets)
- [High-level components](#high-level-components)
- [Launch and Deployment](#launch-and-deployment)
- [Setup this Template with your IDE of choice](#setup-this-template-with-your-ide-of-choice)
  - [IntelliJ](#intellij)
  - [VS Code](#vs-code)
- [Building with Gradle](#building-with-gradle)
  - [Build](#build)
  - [Run](#run)
  - [Test](#test)
  - [Development Mode](#development-mode)
- [Roadmap](#roadmap)
- [Game Wiki](#game-wiki)
  - [Win Conditions](#win-conditions)
  - [Usables](#usables)
    - [Ultimate Attack (Phase 1)](#ultimate-attack-phase-1)
    - [Items (Phase 1)](#items-phase-1)
    - [Cards (Phase 2)](#cards-phase-2)
  - [Spaces (Phase 3)](#spaces-phase-3)
  - [Keyboard Shortcuts](#keyboard-shortcuts)
- [Authors and Acknowledgement](#authors-and-acknowledgement)
- [License](#license)

## Introduction

*Four people, four friends, two vs. two – who will win?*<br>
Mario After Party is an online board game, where you play in teams of two against each other. Your goal as a team? Beat the other one! But how?
The simplest way is by gaining money by walking over the goal. Whoever has the highest amount of money at the end, wins the game, but winning that way is boring…
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
The voice chat lets people communicate in realtime. It features a global and a team chat. In the global chat everyone can hear each
other while the team chat is exclusive for the team. The voice chat was implemented with an external api called [agora](https://www.agora.io/en/).
It uses websockets for the connection. To ensure no conflicts the userId is used for the userId and the channels are made up of a combination of words
and the lobbyId. This ensures that all players which are in a different game have unique channels.

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
    for updating their achievements via service.

## Launch and Deployment

Important infomration about SpringBoot:
Spring Boot simplifies the process of creating and deploying Spring applications by providing a range of features designed to reduce configuration and setup time. This application makes use of this framework. For further information, you can check out this links:

    Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
    Guides: http://spring.io/guides
        Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
        Building REST services with Spring: https://spring.io/guides/tutorials/rest/

## Setup this Template with your IDE of choice

Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ

If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license, if you are a student of course [here](https://www.jetbrains.com/community/education/#students).

1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code

The following extensions can help you get started more easily:

- `vmware.vscode-spring-boot`
- `vscjava.vscode-spring-initializr`
- `vscjava.vscode-spring-boot-dashboard`
- `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the *Gradle Tasks* extension. Then check the *Spring Boot Dashboard* extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
Gradle is a build automation tool that supports a wide range of project types and build processes. It simplifies dependency management, supports incremental builds and build caching. You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build
To initiate the build or the run commands you click on them here, so you don't have to type them. 
```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

By visiting `localhost:8080` in your browser, you can verify that the server is running.

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed. This will make the coding process significantly easier.

Start two terminal windows and run:

`./gradlew build --continuous`

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

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

    The game is yet limited to usage of [Items](#items-phase-1), which have a single choice. Giving the option of selecting multiple players e.g. could lead to
  new and possibly entertaining [Items](#items-phase-1).

    For insipration you can have a look [here](https://github.com/Sopra-FS24-group-30/sopra-fs24-group-30-client/blob/main/Infos.md).
  
    To understand how [Items](#items-phase-1) are implemented have a look at
  - [GameWebSocketController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameWebSocketController.java)
    - here the [Items](#items-phase-1) arrive from the frontend, the message is processed and then an effect is triggered with the handleEffects method.
    - look for the handleUltimate and handleItems methods
  - [GameFlow](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/GameFlow.java)
    - here the effects are evaluated and the status of the game gets updated
    - the methods are called by the handleEffects method of the GameWebSocketController. If you add a completely new effect make sure to add it there.
  - [Items](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/items.json) & [Ultimates](src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/ultimates.json)
    - here are all the defined [Items](#items-phase-1) and ultimates
    - In the Client, an Item needs a Picture, which is called `<ItemName>.png` and is stored in `src\assets\usables`.<br>Additional data, inlcuding choices, are to be defined in `usables.json`, located in `src\assets\data`.<br> In the `choices` key, acceptable values are: `otherPlayerId`, `playerId`, or an Array with 2 or 4 Values.<br> Cards, Ultimates and Win Conditions work much the same way, except that the data is saved in `wincondition.json` and `ultimates.json` respectively.

## Game Wiki

The aim of the Game is to either pass the Goal, while having fulfilled your Win Condition, or by having the highest amount of Coins by the End of 20 Turns. If one Person wins, their Teammate automatically wins aswell.

During a Players Turn there are 3 Phases.

1. (Optional) Playing an [Item](#items-phase-1) or using ones [Ultimate Attack](#ultimate-attack-phase-1).
2. Rolling the Dice or using a [Card](#cards-phase-2).
3. Movement Phase, where everything except for Junctions is done automatically for the Player.

### Win Conditions

Once you or your Teammate passes the Goal, while having fulfilled the condition, you win.<br>
If any Player has Jack Sparrow, CAPTAIN Jack Sparrow

| Win Ccndition Name | Condition |
|---|---|
| Golden is my … | Land on seven Yellow [Spaces](#spaces-phase-3). |
| 🛥️ ← this is a ship (it goes Zvvvvvvvvvvv or blubblub) | Move 15 [Spaces](#spaces-phase-3) in one turn, or move 0 [Spaces](#spaces-phase-3) twice in a row. |
| Explorer of the Seven seas | Pass every [Space](#spaces-phase-3) at least once. |
| Drunk | Land on a Catnami [Space](#spaces-phase-3) thrice. |
| Unlucky | Lose a total of 40 Coins. |
| East Indian Trading Company | As long as you have at least 60 Coins, the Win Condition is fulfilled. If you have less than 60 Coins at any point in time, your Wincondition is no longer fulfilled. |
| The Marooned | As long as you have exactly 0 Coins, 0 [Items](#items-phase-1) and 0 [Cards](#cards-phase-2) the Win Condition is fulfilled. If you gain any Coins, [Items](#items-phase-1) or [Cards](#cards-phase-2) at any point in time, your Win Condition is no longer fulfilled. |
| Jack Sparrow, CAPTAIN Jack Sparrow | You win if the other Team wins, and you lose if your Partner wins. If the game ends after 20 Turns, everyone except for your Partner loses. |
<!-- | Third time's the charm | Pass the goal twice | -->
<!-- | Ohh shiny | Use one Bronze, Silver and Gold [Item](#items-phase-1), and one Bronze, Silver and Gold [Card](#cards-phase-2). | -->

### Usables

Usables are stuff that the Players can use during their Turn, which may either benefit them, or hinder their opponents.<br>
Usables are split into 3 categorys. [Ultimate Attacks](#ultimate-attack-phase-1), [Items](#items-phase-1) and [Cards](#cards-phase-2).<br>
The [Ultimate Attack](#ultimate-attack-phase-1), or [Items](#items-phase-1) can be used exclusively in Phase 1, the [Cards](#cards-phase-2) in Phase 2.<br>
Using Usables is optional, Phase 1 can be skipped, and in Phase 2 a Dice can be rolled.<br>
Usables can be obtained by landing on special [Spaces](#spaces-phase-3), or stealing them from other Players.

#### Ultimate Attack (Phase 1)

Each Player has an [Ultimate Attack](#ultimate-attack-phase-1), which can be used once per Game, at the beginning of ones Turn, instead of using an [Item](#items-phase-1).<br>
The starting Player may not use their [Ultimate Attack](#ultimate-attack-phase-1) on their first Turn, in order to keep the game balancend.

| Ultimate Name | Effect |
|---|---|
| The Big Shuffle | The Win-Conditions of all Players get shuffled (It is possible to receive the one you originally had) |
| Pickpocket | Steal half of the Coins of all other players (including your teammate). |
| Fresh start :) | All other Players get teleported back to their starting [Space](#spaces-phase-3). |
| /tp | Move to a random [Space](#spaces-phase-3) on the board (fun). |
| Nothing (Maybe you should've taken another Card?) | Nothing happens (still prevents using an [Item](#items-phase-1)). |
<!-- | Chameleon | Use the effect of any [Item](#items-phase-1) in the game. | -->
<!-- | Stop? | The game terminates after 5 more turns. (Round Counter gets set to 15 after this turn ends) | -->

#### Items (Phase 1)

Each Player can use one [Item](#items-phase-1) per Turn.<br>
Just like [Items](#items-phase-1), [Cards](#cards-phase-2) are split into <span style="color:#ffe600">Gold</span>, <span style="color:#AAAAAA">Silver</span> and <span style="color:#d97504">Bronze</span> ones, which is an indication of its general power level, but is purely cosmetic.<br>
Some [Items](#items-phase-1) cause the Player to automatically skip the second Phase.

| Item Name | Effect | Notes |
|---|---|---|
| Magic Mushroom | This turn you roll 2 dice. If you roll doubles: +10 Coins. | Skips Phase 2. |
| The uncle of your sister's cousin, has a brother-in-law, who once worked at Facebook | Use this [Item](#items-phase-1) to enter a gate. This [Item](#items-phase-1) gets used automatically at a gate, if you choose to pass the gate. | Cannot be used during Phase 1. |
| Peace I'm out | Teleport to a random other player. | |
| Ice cream treasure chest  | Steal a random [Card](#cards-phase-2) from a player of your choosing. | |
| OwO what's this | You receive a random [Card](#cards-phase-2). | |
| Super Magic Mushroom | This turn you roll 3 dice. If you roll triplets: +30 Coins. | Skips Phase 2. |
| Treasure chest | Steal a random [Item](#items-phase-1) from a player of your choice. | |
| Meow you in particular | The goal moves to a different spot. | |
| Steering a submarine using an Xbox Controller | Every player (including yourself) loses 5 Coins. | |
| Ultra Magic Mushroom | This turn you roll 4 dice. If you roll quadruplets: +69 Coins. | Skips Phase 2. |
| Best Trade deal in the history of Trade deals maybe ever | Give another player of your choosing all of your [Cards](#cards-phase-2), and steal all of their [Items](#items-phase-1) in return. | |
| (Almost) all your Items are belong to mine | Steal 4 random [Items](#items-phase-1) from a player of your choice. | |
| Only-Fans Subscription | Steal 7 Coins from every other player (even your Teammate) | |
| Dino Chicky Nuggie | −20 Coins, your ultimate becomes usable again. | |
<!-- | Two (Regular) Mushrooms | This turn you move twice as many [Spaces](#spaces-phase-3). | -->
<!-- | Stick | If you pass a player this turn, steal 15 Coins from them. | -->
<!-- | Meow it I'm out | Switch places with a random other player. | -->
<!-- | Bad Wifi | Chose a player, they get muted for 1 turn (2 if it was your teammate) | -->
<!-- | I am confusion | This turn you move backwards instead of forwards. | -->
<!-- | Golden Snitch | A Player of your choice has to roll the Big Oops Roulette. +10 Coins if it was your teammate. | -->

#### Cards (Phase 2)

Instead of Rolling a Player may instead play a [Card](#cards-phase-2), and move a certain amount of [Spaces](#spaces-phase-3).<br>
Just like [Items](#items-phase-1), [Cards](#cards-phase-2) are split into <span style="color:#ffe600">Gold</span>, <span style="color:#AAAAAA">Silver</span> and <span style="color:#d97504">Bronze</span> ones.<br>

- <span style="color:#ffe600">Gold</span> [Cards](#cards-phase-2) allow you to choose one of several numbers and move that many [Spaces](#spaces-phase-3).  
- <span style="color:#AAAAAA">Silver</span> [Cards](#cards-phase-2) have a single number, and have you move that many [Spaces](#spaces-phase-3).
- <span style="color:#d97504">Bronze</span> [Cards](#cards-phase-2) have multiple numbers on them, and you randomly move as many [Spaces](#spaces-phase-3) as one of those numbers.  
  
The following [Cards](#cards-phase-2) exist for each category:

| Category | Cards |
|---|---|
| <span style="color:#ffe600">Gold</span> | 1/3, 2/6, 4/5, 0/4, 3/7, 1/2/5/6 |
| <span style="color:#AAAAAA">Silver</span> | 0, 1, 2, 3, 4, 5, 6, 7 |
| <span style="color:#d97504">Bronze</span> | 1/4, 2/6, 3/5, 1/3/5, 2/4/6, 1/2/3, 4/5/6, 0/7 |

### Spaces (Phase 3)

There are 8 different [Spaces](#spaces-phase-3), which trigger fun effects when landed upon.

| Space Name | Effect when landed upon |
|---|---|
| <span style="color:#10107b; background-color:#f2f2f2"> ◉ </span> Blue | Gain 4 Coins |
| <span style="color:#f70901; background-color:#f2f2f2"> ◉ </span> Small Oops | You or everyone loses 10 Coins, or you get teleported back to your starting [Space](#spaces-phase-3). |
| <span style="color:#fefffd; background-color:#0a0704"> ◉ </span> Big Oops | Communism, lose 69 Coins, or the positions of all Players get shuffled. |
| <span style="color:#529c31; background-color:#ecf6e9"> ◉ </span> Item | You receive a random [Item](#items-phase-1). |
| <span style="color:#529c31; background-color:#ffa5a4"> ◉ </span> Card | You receive a random [Card](#cards-phase-2). |
| <span style="color:#529c31; background-color:#eea805"> ◉ </span> Gambling | Randomly double or lose all of your Coins, [Items](#items-phase-1) or [Cards](#cards-phase-2). |
| <span style="color:#529c31; background-color:#0d12c1"> ◉ </span> Catnami | Gain 69 Coins or swap your Win Condition with that of another Player. |
| <span style="color:#fddc11; background-color:#59270e"> ◉ </span> Yellow | A fun effect happens, depending on which exact Yellow [Space](#spaces-phase-3) was landed upon. A detailed table is visible in `infos.md` in the client repository (Certain data may be not be up to date). |

There are 4 different walkover [Spaces](#spaces-phase-3), which trigger fun effects when walked over.

| Space Name | Effect when walked over. |
|---|---|
| <span style="color:#483115; background-color:#886732"> ▣ </span> Bridge | You receive a random [Item](#items-phase-1). |
| <span style="color:#b52910; background-color:#fecb67"> × </span> Goal | If your Win Condition is fulfilled, you win the game, otherwise you gain 15 Coins and the Goal moves to a different [Space](#spaces-phase-3). Covers a Blue [Space](#spaces-phase-3). |
| <span style="color:#d4750f; background-color:#7e4b0c"> ↔ </span> Junction | Chose the direction in which you want to move. |
| <span style="color:#b3adab; background-color:#7e4b0c"> ↔ </span> Gate | If you have a The uncle of your sister's cousin, has a brother-in-law, who once worked at Facebook, you may chose in which direction you want to move. If you chose the gated one, you will lose said [Item](#items-phase-1). |

### Keyboard Shortcuts

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
| X | Toggles the visibility of Status Messages (e.g. on which [Space](#spaces-phase-3) a Player landed). |
| Return | Toggles an Overlay, showing which way Players move. |
| Esc | Disables said Overlay. |

## Authors and Acknowledgement

- [Céline Mai Anh Ziegler](https://github.com/CelineZi)
- [Ambros Eberhard](https://github.com/ambros02)
- [Carlos Hernandez](https://github.com/KarlGrossGROSS)
- [Thi Tam Gian Nguyen](https://github.com/tamtam-27)
- [MetaKnightEX](https://github.com/MetaKnightEX)

We want to thank our teaching assistant [Marco Leder](https://github.com/marcoleder) for the support during the semester.

## License

The Code is licensed under the [Apache 2.0 License](./LICENSE).
