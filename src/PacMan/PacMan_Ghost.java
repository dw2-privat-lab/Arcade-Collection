package PacMan;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static PacMan.Directions.*;

public class PacMan_Ghost {
    final int type;
    private final float originalStepSize;
    private final float originX;
    private final float originY;
    private final PacMan_player player;
    private final Directions[] tryDirections = {UP, RIGHT, LEFT, DOWN};
    private final Board board;
    private final int[][] gamefield;
    float x;
    float y;
    float x2;
    float y2;
    boolean isFrightend = false;
    Directions current_direction = Directions.LEFT;
    private boolean isScatterMode = true;
    private float stepSize;
    private boolean lastStepWasFrightendSpeed = false;
    private boolean moveBack = false;
    private boolean forceMoveDown = false;
    private boolean forceMoveOut = true;
    private boolean waiting = true;
    private boolean waitingAnimationDown = false;
    private ArrayList<Point> homeRoute = new ArrayList<>();
    private int homeRouteIndex = 1;

    public PacMan_Ghost(float x, float y, float stepSize, int type, Board board) {
        this.x = x;
        this.y = y;
        originX = x;
        originY = y;
        originalStepSize = stepSize;
        this.stepSize = originalStepSize;
        this.board = board;
        player = board.getPlayer();
        this.type = type;
        if (type == 1) waiting = false;
        gamefield = board.getBoard();
    }

    public void reset() {
        forceMoveDown = false;
        forceMoveOut = true;
        moveBack = false;
        homeRoute.clear();
        homeRouteIndex = 1;
        waiting = type != 1;
        waitingAnimationDown = false;
        isFrightend = false;
        lastStepWasFrightendSpeed = false;
        isScatterMode = false;
        current_direction = Directions.LEFT;
        stepSize = originalStepSize;
        x = originX;
        y = originY;
    }

    public void move() {
        if (waiting) {
            stepSize = originalStepSize / 2.0f;
            if (Math.abs(y - originY) >= 0.25f) {
                waitingAnimationDown = !waitingAnimationDown;
            }
            y += waitingAnimationDown ? stepSize : -stepSize;
            return;
        }

        if (forceMoveOut) {
            stepSize = originalStepSize / 2.0f;
            float targetX = 13.5f;
            float targetY = 11.0f;
            if (Math.abs(x - targetX) > 0.0001f) {
                x += x < targetX ? Math.min(stepSize, targetX - x) : -Math.min(stepSize, x - targetX);
            } else if (Math.abs(y - targetY) > 0.0001f) {
                y += y < targetY ? Math.min(stepSize, targetY - y) : -Math.min(stepSize, y - targetY);
            } else {
                x = targetX;
                y = targetY;
                forceMoveOut = false;
            }
            return;
        }

        if (moveBack && Math.abs(x - 13.5f) < 0.0001f && Math.abs(y - 11.0f) < 0.0001f) {
            x = 13.5f;
            y = 11.0f;
            moveBack = false;
            forceMoveDown = true;
        }

        if (forceMoveDown) {
            stepSize = originalStepSize * 2.0f;
            y = Math.min(y + stepSize, 14.0f);
            updateBounds();
            if (Math.abs(y - 14.0f) < 0.0001f) {
                forceMoveOut = true;
                forceMoveDown = false;
                board.releaseGhosts();
                isFrightend = false;
            }
            return;
        }

        boolean frightenedSpeed = isFrightend && !moveBack;
        stepSize = moveBack
                ? originalStepSize * 2.0f
                : frightenedSpeed ? originalStepSize / 2.0f : originalStepSize;

        if (frightenedSpeed != lastStepWasFrightendSpeed) {
            float snappedX = Math.round(x * 2.0f) / 2.0f;
            float snappedY = Math.round(y * 2.0f) / 2.0f;
            float maximumSnapDistance = originalStepSize * 2.0f;
            boolean closeEnough = Math.abs(snappedX - x) <= maximumSnapDistance
                    && Math.abs(snappedY - y) <= maximumSnapDistance;

            if (closeEnough && canOccupy(snappedX, snappedY)) {
                x = snappedX;
                y = snappedY;
                lastStepWasFrightendSpeed = frightenedSpeed;
            }
        }
        updateBounds();

        if (isAtTileCenter()) {
            chooseNextDirection();
        }

        if (!canMove(current_direction)) {
            if (moveBack) {
                if (!isAtTileCenter()) {
                    float alignedX = Math.round(x * 2.0f) / 2.0f;
                    float alignedY = Math.round(y * 2.0f) / 2.0f;
                    if (canOccupy(alignedX, alignedY)) {
                        x = alignedX;
                        y = alignedY;
                        updateBounds();
                    }
                }
                advanceHomeRoute();
                if (!canMove(current_direction)) return;
            } else {
                Directions reverse = opposite(current_direction);
                if (!canMove(reverse)) return;
                current_direction = reverse;
            }
        }

        switch (current_direction) {
            case UP -> y -= stepSize;
            case DOWN -> y += stepSize;
            case LEFT -> x -= stepSize;
            case RIGHT -> x += stepSize;
        }

        int tunnelRow = Math.round(y);
        if (tunnelRow >= 0 && tunnelRow < gamefield.length
                && gamefield[tunnelRow][0] >= 0 && gamefield[tunnelRow][gamefield[0].length - 1] >= 0) {
            if (x < 0.0f) x = gamefield[0].length - 1;
            else if (x > gamefield[0].length - 1) x = 0.0f;
            updateBounds();
        }

        if (collidesWithPlayer()) {
            if (isFrightend && !moveBack) {
                moveBack = true;
                stepSize = originalStepSize * 2.0f;
                float routeStartX = Math.round(x * 2.0f) / 2.0f;
                float routeStartY = Math.round(y * 2.0f) / 2.0f;
                if (canOccupy(routeStartX, routeStartY)) {
                    x = routeStartX;
                    y = routeStartY;
                    updateBounds();
                }
                homeRoute = calculateHomeRoute();
                homeRouteIndex = 1;
                board.addScore(200);
                board.ghostEaten();
                if (!homeRoute.isEmpty() && homeRoute.size() > 1) {
                    current_direction = directionTo(homeRoute.get(0), homeRoute.get(1));
                }
            } else if (!isFrightend && !moveBack) {
                board.killPlayer();
            }
        }
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

        int gridX1 = (int) Math.floor(targetX1);
        int gridY1 = (int) Math.floor(targetY1);
        int gridX2 = (int) Math.floor(targetX2);
        int gridY2 = (int) Math.floor(targetY2);

        int tunnelRow = Math.round(y);
        boolean tunnelRowOpen = tunnelRow >= 0 && tunnelRow < gamefield.length
                && gamefield[tunnelRow][0] >= 0 && gamefield[tunnelRow][gamefield[0].length - 1] >= 0;
        if (tunnelRowOpen && direction == LEFT && x <= 0.0f) return true;
        if (tunnelRowOpen && direction == RIGHT && x >= gamefield[0].length - 1) return true;

        return isOpen(gridX1, gridY1) && isOpen(gridX2, gridY1) && isOpen(gridX1, gridY2) && isOpen(gridX2, gridY2);
    }

    private boolean isOpen(int tileX, int tileY) {
        return tileY >= 0 && tileY < gamefield.length && tileX >= 0 && tileX < gamefield[0].length && gamefield[tileY][tileX] >= 0;
    }

    private void chooseNextDirection() {
        if (moveBack) {
            advanceHomeRoute();
            return;
        }

        if (isFrightend) {
            current_direction = getRandomPossibleDirection();
            return;
        }

        int targetX = Math.round(player.x);
        int targetY = Math.round(player.y);

        if (isScatterMode) {
            switch (type) {
                case 1 -> {
                    targetX = 27;
                    targetY = 0;
                }
                case 2 -> {
                    targetX = 0;
                    targetY = 0;
                }
                case 3 -> {
                    targetX = 27;
                    targetY = 30;
                }
                case 4 -> {
                    targetX = 0;
                    targetY = 30;
                }
                default -> {
                }
            }
        } else {
            double dx;
            double dy;
            Directions playerDir = player.current_direction;
            if (playerDir == null) playerDir = Directions.LEFT;
            switch (type) {
                case 2 -> {
                    switch (playerDir) {
                        case UP -> targetY -= 4;
                        case DOWN -> targetY += 4;
                        case LEFT -> targetX -= 4;
                        case RIGHT -> targetX += 4;
                    }
                }
                case 3 -> {
                    switch (playerDir) {
                        case UP -> targetY -= 2;
                        case DOWN -> targetY += 2;
                        case LEFT -> targetX -= 2;
                        case RIGHT -> targetX += 2;
                    }
                    dx = targetX - board.getBlinkyX();
                    dy = targetY - board.getBlinkyY();
                    targetX = (int) (targetX + dx);
                    targetY = (int) (targetY + dy);
                }
                case 4 -> {
                    dx = targetX - x;
                    dy = targetY - y;
                    if (Math.sqrt(dx * dx + dy * dy) < 8.0) {
                        targetX = 0;
                        targetY = 30;
                    }
                }
                default -> {
                }
            }
        }
        current_direction = getDirectionToFollowPoint(targetX, targetY);
    }

    private Directions getRandomPossibleDirection() {
        ArrayList<Directions> possibleDirections = new ArrayList<>();
        Directions opposite = opposite(current_direction);
        for (Directions direction : tryDirections) {
            if (direction != opposite && canMove(direction)) {
                possibleDirections.add(direction);
            }
        }
        if (possibleDirections.isEmpty()) {
            return canMove(opposite) ? opposite : current_direction;
        }
        return possibleDirections.get((int) (Math.random() * possibleDirections.size()));
    }

    private ArrayList<Point> calculateHomeRoute() {
        final int startX = Math.round(x * 2.0f);
        final int startY = Math.round(y * 2.0f);

        ArrayList<Point> route = new ArrayList<>();
        boolean[][] visited = new boolean[62][56];
        int[][] parentX = new int[62][56];
        int[][] parentY = new int[62][56];
        for (int row = 0; row < 62; row++) {
            java.util.Arrays.fill(parentX[row], -1);
            java.util.Arrays.fill(parentY[row], -1);
        }
        ArrayDeque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(startX, startY));
        visited[startY][startX] = true;

        while (!queue.isEmpty() && !visited[22][27]) {
            Point position = queue.removeFirst();
            for (Directions direction : tryDirections) {
                int nextX = position.x;
                int nextY = position.y;
                switch (direction) {
                    case UP -> nextY--;
                    case DOWN -> nextY++;
                    case LEFT -> nextX--;
                    case RIGHT -> nextX++;
                }
                if (nextX < 0 || nextX >= 56 || nextY < 0 || nextY >= 62 || visited[nextY][nextX]) {
                    continue;
                }
                if (!canOccupy(nextX / 2.0f, nextY / 2.0f)) {
                    continue;
                }
                visited[nextY][nextX] = true;
                parentX[nextY][nextX] = position.x;
                parentY[nextY][nextX] = position.y;
                queue.addLast(new Point(nextX, nextY));
            }
        }

        if (!visited[22][27]) return route;

        int nodeX = 27;
        int nodeY = 22;
        while (nodeX != startX || nodeY != startY) {
            route.addFirst(new Point(nodeX, nodeY));
            int previousX = parentX[nodeY][nodeX];
            int previousY = parentY[nodeY][nodeX];
            nodeX = previousX;
            nodeY = previousY;
        }
        route.addFirst(new Point(startX, startY));
        return route;
    }

    private void advanceHomeRoute() {
        if (homeRoute.isEmpty()) return;
        if (homeRouteIndex < homeRoute.size()) {
            Point nextNode = homeRoute.get(homeRouteIndex);
            if (isAtTileCenter() && Math.abs(x - nextNode.x / 2.0f) < 0.0001f && Math.abs(y - nextNode.y / 2.0f) < 0.0001f) {
                homeRouteIndex++;
            }
        }
        if (homeRouteIndex < homeRoute.size()) {
            current_direction = directionTo(homeRoute.get(homeRouteIndex - 1), homeRoute.get(homeRouteIndex));
        }
    }

    private Directions directionTo(Point from, Point to) {
        if (to.x > from.x) return Directions.RIGHT;
        if (to.x < from.x) return Directions.LEFT;
        if (to.y > from.y) return Directions.DOWN;
        return Directions.UP;
    }

    private boolean canOccupy(float worldX, float worldY) {
        int left = (int) Math.floor(worldX);
        int top = (int) Math.floor(worldY);
        int right = (int) Math.floor(worldX + 1.0f - originalStepSize);
        int bottom = (int) Math.floor(worldY + 1.0f - originalStepSize);
        return isOpen(left, top) && isOpen(right, top) && isOpen(left, bottom) && isOpen(right, bottom);
    }

    private Directions getDirectionToFollowPoint(double targetX, double targetY) {
        ArrayList<Directions> preferredDirections = new ArrayList<>();
        Directions reverse = opposite(current_direction);

        for (Directions direction : tryDirections) {
            if (canMove(direction)) {
                if (direction != reverse) {
                    preferredDirections.add(direction);
                }
            }
        }
        if (preferredDirections.isEmpty()) {
            if (canMove(reverse)) preferredDirections.add(reverse);
            if (preferredDirections.isEmpty()) {
                return current_direction;
            }
        }

        Directions chosen = preferredDirections.getFirst();
        double closestDistance = Double.MAX_VALUE;
        for (Directions direction : preferredDirections) {
            double nextX = x;
            double nextY = y;
            switch (direction) {
                case UP -> nextY -= 1.0;
                case DOWN -> nextY += 1.0;
                case LEFT -> nextX -= 1.0;
                case RIGHT -> nextX += 1.0;
            }
            double dx = nextX - targetX;
            double dy = nextY - targetY;
            double distance = dx * dx + dy * dy;
            if (distance < closestDistance) {
                closestDistance = distance;
                chosen = direction;
            }
        }
        return chosen;
    }

    private boolean isAtTileCenter() {
        return isAligned(x) && isAligned(y);
    }

    private boolean isAligned(float coordinate) {
        float nearestHalfTile = Math.round(coordinate * 2.0f) / 2.0f;
        return Math.abs(coordinate - nearestHalfTile) < 0.0001f;
    }

    private Point getCoordinates(Directions direction) {
        int outputX = Math.round(x);
        int outputY = Math.round(y);
        switch (direction) {
            case UP -> outputY--;
            case DOWN -> outputY++;
            case LEFT -> outputX--;
            case RIGHT -> outputX++;
        }
        return new Point(outputX, outputY);
    }

    private Directions opposite(Directions direction) {
        return switch (direction) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public void setFrightend(boolean frightend) {
        if (this.isFrightend == frightend) return;
        this.isFrightend = frightend;
        if (!moveBack) {
            current_direction = opposite(current_direction);
        }
    }

    public void setScatterMode(boolean scatterMode) {
        if (this.isScatterMode != scatterMode) {
            this.isScatterMode = scatterMode;
            current_direction = opposite(current_direction);
        }
    }

    private boolean collidesWithPlayer() {
        Rectangle playerBounds = new Rectangle((int) (player.x * 20), (int) (player.y * 20), 20, 20);
        Rectangle ghostBounds = new Rectangle((int) (x * 20), (int) (y * 20), 20, 20);
        return ghostBounds.intersects(playerBounds);
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public boolean isDead() {
        return moveBack;
    }
}
