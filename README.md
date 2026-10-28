# Game Collection

This is a self-made collection of games implemented using Java Swing.

## Downloads and Servers

- [Main application JAR](https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/SpaceInvaders_jar)
- [Chess Server JAR](https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/ChessServer_jar)
- [Connect 4 Server JAR](https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/Connect4_jar)
- [Docker image archive containing both servers](https://github.com/dw2-privat-lab/Arcade-Collection/blob/master/src/GameCollectionServers.tar)
- [Docker Hub image](https://hub.docker.com/r/davidprivat/gamecollectionservers?uuid=5A677159-4C56-47F0-91DC-2C45B406D078)

### Server Ports and Networking

The Chess Server uses port `25565`, and the Connect 4 Server uses port `25566`.

When running the Docker image, the IP address shown in the Docker console is an address inside Docker and may not be reachable from another device. To find the host computer's IP address, run `ipconfig` on Windows or macOS, or `ip address` on Linux.

## Games

### Space Invaders
Move your ship left and right, and press **Space** to shoot. Press **Esc** to pause or resume.

### Snake
Guide the snake around the board to eat apples and grow. Use the arrow keys or **WASD**. Press **Esc** to pause or resume.

### Pong
Two players share the keyboard: the left paddle uses **W/S**, and the right paddle uses **Up/Down**. Press **Esc** to pause; use the on-screen buttons to resume or return to the menu.

### Chess
Choose **Play Locally** for a two-player game on one computer, or **Play Online** to host or join a game. Select and move pieces with the mouse. Online play requires a reachable host address; joining also requires the game code.

### Minesweeper
Choose Easy, Medium, or Hard, then reveal squares with the left mouse button. Use the right mouse button to flag a suspected mine. Board sizes are 9×9, 16×16, and 30×16, with 10, 40, and 99 mines respectively.

### Connect 4
Choose **Play Locally** to take turns on one computer, or **Play Online** to host or join a game. Click a column to drop a piece. Online play requires a reachable host address and a game code when joining.

### 2048
Use the arrow keys to slide tiles. Matching values merge; combine tiles to reach 2048. Select a board size from 3×3 to 7×7, or choose **New Game** to restart.

### Tetris
Use **Left/Right** to move a piece, **Down** to drop it faster, and **Up** to rotate it. Clear rows to score points. Use the on-screen controls to restart or return to the menu.

### Pac-Man
Use the arrow keys to move through the maze, eat dots, and avoid the ghosts.

## Running the App

Launch the `Main` class from your Java IDE, or run the main JAR file. A Java Runtime Environment (JRE) is required to run the JAR.

The game selection screen opens first; click a game to start. Use each game's return control to go back to the selection screen.

The project is configured as an IntelliJ IDEA Java module. Make sure the `resources` directory is included on the runtime classpath so the games can load their images and sounds.

## Technology

The desktop interface and game screens are built with Java Swing. Swing panels handle menus, rendering, and mouse input; keyboard listeners and Swing timers drive gameplay. The project also includes image and sound resources used by several games.

## Usage of AI

I used AI when I encountered bugs that I could not figure out on my own. For example, I may ask for help with a Java exception that I have not encountered before and need to resolve. I did not use AI to generate entirely new game logic.
