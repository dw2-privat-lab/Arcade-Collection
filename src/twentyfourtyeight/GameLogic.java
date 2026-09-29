package twentyfourtyeight;

import SpaceInvaders.directions;

import java.util.ArrayList;

public class GameLogic {
    private int[][] gameField = new int[4][4];
    private boolean gameOver = false;
    public GameLogic(){
        reset();
    }
    private void reset(){
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                gameField[i][j]=0;
        spawnPiece();
        spawnPiece();
    }

    public void move(directions direction) {
        ArrayList<Integer> list = new ArrayList<>();

        switch (direction) {
            case LEFT, RIGHT:
                for (int i = 0; i < 4; i++) {
                    list.clear();

                    for (int j = 0; j < 4; j++) {
                        list.add(gameField[i][j]);
                        gameField[i][j] = 0;
                    }

                    if (direction == directions.RIGHT)
                        java.util.Collections.reverse(list);

                    for (int j = list.size() - 1; j >= 0; j--)
                        if (list.get(j) == 0)
                            list.remove(j);

                    for (int j = 0; j < list.size() - 1; j++) {
                        if (list.get(j).equals(list.get(j + 1))) {
                            list.set(j, list.get(j) * 2);
                            list.remove(j + 1);
                        }
                    }

                    if (direction == directions.RIGHT)
                        java.util.Collections.reverse(list);

                    int offset = direction == directions.RIGHT? 4 - list.size(): 0;
                    for (int j = 0; j < list.size(); j++)
                        gameField[i][j + offset] = list.get(j);
                }
                break;
            case UP, DOWN:
                for (int j = 0; j < 4; j++) {
                    list.clear();

                    for (int i = 0; i < 4; i++) {
                        list.add(gameField[i][j]);
                        gameField[i][j] = 0;
                    }

                    if (direction == directions.DOWN)
                        java.util.Collections.reverse(list);

                    for (int i = list.size() - 1; i >= 0; i--)
                        if (list.get(i) == 0)
                            list.remove(i);

                    for (int i = 0; i < list.size() - 1; i++) {
                        if (list.get(i).equals(list.get(i + 1))) {
                            list.set(i, list.get(i) * 2);
                            list.remove(i + 1);
                        }
                    }

                    if (direction == directions.DOWN)
                        java.util.Collections.reverse(list);

                    int offset = direction == directions.DOWN? 4 - list.size(): 0;
                    for (int i = 0; i < list.size(); i++)
                        gameField[i + offset][j] = list.get(i);
                }
        }
        gameOver();
        spawnPiece();
    }

    private void gameOver(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(gameField[i][j] == 0)
                    return;
            }
        }
        gameOver = true;
    }

    private void spawnPiece(){
        int randomX;
        int randomY;
        do{
            randomX = (int)(Math.random() * 4);
            randomY = (int)(Math.random() * 4);
        }while (gameField[randomX][randomY] != 0);
        gameField[randomX][randomY] = (int) (Math.random()*10)==1?4:2;
    }

    public int[][] getGameField() {
        return gameField;
    }
    public boolean isGameOver() {
        return gameOver;
    }
}
