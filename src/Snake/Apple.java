package Snake;

import java.awt.*;
import java.util.ArrayList;

public class Apple {
    private final ArrayList<Rectangle> apples = new ArrayList<>();

    public Apple() {
        ArrayList<Rectangle> temp = new ArrayList<>();
        for (int i = 0; i < GameOptions.snakeLengthAtStart; i++) {
            temp.add(new Rectangle(GameOptions.originX + i * GameOptions.tileSize, GameOptions.originY,GameOptions.tileSize,GameOptions.tileSize));
        }
        for (int i = 0; i < GameOptions.amountApples; i++) {
            addApple(temp);
        }
    }
    boolean touches(Rectangle rectangle,ArrayList<Rectangle> list) {
        for (Rectangle Element : list) {
            if (Element.intersects(rectangle)) {
                return true;
            }
        }
        return false;
    }

    int getTouchingApple(Rectangle rectangle) {
        for (int i = 0; i < apples.size(); i++) {
            if (apples.get(i).intersects(rectangle)) {
                return i;
            }
        }
        return -1;
    }

    void addApple(ArrayList<Rectangle> snakeElements) {
        int randomX;
        int randomY;
        do {
            randomX = (int) (Math.random() * GameOptions.WIDTH) / GameOptions.tileSize * GameOptions.tileSize;
            randomY = (int) (Math.random() * GameOptions.HEIGHT) / GameOptions.tileSize * GameOptions.tileSize;
        } while (touches(new Rectangle(randomX, randomY,GameOptions.tileSize, GameOptions.tileSize),apples)||touches(new Rectangle(randomX, randomY,GameOptions.tileSize, GameOptions.tileSize), snakeElements));

        apples.add(new Rectangle(randomX, randomY,GameOptions.tileSize, GameOptions.tileSize));
    }
    public ArrayList<Rectangle> getApples() {
        return apples;
    }
}
