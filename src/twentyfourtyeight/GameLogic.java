package twentyfourtyeight;

import SpaceInvaders.directions;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameLogic {
    private int customSize = 7;
    private int[][] gameField = new int[customSize][customSize];
    private boolean gameOver = false;
    private int score = 0;
    private final Map<Integer, Integer> highscores = new HashMap<>();
    private static final Path SAVE_FILE = Path.of("highscores.json");
    private boolean renderWin=false;
    private boolean got2048 = false;
    private ArrayList<TileAnimation> currentAnimations = new ArrayList<>();

    public GameLogic(){
        loadHighscores();
        reset();
    }
    public void reset(){
        renderWin=false;
        got2048=false;
        gameOver=false;
        gameField = new int[customSize][customSize];
        spawnPiece();
        spawnPiece();
        score = 0;
    }

    private void loadHighscores() {
        if (!Files.exists(SAVE_FILE)) return;

        try {
            String content = Files.readString(SAVE_FILE);
            Matcher matcher = Pattern.compile("\"(\\d+)\"\\s*:\\s*(\\d+)").matcher(content);
            while (matcher.find()) {
                int size = Integer.parseInt(matcher.group(1));
                int highscore = Integer.parseInt(matcher.group(2));
                highscores.put(size, highscore);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCustomSize(int size){
        if(size!=customSize) {
            customSize = size;
            saveHighscores();
            reset();
        }
    }

    private static class Element {
        int value;
        int originalIndex;
        Element(int v, int idx) { this.value = v; this.originalIndex = idx; }
    }

    private int[][] tryMove(directions direction,boolean createAnimation) {
        if(createAnimation)
            currentAnimations.clear();
        int[][] tempfield = new int[customSize][customSize];
        ArrayList<Element> list = new ArrayList<>();

        switch (direction) {
            case LEFT, RIGHT:
                for (int i = 0; i < customSize; i++) {
                    list.clear();
                    for (int j = 0; j < customSize; j++) {
                        if (gameField[i][j] != 0) {
                            list.add(new Element(gameField[i][j], j));
                        }
                    }

                    if (direction == directions.RIGHT) {
                        java.util.Collections.reverse(list);
                    }

                    for (int j = 0; j < list.size() - 1; j++) {
                        if (list.get(j).value == list.get(j + 1).value) {
                            list.get(j).value *= 2;
                            if(createAnimation)
                                score += list.get(j).value;
                            if(list.get(j).value == 2048)
                                if(!got2048) {
                                    renderWin = true;
                                    got2048 = true;
                                }
                            list.get(j + 1).value = -list.get(j + 1).value;
                        }
                    }

                    int targetCount = 0;
                    for (Element el : list) {
                        int actualValue = Math.abs(el.value);
                        boolean isMerge = el.value < 0;

                        int effectiveIndex = isMerge ? (targetCount - 1) : targetCount;
                        int targetCol = (direction == directions.LEFT) ? effectiveIndex : (customSize-1 - effectiveIndex);
                        if(createAnimation)
                            currentAnimations.add(new TileAnimation(i, el.originalIndex, i, targetCol, actualValue));

                        if (!isMerge) {
                            tempfield[i][targetCol] = actualValue;
                            targetCount++;
                        }
                    }
                }
                break;

            case UP, DOWN:
                for (int j = 0; j < customSize; j++) {
                    list.clear();
                    for (int i = 0; i < customSize; i++) {
                        if (gameField[i][j] != 0) {
                            list.add(new Element(gameField[i][j], i));
                        }
                    }

                    if (direction == directions.DOWN) {
                        java.util.Collections.reverse(list);
                    }

                    for (int i = 0; i < list.size() - 1; i++) {
                        if (list.get(i).value == list.get(i + 1).value) {
                            list.get(i).value *= 2;
                            if(createAnimation)
                                score += list.get(i).value;
                            if(list.get(i).value == 2048)
                                if(!got2048) {
                                    renderWin = true;
                                    got2048 = true;
                                }
                            list.get(i + 1).value = -list.get(i + 1).value;
                        }
                    }

                    int targetCount = 0;
                    for (Element el : list) {
                        int actualValue = Math.abs(el.value);
                        int targetRow = (direction == directions.UP) ? targetCount : customSize-1 - targetCount;
                        if(createAnimation)
                            currentAnimations.add(new TileAnimation(el.originalIndex, j, targetRow, j, actualValue));

                        if (el.value > 0) {
                            tempfield[targetRow][j] = actualValue;
                            targetCount++;
                        } else {
                            int prevTargetRow = (direction == directions.UP) ? (targetCount - 1) : customSize-1 - (targetCount - 1);
                            if(createAnimation) {
                                currentAnimations.getLast().startRow = el.originalIndex;
                                currentAnimations.getLast().endRow = prevTargetRow;
                            }
                        }
                    }
                }
                break;
        }
        return tempfield;
    }

    public void move(directions direction) {
        int[][] tempfield = tryMove(direction,true);
        if (!java.util.Arrays.deepEquals(tempfield, gameField)) {
            gameField = tempfield;
            spawnPiece();
            if (score > highscores.getOrDefault(customSize,0)) {
                highscores.put(customSize,score);
                saveHighscores();
            }
        }
        gameOver();
    }

    public ArrayList<TileAnimation> getCurrentAnimations() { return currentAnimations; }
    private void gameOver() {
        int[][] tempfield = tryMove(directions.UP,false);
        if (!java.util.Arrays.deepEquals(tempfield, gameField))return;
        tempfield = tryMove(directions.DOWN,false);
        if (!java.util.Arrays.deepEquals(tempfield, gameField))return;
        tempfield = tryMove(directions.LEFT,false);
        if (!java.util.Arrays.deepEquals(tempfield, gameField))return;
        tempfield = tryMove(directions.RIGHT,false);
        if (!java.util.Arrays.deepEquals(tempfield, gameField))return;
        gameOver = true;
        saveHighscores();
    }

    private void spawnPiece() {
        if(gameOver) return;
        int randomX;
        int randomY;
        do{
            randomX = (int)(Math.random() * customSize);
            randomY = (int)(Math.random() * customSize);
        }while (gameField[randomX][randomY] != 0);
        gameField[randomX][randomY] = (int) (Math.random()*10)==1?4:2;

    }
    public void saveHighscores() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : highscores.entrySet()) {
            sb.append(String.format("  \"%d\": %d", entry.getKey(), entry.getValue()));
            if (++count < highscores.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("}");

        try {
            Files.writeString(SAVE_FILE, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getCustomSize(){return customSize;}
    public int[][] getGameField() { return gameField; }
    public int getScore() { return score; }
    public int getHighscore() { return highscores.getOrDefault(customSize, 0); }
    public boolean isGameOver() { return gameOver; }
    public boolean isRenderWin() { return renderWin; }
    public void setRenderWin(boolean renderWin) { this.renderWin = renderWin; }
}
