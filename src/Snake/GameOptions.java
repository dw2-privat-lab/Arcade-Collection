package Snake;

import java.awt.*;

public class GameOptions {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int snakeLengthAtStart = 4;
    public static final int tileSize = 20;
    public static final int originX = 80;
    public static final int originY = 80;
    public static final int gameSpeed = 100; //time for every tick
    public static final Color snakeBaseColor = new Color(17, 151, 139);
    public static Color snakeHeadColor = new Color(250, 147, 0);
    public static Color backgroundColor = new Color(57, 57, 57);
    public static Color pausedOverlayColor = new Color(255, 255, 255, 110);
    public static directions startingDirection = directions.RIGHT;
    public static int amountApples = 5;
}
