package SpaceInvaders;

import java.awt.*;
import java.util.ArrayList;

public class SpaceInvaders_Player {
    public final int bulletSize = 12;
    private final int y;
    private final int playerWidth;
    private final int gameWidth;
    private final ArrayList<Rectangle> bullets = new ArrayList<>();
    private int x;

    public SpaceInvaders_Player(int x, int y, int playerWidth, int gameWidth) {
        this.x = x;
        this.y = y;
        this.playerWidth = playerWidth;
        this.gameWidth = gameWidth;
    }

    public void move(boolean Left, boolean Right) {
        x -= Left ? 1 : -1;
        x += Right ? 1 : -1;
        if (x < 0) {
            x = 0;
        }
        if (x > gameWidth - playerWidth) {
            x = gameWidth - playerWidth;
        }
    }

    public void moveBullets() {
        for (Rectangle shot : bullets) {
            shot.y -= 2;
        }
        bullets.removeIf(shot -> shot.y < -5);
    }

    public void shoot() {
        if (bullets.isEmpty())
            bullets.add(new Rectangle(x + playerWidth / 2 - bulletSize / 2, y, bulletSize, bulletSize));
    }

    public void removeShot(Rectangle r) {
        bullets.remove(r);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle[] getBullets() {
        return bullets.toArray(new Rectangle[0]);
    }
}
