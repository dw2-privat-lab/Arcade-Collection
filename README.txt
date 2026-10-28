GAME COLLECTION
This is a self-made game collection. All Games implemented use purely java swing.
Link to the main .jar file: https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/SpaceInvaders_jar
Link to the Chess Server .jar: https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/ChessServer_jar
Link to the Connect4 Server .jar: https://github.com/dw2-privat-lab/Arcade-Collection/tree/master/out/artifacts/Connect4_jar

Link to a docker Image having both Servers implemented: https://github.com/dw2-privat-lab/Arcade-Collection/blob/master/src/GameCollectionServers.tar
or https://hub.docker.com/r/davidprivat/gamecollectionservers?uuid=5A677159-4C56-47F0-91DC-2C45B406D078

Note: running the ChessServer is going to utilize port 25565; running the connect4Server will use port 25566;
      running the docker image will give an ipAddress in the docker console. this isn't the device ip and therefore connecting to it won't work.
      if you want the real ip address run "ipConfig" in the cmd on windows/macOs. for linux the command is "ip address"

GAMES:

Space Invaders
Move your ship left and right, and press Space to shoot. Press Esc to pause or resume.

Snake
Guide the snake around the board to eat apples and grow. Use the arrow keys or WASD. Press Esc to pause or resume.

Pong
Two players share the keyboard: the left paddle uses W/S, and the right paddle uses Up/Down. Press Esc to pause; use the on-screen buttons to resume or return to the menu.

Chess
Choose Play locally for a two-player game on the same computer, or Play online to host or join a game. Select and move pieces with the mouse. Online play needs a reachable host address; joining also uses the game code.

Minesweeper
Choose Easy, Medium, or Hard, then reveal squares with the left mouse button. Use the right mouse button to flag a suspected mine. The board sizes are 9x9, 16x16, and 30x16, with 10, 40, and 99 mines respectively.

Connect 4
Choose Play Locally to take turns on one computer, or Play Online to host or join a game. Click a column to drop a piece. Online play needs a reachable host address and game code when joining.

2048
Use the arrow keys to slide tiles. Matching values merge; combine tiles to reach 2048. Use new Game to restart, and choose a board size from 3x3 to 7x7 using the size selector.

Tetris
Use Left/Right to move a piece, Down to drop it faster, and Up to rotate it. Clear rows to score points. The on-screen restart and return buttons are available with the mouse.

Pac-Man
Use the arrow keys to move through the maze, eat dots, and avoid the ghosts.

RUNNING THE APP

Launch the Main class from your Java IDE or launch the main jar file(You need a JRE for this to work).
The game selection screen opens first; click a game to start.
Return to the selection screen using each game's return control.

The project is configured as an IntelliJ IDEA Java module. Make sure the resources directory is included on the runtime classpath so the games can load their images and sounds.

TECHNOLOGY

The desktop interface and game screens are built with Java Swing. Swing panels handle the menus, rendering, and mouse input; keyboard listeners and Swing timers drive gameplay.
The project also includes image and sound resources used by several games.
