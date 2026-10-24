package PacMan;

public class PacMan_player {
    private final float stepSize;
    float x;
    float y;
    float x2;
    float y2;
    Directions current_direction = Directions.LEFT;
    Directions next_direction = Directions.LEFT;
    private final Board board;
    int lives = 3;

    public PacMan_player(float x, float y, float stepSize, Board board) {
        this.x = x;
        this.y = y;
        this.stepSize = stepSize;
        this.board = board;
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

        // The maze's tunnel connects the two edges only on rows with open tunnel tiles.
        int tileY = Math.round(y);
        if (x < 0 && tileY >= 0 && tileY < board.getBoard().length
                && board.getBoard()[tileY][0] >= 0 && board.getBoard()[tileY][27] >= 0) {
            x = 27;
        } else if (x > 27 && tileY >= 0 && tileY < board.getBoard().length
                && board.getBoard()[tileY][0] >= 0 && board.getBoard()[tileY][27] >= 0) {
            x = 0;
        }

        updateBounds();
        int tileX = Math.round(x);
        tileY = Math.round(y);
        int[][] tiles = board.getBoard();
        if (tileY >= 0 && tileY < tiles.length && tileX >= 0 && tileX < tiles[0].length
                && Math.abs(x - tileX) < stepSize && Math.abs(y - tileY) < stepSize
                && tiles[tileY][tileX] > 0) {
            if (tiles[tileY][tileX] > 1) {
                board.setFrightened();
            }
            board.setTile(tileX, tileY, 0);
        }
    }

    private void updateBounds() {
        x2 = x + 1.0f - stepSize;
        y2 = y + 1.0f - stepSize;
    }

    private boolean canMove(Directions direction) {
        int[][] gameField = board.getBoard();
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

        // Crossing the edge is allowed only along an actual tunnel row.
        int tileY = Math.round(y);
        boolean tunnelRow = tileY >= 0 && tileY < gameField.length
                && gameField[tileY][0] >= 0 && gameField[tileY][27] >= 0;
        if (tunnelRow && direction == Directions.LEFT && x <= 0.0f) return true;
        if (tunnelRow && direction == Directions.RIGHT && x >= 27.0f) return true;

        int gridX1 = (int) Math.floor(targetX1);
        int gridY1 = (int) Math.floor(targetY1);
        int gridX2 = (int) Math.floor(targetX2);
        int gridY2 = (int) Math.floor(targetY2);

        return isOpen(gameField, gridX1, gridY1)
                && isOpen(gameField, gridX2, gridY1)
                && isOpen(gameField, gridX1, gridY2)
                && isOpen(gameField, gridX2, gridY2);
    }

    private boolean isOpen(int[][] gameField, int x, int y) {
        return y >= 0 && y < gameField.length && x >= 0 && x < gameField[0].length
                && gameField[y][x] >= 0;
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
