package Snake;

import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private final ArrayList<Rectangle> snakeList = new ArrayList<>();
    private boolean growNextTick = false;
    private directions NextDirection = GameOptions.startingDirection;
    directions CurrentDirection = GameOptions.startingDirection;
    private boolean restart = false;

    public Snake() {
        spawnFirstSnakeElements();
    }

    private void spawnFirstSnakeElements() {
        for (int i = 0; i < GameOptions.snakeLengthAtStart; i++) {
            snakeList.add(new Rectangle(GameOptions.originX + i * GameOptions.tileSize, GameOptions.originY,GameOptions.tileSize,GameOptions.tileSize));
        }
    }

    public void growSnake() {
        growNextTick = true;
    }

    public void isSnakeIntersectingItself() {
        Rectangle last = snakeList.getLast();
        for (int i = 0; i < snakeList.size() - 1; i++) {
            if (snakeList.get(i).intersects(last))
                restart = true;
        }
    }

    public void moveSnake() {
        switch (NextDirection) {
            case directions.UP:
                snakeList.add(new Rectangle((int) snakeList.getLast().getX(), (int) snakeList.getLast().getY() - GameOptions.tileSize,GameOptions.tileSize,GameOptions.tileSize));
                break;
            case directions.DOWN:
                snakeList.add(new Rectangle((int) snakeList.getLast().getX(), (int) snakeList.getLast().getY() + GameOptions.tileSize,GameOptions.tileSize,GameOptions.tileSize));
                break;
            case directions.LEFT:
                snakeList.add(new Rectangle((int) snakeList.getLast().getX() - GameOptions.tileSize, (int) snakeList.getLast().getY(), GameOptions.tileSize,GameOptions.tileSize));
                break;
            case directions.RIGHT:
                snakeList.add(new Rectangle((int) snakeList.getLast().getX() + GameOptions.tileSize, (int) snakeList.getLast().getY(), GameOptions.tileSize,GameOptions.tileSize));
        }
        if (!new Rectangle(GameOptions.WIDTH,GameOptions.HEIGHT).contains(snakeList.getLast()))
            restart = true;

        if (!growNextTick)
            snakeList.remove(snakeList.getFirst());
        else growNextTick = false;

        isSnakeIntersectingItself();
        CurrentDirection = NextDirection;
    }

    public void reset() {
        snakeList.clear();
        spawnFirstSnakeElements();
        NextDirection = GameOptions.startingDirection;
        CurrentDirection = GameOptions.startingDirection;
    }

    public boolean isRestart() {
        return restart;
    }
    public ArrayList<Rectangle> getSnakeList() {
        return snakeList;
    }

    public void setNextDirection(directions directions) {
        NextDirection = directions;
    }

    public directions getCurrentDirection() {
        return CurrentDirection;
    }

    public void setRestart(boolean b) {
        restart = b;
    }
}
