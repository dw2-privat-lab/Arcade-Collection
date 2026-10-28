package Snake;

import java.awt.*;

public class GameOptions {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int snakeLengthAtStart = 4;
    public static final int tileSize = 20;
    public static final int playfieldMargin = tileSize;
    public static final int originX = 80;
    public static final int originY = 80;
    public static final int gameSpeed = 100;
    public static final Color snakeBaseColor = new Color(91, 125, 88);
    public static directions startingDirection = directions.RIGHT;
    public static int amountApples = 5;
    public static Color snakeHeadColor = new Color(165, 190, 107);
    public static Color backgroundColor = new Color(12, 15, 17);
    public static Color boardColor = new Color(21, 28, 22);
    public static Color gridColor = new Color(28, 40, 29);
    public static Color frameColor = new Color(82, 101, 74);
    public static Color appleColor = new Color(136, 64, 48);
}
