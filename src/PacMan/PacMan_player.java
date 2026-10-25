package PacMan;

import commonTools.SoundPlayer;

public class PacMan_player {
    private final float stepSize;
    private final float originX;
    private final float originY;
    private final Board board;
    float x;
    float y;
    float x2;
    float y2;
    Directions current_direction = Directions.LEFT;
    Directions next_direction = Directions.LEFT;
    int lives = 3;
    int remaininDots = 244;
    private final int[][] gamefield;
    private boolean firstDot = true;

    public PacMan_player(float x, float y, float stepSize, Board board) {
        originX = x;
        originY = y;
        this.x = x;
        this.y = y;
        this.stepSize = stepSize;
        this.board = board;
        gamefield = board.getBoard();
        updateBounds();
    }

    public void move() {
        if (isAtTileCenter() && canMove(next_direction)) {
            current_direction = next_direction;
        }

        if (canMove(current_direction)) {
            switch (current_direction) {
                case UP -> y -= stepSize;
                case DOWN -> y += stepSize;
                case LEFT -> x -= stepSize;
                case RIGHT -> x += stepSize;
            }
        }

        int tileY = Math.round(y);
        if (x < 0 && tileY >= 0 && tileY < board.getBoard().length && board.getBoard()[tileY][0] >= 0 && board.getBoard()[tileY][27] >= 0) {
            x = 27;
        } else if (x > 27 && tileY >= 0 && tileY < board.getBoard().length && board.getBoard()[tileY][0] >= 0 && board.getBoard()[tileY][27] >= 0) {
            x = 0;
        }

        updateBounds();
        int tileX = Math.round(x);
        tileY = Math.round(y);
        int[][] tiles = board.getBoard();
        if (tileY >= 0 && tileY < tiles.length && tileX >= 0 && tileX < tiles[0].length && Math.abs(x - tileX) < stepSize && Math.abs(y - tileY) < stepSize && tiles[tileY][tileX] > 0) {
            if (tiles[tileY][tileX] > 1) {
                board.setFrightened();
            }
            if (tiles[tileY][tileX] <= 2) {
                SoundPlayer.playSound("resources/Sounds/pacman_eat_dot_" + (firstDot ? 0 : 1) + ".wav");
                firstDot = !firstDot;
                remaininDots--;
                board.addScore(tiles[tileY][tileX] == 1 ? 10 : 50);
                if (remaininDots == 0) {
                    board.wonLevel();
                }
            }
            board.setTile(tileX, tileY, 0);
        }
    }

    public void reset() {
        x = originX;
        y = originY;
        current_direction = Directions.LEFT;
        next_direction = Directions.LEFT;
        updateBounds();
    }

    public void resetForNewGame() {
        reset();
        lives = 3;
        firstDot = true;
        remaininDots = 244;
    }

    private void updateBounds() {
        x2 = x + 1.0f - stepSize;
        y2 = y + 1.0f - stepSize;
    }

    private boolean canMove(Directions direction) {
        float targetX1 = x;
        float targetY1 = y;
        float targetX2 = x2;
        float targetY2 = y2;

        switch (direction) {
            case UP -> {
                targetY1 -= stepSize;
                targetY2 -= stepSize;
            }
            case DOWN -> {
                targetY1 += stepSize;
                targetY2 += stepSize;
            }
            case LEFT -> {
                targetX1 -= stepSize;
                targetX2 -= stepSize;
            }
            case RIGHT -> {
                targetX1 += stepSize;
                targetX2 += stepSize;
            }
        }

        int tileY = Math.round(y);
        boolean tunnelRow = tileY >= 0 && tileY < gamefield.length && gamefield[tileY][0] >= 0 && gamefield[tileY][27] >= 0;
        if (tunnelRow && direction == Directions.LEFT && x <= 0.0f) return true;
        if (tunnelRow && direction == Directions.RIGHT && x >= 27.0f) return true;

        int gridX1 = (int) Math.floor(targetX1);
        int gridY1 = (int) Math.floor(targetY1);
        int gridX2 = (int) Math.floor(targetX2);
        int gridY2 = (int) Math.floor(targetY2);

        return isOpen(gridX1, gridY1)
                && isOpen(gridX2, gridY1)
                && isOpen(gridX1, gridY2)
                && isOpen(gridX2, gridY2);
    }

    private boolean isOpen(int x, int y) {
        return y >= 0 && y < gamefield.length && x >= 0 && x < gamefield[0].length
                && gamefield[y][x] >= 0;
    }

    private boolean isAtTileCenter() {
        return isCentered(x) && isCentered(y);
    }

    private boolean isCentered(float coordinate) {
        float distance = coordinate - (float) Math.floor(coordinate);
        return distance < 0.0001f || 1.0f - distance < 0.0001f;
    }

    public void setNextDirection(Directions direction) {
        this.next_direction = direction;
    }
}
