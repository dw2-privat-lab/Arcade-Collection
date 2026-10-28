package Pong;

import java.awt.*;

public class Paddle extends Rectangle {
    private final int speed = Settings.GameSize / 64;

    public Paddle(int x, int y) {
        super(x, y, Settings.GameSize / 64, Settings.GameSize / 16);
    }

    public void move(direction thisDirection) {
        if (thisDirection == direction.UP) {
            y = Math.max(0, y - speed);
        } else if (thisDirection == direction.DOWN) {
            y = Math.min(Settings.GameSize - height, y + speed);
        }
    }

    public double getCenterY() {
        return y + height / 2.0;
    }
}
