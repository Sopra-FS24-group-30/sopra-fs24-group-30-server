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

Important Information about Spring Boot

    Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
    Guides: http://spring.io/guides
        Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
        Building REST services with Spring: https://spring.io/guides/tutorials/rest/
        
## Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).<!---->

### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license, if you are a student of course [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:

-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application (You can click on it, instead of typing the commands manually).
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

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
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`



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
