package Tetris;

import commonTools.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetrisGame implements Runnable {
    private static final Path SAVE_FILE = Path.of("highscores.json");
    private final int[][] gameField;
    private final Point center = new Point();
    private final Timer gameloop = new Timer(799, e -> {
        move(directions.DOWN);
    });
    TetrisMainPanel mainPanel;
    private int currentPieceNumber = 1;
    private int level = 1;
    private int clearedLines;
    private boolean gameOver = false;
    private boolean lockUserInput = false;
    private int nextPieceNumber;
    private int nextPieceColor;
    private int score = 0;
    private int rotation = 0;
    private int[][] currentPiece;
    private int highscore = 0;

    public TetrisGame(TetrisMainPanel mainPanel) {
        this.mainPanel = mainPanel;
        gameField = new int[20][10];
        loadHighscore();
        resetGame();
    }

    public void run() {
        gameloop.start();
    }

    public void resetGame() {
        gameloop.setDelay(799);
        level = 1;
        score = 0;
        gameOver = false;
        for (int i = 0; i < gameField.length; i++)
            for (int j = 0; j < gameField[0].length; j++) gameField[i][j] = 0;

        nextPieceNumber = 1 + (int) (Math.random() * 7);
        nextPieceColor = 1 + (int) (Math.random() * 7);
        spawnPiece();
    }

    public void unpause() {
        gameloop.start();
    }

    public void pauseGame() {
        gameloop.stop();
    }


    private void loadHighscore() {
        try {
            if (!Files.exists(SAVE_FILE)) return;

            String content = Files.readString(SAVE_FILE);
            Matcher categoryMatcher = Pattern.compile("\"Tetris\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL).matcher(content);

            if (categoryMatcher.find()) {
                String data = categoryMatcher.group(1);

                Matcher scoreMatcher = Pattern.compile("\"highscore\"\\s*:\\s*(\\d+)").matcher(data);

                if (scoreMatcher.find()) {
                    highscore = Integer.parseInt(scoreMatcher.group(1));
                }
            }

        } catch (IOException e) {
        }
    }

    public void spawnPiece() {
        switch (nextPieceNumber) {
            case 1 ->
                    currentPiece = new int[][]{{-nextPieceColor, -nextPieceColor}, {-nextPieceColor, -nextPieceColor}};
            case 2 ->
                    currentPiece = new int[][]{{-nextPieceColor}, {-nextPieceColor}, {-nextPieceColor}, {-nextPieceColor}};
            case 3 ->
                    currentPiece = new int[][]{{-nextPieceColor, -nextPieceColor, 0}, {0, -nextPieceColor, -nextPieceColor}};
            case 4 ->
                    currentPiece = new int[][]{{0, -nextPieceColor, -nextPieceColor}, {-nextPieceColor, -nextPieceColor, 0}};
            case 5 ->
                    currentPiece = new int[][]{{-nextPieceColor, 0}, {-nextPieceColor, 0}, {-nextPieceColor, -nextPieceColor}};
            case 6 ->
                    currentPiece = new int[][]{{0, -nextPieceColor}, {0, -nextPieceColor}, {-nextPieceColor, -nextPieceColor}};
            case 7 ->
                    currentPiece = new int[][]{{0, -nextPieceColor, 0}, {-nextPieceColor, -nextPieceColor, -nextPieceColor}};
            default -> currentPiece = new int[][]{{-nextPieceColor}};
        }
        currentPieceNumber = nextPieceNumber;
        rotation = 0;

        center.setLocation(gameField[0].length / 2 - (currentPiece[0].length + 1) / 2, 0);
        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {
                if (currentPiece[r][c] < 0) {
                    int targetY = center.y + r;
                    int targetX = center.x + c;
                    if (targetY >= 0 && targetY < gameField.length && targetX >= 0 && targetX < gameField[0].length) {
                        if (gameField[targetY][targetX] > 0) {
                            gameOver = true;
                            gameloop.stop();
                            System.out.println("Game Over");
                            return;
                        }
                        gameField[targetY][targetX] = currentPiece[r][c];
                    }
                }
            }
        }

        nextPieceNumber = 1 + (int) (Math.random() * 7);
        nextPieceColor = 1 + (int) (Math.random() * 7);
    }


    public void move(directions direction) {
        if (gameOver) return;
        if (!canMove(direction)) {
            if (direction == directions.DOWN) {
                int temp = 0;
                ArrayList<Integer> linesToClear = new ArrayList<>();
                for (int i = 0; i < gameField.length; i++) {
                    if (canClearCurrentLine(i)) {
                        linesToClear.add(i);
                        temp++;
                    }
                }
                switch (temp) {
                    case 1 -> score += 40 * level;
                    case 2 -> score += 100 * level;
                    case 3 -> score += 300 * level;
                    case 4 -> score += 1200 * level;
                }
                for (int line : linesToClear) {
                    clearLine(line);
                }
                if (temp != 0)
                    SoundPlayer.playSound("resources/Sounds/tetris_ClearLine.wav");
                else
                    SoundPlayer.playSound("resources/Sounds/tetris_landPiece.wav");
                if (score > highscore) {
                    highscore = score;
                    saveHighscore();
                }
                clearedLines += temp;
                if (clearedLines >= 10) {
                    clearedLines -= 10;
                    level++;
                    setNewSpeed();
                }
                stopPlayer();
                spawnPiece();
            }
            return;
        }

        int dirX = 0;
        int dirY = 0;
        switch (direction) {
            case LEFT -> dirX = -1;
            case RIGHT -> dirX = 1;
            case DOWN -> dirY = 1;
            default -> {
                return;
            }
        }

        clearCurrentPiece();

        center.x += dirX;
        center.y += dirY;
        placeCurrentPiece();
        if (direction != directions.DOWN)
            SoundPlayer.playSound("resources/Sounds/tetris_movePiece.wav");
    }

    private void setNewSpeed() {
        switch (level) {
            case 1 -> gameloop.setDelay(799);
            case 2 -> gameloop.setDelay(715);
            case 3 -> gameloop.setDelay(632);
            case 4 -> gameloop.setDelay(549);
            case 5 -> gameloop.setDelay(466);
            case 6 -> gameloop.setDelay(383);
            case 7 -> gameloop.setDelay(300);
            case 8 -> gameloop.setDelay(216);
            case 9 -> gameloop.setDelay(100);
            case 10 -> gameloop.setDelay(83);
            case 11, 12, 13 -> gameloop.setDelay(67);
            case 14, 15, 16 -> gameloop.setDelay(50);
            case 17, 18, 19 -> gameloop.setDelay(33);
            default -> gameloop.setDelay(17);
        }
    }

    public void moveDown() {
        if (gameOver) return;

        lockUserInput = true;

        Timer downTimer = new Timer(33, null);
        downTimer.addActionListener(e -> {
            if (canMove(directions.DOWN)) {
                move(directions.DOWN);
            } else {
                downTimer.stop();

                move(directions.DOWN);
                lockUserInput = false;
                gameloop.start();
            }
        });

        gameloop.stop();
        downTimer.start();
    }

    private boolean canClearCurrentLine(int line) {
        for (int i = 0; i < gameField[line].length; i++) {
            if (gameField[line][i] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearLine(int line) {

        for (int i = line; i > 0; i--) {
            System.arraycopy(gameField[i - 1], 0, gameField[i], 0, gameField[i].length);
        }
        gameField[0] = new int[gameField[0].length];
    }

    private boolean canMove(directions direction) {
        int dirX = 0;
        int dirY = 0;
        switch (direction) {
            case LEFT -> dirX = -1;
            case RIGHT -> dirX = 1;
            case DOWN -> dirY = 1;
            default -> {
                return false;
            }
        }

        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {
                if (currentPiece[r][c] < 0) {
                    int targetY = center.y + r + dirY;
                    int targetX = center.x + c + dirX;

                    if (targetY < 0 || targetY >= gameField.length || targetX < 0 || targetX >= gameField[0].length) {
                        return false;
                    }

                    if (gameField[targetY][targetX] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void clearCurrentPiece() {
        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {
                if (currentPiece[r][c] < 0) {
                    int targetY = center.y + r;
                    int targetX = center.x + c;
                    if (targetY >= 0 && targetY < gameField.length && targetX >= 0 && targetX < gameField[0].length) {
                        if (gameField[targetY][targetX] < 0) {
                            gameField[targetY][targetX] = 0;
                        }
                    }
                }
            }
        }
    }

    private void placeCurrentPiece() {
        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {
                if (currentPiece[r][c] < 0) {
                    int targetY = center.y + r;
                    int targetX = center.x + c;
                    if (targetY >= 0 && targetY < gameField.length && targetX >= 0 && targetX < gameField[0].length) {
                        gameField[targetY][targetX] = currentPiece[r][c];
                    }
                }
            }
        }
    }

    public void saveHighscore() {
        try {
            String content = Files.readString(SAVE_FILE);

            String replacement = "\"Tetris\": {\n    \"highscore\": " + highscore + "\n  }";

            Pattern pattern = Pattern.compile("\"Tetris\"\\s*:\\s*\\{.*?\\}", Pattern.DOTALL);

            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                content = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
            } else {
                content = content.replaceFirst("\\}\\s*$", Matcher.quoteReplacement(",\n  " + replacement + "\n}"));
            }

            Files.writeString(SAVE_FILE, content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayer() {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int targetY = center.y + r;
                int targetX = center.x + c;
                if (targetY >= 0 && targetY < gameField.length && targetX >= 0 && targetX < gameField[0].length) {
                    if (gameField[targetY][targetX] < 0) {
                        gameField[targetY][targetX] = Math.abs(gameField[targetY][targetX]);
                    }
                }

            }
        }
    }

    public void rotate() {
        if (gameOver) return;

        int oldRotation = rotation;
        int oldX = center.x;
        int oldY = center.y;
        rotation = (rotation + 1) % 4;

        boolean success = false;
        if (canRotate()) {
            success = true;
        } else {
            center.x--;
            if (canRotate()) {
                success = true;
            } else {
                center.x += 2;
                if (canRotate()) {
                    success = true;
                }
            }
        }

        if (success) {
            doRotation();
            SoundPlayer.playSound("resources/Sounds/tetris_rotatePiece.wav");
        } else {
            rotation = oldRotation;
            center.x = oldX;
            center.y = oldY;
        }
    }

    private void doRotation() {
        getRotationOffsets(center);
        int rows = currentPiece.length;
        int cols = currentPiece[0].length;
        int[][] rotated = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = currentPiece[r][c];
            }
        }

        for (int r = 0; r < rotated.length; r++) {
            for (int c = 0; c < rotated[r].length; c++) {
                if (rotated[r][c] < 0) {
                    int targetY = center.y + r;
                    int targetX = center.x + c;

                    if (targetY < 0 || targetY >= gameField.length || targetX < 0 || targetX >= gameField[0].length) {
                        return;
                    }

                    if (gameField[targetY][targetX] > 0) {
                        return;
                    }
                }
            }
        }
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[0].length; j++) {
                if (gameField[i][j] < 0) {
                    gameField[i][j] = 0;
                }
            }
        }
        currentPiece = rotated;
        placeCurrentPiece();
    }

    private Point getRotationOffsets(Point original) {
        switch (currentPieceNumber) {
            case 2 -> {
                switch (rotation) {
                    case 1 -> {
                        original.x -= 1;
                        original.y += 2;
                    }
                    case 2 -> {
                        original.x += 1;
                        original.y -= 1;
                    }
                    case 3 -> {
                        original.x -= 2;
                        original.y += 1;
                    }
                    case 0 -> {
                        original.x += 2;
                        original.y -= 2;
                    }
                }
            }
            case 3, 4, 7 -> {
                switch (rotation) {
                    case 1 -> original.x += 1;
                    case 2 -> {
                        original.x -= 1;
                        original.y += 1;
                    }
                    case 3 -> original.y -= 1;
                }
            }
            case 5 -> {
                switch (rotation) {
                    case 1 -> {
                        original.x -= 1;
                        original.y += 1;
                    }
                    case 2 -> original.y -= 1;
                    case 0 -> original.x += 1;
                }
            }
            case 6 -> {
                switch (rotation) {
                    case 2 -> original.x += 1;
                    case 3 -> {
                        original.x -= 1;
                        original.y += 1;
                    }
                    case 0 -> original.y -= 1;
                }
            }
        }
        return original;
    }

    private boolean canRotate() {
        Point tempCenter = getRotationOffsets(new Point(center.x, center.y));

        int rows = currentPiece.length;
        int cols = currentPiece[0].length;
        int[][] rotated = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = currentPiece[r][c];
            }
        }

        for (int r = 0; r < rotated.length; r++) {
            for (int c = 0; c < rotated[r].length; c++) {
                if (rotated[r][c] < 0) {
                    int targetY = tempCenter.y + r;
                    int targetX = tempCenter.x + c;

                    if (targetY < 0 || targetY >= gameField.length || targetX < 0 || targetX >= gameField[0].length) {
                        return false;
                    }
                    if (gameField[targetY][targetX] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getNextPieceNumber() {
        return nextPieceNumber;
    }

    public int getNextPieceColor() {
        return nextPieceColor;
    }

    public int[][] getGameField() {
        return gameField;
    }

    public boolean isLockUserInput() {
        return lockUserInput;
    }

    public int getScore() {
        return score;
    }

    public int getHighscore() {
        return highscore;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}