package Snake;

import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private final ArrayList<Rectangle> snakeList = new ArrayList<>();
    directions CurrentDirection = GameOptions.startingDirection;
    private directions NextDirection = GameOptions.startingDirection;
    private boolean growNextTick = false;
    private boolean directionChanged = false;
    private boolean gameOver = false;

    public Snake() {
        spawnFirstSnakeElements();
    }

    private void spawnFirstSnakeElements() {
        snakeList.clear();
        for (int i = 0; i < GameOptions.snakeLengthAtStart; i++) {
            snakeList.add(new Rectangle(GameOptions.originX + i * GameOptions.tileSize, GameOptions.originY,
                    GameOptions.tileSize, GameOptions.tileSize));
        }
    }

    public void growSnake() {
        growNextTick = true;
    }

    public void moveSnake() {
        if (gameOver || snakeList.isEmpty()) return;
        Rectangle head = snakeList.getLast();
        int nextX = head.x;
        int nextY = head.y;
        switch (NextDirection) {
            case UP -> nextY -= GameOptions.tileSize;
            case DOWN -> nextY += GameOptions.tileSize;
            case LEFT -> nextX -= GameOptions.tileSize;
            case RIGHT -> nextX += GameOptions.tileSize;
        }

        Rectangle nextHead = new Rectangle(nextX, nextY, GameOptions.tileSize, GameOptions.tileSize);
        int minimumX = GameOptions.playfieldMargin;
        int minimumY = GameOptions.playfieldMargin;
        int maximumX = GameOptions.WIDTH - GameOptions.playfieldMargin;
        int maximumY = GameOptions.HEIGHT - GameOptions.playfieldMargin;
        if (nextX < minimumX || nextY < minimumY || nextX + GameOptions.tileSize > maximumX
                || nextY + GameOptions.tileSize > maximumY) {
            gameOver = true;
            return;
        }

        int bodyEnd = growNextTick ? snakeList.size() : snakeList.size() - 1;
        for (int i = 0; i < bodyEnd; i++) {
            if (nextHead.intersects(snakeList.get(i))) {
                gameOver = true;
                return;
            }
        }

        snakeList.add(nextHead);
        if (!growNextTick) snakeList.removeFirst();
        growNextTick = false;
        CurrentDirection = NextDirection;
        directionChanged = false;
    }

    public void reset() {
        spawnFirstSnakeElements();
        CurrentDirection = GameOptions.startingDirection;
        NextDirection = GameOptions.startingDirection;
        growNextTick = false;
        directionChanged = false;
        gameOver = false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public ArrayList<Rectangle> getSnakeList() {
        return snakeList;
    }

    public void setNextDirection(directions direction) {
        if (direction == null || directionChanged || isOpposite(CurrentDirection, direction)) return;
        NextDirection = direction;
        directionChanged = true;
    }

    private boolean isOpposite(directions current, directions requested) {
        return current == directions.UP && requested == directions.DOWN
                || current == directions.DOWN && requested == directions.UP
                || current == directions.LEFT && requested == directions.RIGHT
                || current == directions.RIGHT && requested == directions.LEFT;
    }
}
