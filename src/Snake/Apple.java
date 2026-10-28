package Snake;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Apple {
    private final ArrayList<Rectangle> apples = new ArrayList<>();

    public Apple(ArrayList<Rectangle> snakeElements) {
        addApples(snakeElements);
    }

    private void addApples(ArrayList<Rectangle> snakeElements) {
        for (int i = 0; i < GameOptions.amountApples; i++) {
            if (!addApple(snakeElements)) return;
        }
    }

    boolean touches(Rectangle rectangle, List<Rectangle> list) {
        for (Rectangle element : list) {
            if (element.intersects(rectangle)) return true;
        }
        return false;
    }

    int getTouchingApple(Rectangle rectangle) {
        for (int i = 0; i < apples.size(); i++) {
            if (apples.get(i).intersects(rectangle)) return i;
        }
        return -1;
    }

    boolean addApple(List<Rectangle> snakeElements) {
        ArrayList<Rectangle> freeCells = new ArrayList<>();
        int minimumX = GameOptions.playfieldMargin;
        int minimumY = GameOptions.playfieldMargin;
        int maximumX = GameOptions.WIDTH - GameOptions.playfieldMargin;
        int maximumY = GameOptions.HEIGHT - GameOptions.playfieldMargin;
        for (int y = minimumY; y < maximumY; y += GameOptions.tileSize) {
            for (int x = minimumX; x < maximumX; x += GameOptions.tileSize) {
                Rectangle cell = new Rectangle(x, y, GameOptions.tileSize, GameOptions.tileSize);
                if (!touches(cell, apples) && !touches(cell, snakeElements)) freeCells.add(cell);
            }
        }
        if (freeCells.isEmpty()) return false;
        apples.add(freeCells.get(ThreadLocalRandom.current().nextInt(freeCells.size())));
        return true;
    }

    public ArrayList<Rectangle> getApples() {
        return apples;
    }
}
