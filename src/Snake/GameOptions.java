package Snake;

import java.awt.*;

public class GameOptions {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int snakeLengthAtStart = 4;
    public static final int tileSize = 20;
    public static final int originX = 80;
    public static final int originY = 80;
    public static directions startingDirection = directions.RIGHT;
    public static int amountApples = 5;

    public static final int gameSpeed = 75; //time for every tick
    public static final Color snakeBaseColor = new Color(112, 112, 112);
    public static Color snakeHeadColor = new Color(255, 255, 255);
    public static Color backgroundColor = new Color(0, 0, 0);
}

//here are some other themes:
/*
    //Default theme
    public static final int gameSpeed = 100; //time for every tick
    public static final Color snakeBaseColor = new Color(17, 151, 139);
    public static Color snakeHeadColor = new Color(250, 147, 0);
    public static Color backgroundColor = new Color(57, 57, 57);

 */
/*
    //Arcade Like
    public static int gameSpeed = 65;                              // Fast & frantic arcade tick
    public static Color backgroundColor = new Color(5, 5, 10);      // Deep space void
    public static Color snakeBaseColor = new Color(0, 255, 65);     // Alien Invader Green
    public static Color snakeHeadColor = new Color(255, 235, 59);   // Laser Cannon Yellow
*/
/*
    // Cyberpunk Preset
    public static int gameSpeed = 80;                              // Fast pace
    public static Color backgroundColor = new Color(18, 12, 38);    // Midnight synthwave purple
    public static Color snakeBaseColor = new Color(255, 0, 127);    // Neon magenta
    public static Color snakeHeadColor = new Color(0, 245, 255);    // Electric cyan
*/
/*
    // Game Boy Monochrome Preset
    public static int gameSpeed = 120;                             // Nostalgic moderate pace
    public static Color backgroundColor = new Color(15, 56, 15);  // Olive LCD background
    public static Color snakeBaseColor = new Color(48, 98, 48);     // Dark green pixel body
    public static Color snakeHeadColor = new Color(155, 188, 15);     // Deepest dark green head
*/

