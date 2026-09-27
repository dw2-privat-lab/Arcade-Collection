package connect4;

public class connect4_gameLogic {
    public int[][] gameField = new int[7][6];
    boolean firstPlayerPlaying = true;
    int won = 0;
    public void resetField(){
        won = 0;
        firstPlayerPlaying = true;
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField[0].length; y++) {
                gameField[x][y] = 0;
            }
        }
    }

    public void move(int pos){
        if (won != 0) return;
        if (pos < 0 || pos >= gameField.length) return;
        if(gameField[pos][0] != 0)return;

        int y = gameField[0].length - 1;
        while(gameField[pos][y] != 0){
            y--;
        }

        gameField[pos][y] = firstPlayerPlaying ? 1 : -1;
        checkForWinner(pos, y);
        firstPlayerPlaying = !firstPlayerPlaying;
    }

    private void checkForWinner(int x, int y) {
        int color = gameField[x][y];
        if (color == 0) return;

        int[][] axes = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

        for (int[] axis : axes) {
            int count = 1;
            count += rowScan(x, y, axis[0], axis[1], color);
            count += rowScan(x, y, -axis[0], -axis[1], color);
            if (count >= 4) {
                won = color;
                return;
            }
        }
    }

    private int rowScan(int x, int y, int dX, int dY, int color) {
        int count = 0;
        int curX = x + dX;
        int curY = y + dY;

        while (curX >= 0 && curX < gameField.length&& curY >= 0 && curY < gameField[0].length&& gameField[curX][curY] == color) {
            count++;
            curX += dX;
            curY += dY;
        }
        return count;
    }
}

