package chess;

import java.io.Serializable;

public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    int x;
    int y;
    int toX;
    int toY;
    public Move(int x, int y, int toX, int toY) {
        this.x = x;
        this.y = y;
        this.toX = toX;
        this.toY = toY;
    }
}
