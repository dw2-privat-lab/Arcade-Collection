package SpaceInvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SpaceInvaders_EnemyController implements ActionListener {
    private final int enemywidth;
    private final int enemyheight;
    private final int spacing;
    private final int yOffset;
    private final int bulletwidth = 8;
    private final int bulletheight = 24;
    ArrayList<SpaceInvaders_Enemy> enemies = new ArrayList<>();
    ArrayList<Rectangle> bullets = new ArrayList<>();
    Timer timer;
    private directions currentdirection = directions.RIGHT;
    private int x = 0;
    private boolean firstFrame = true;

    public SpaceInvaders_EnemyController(int yOffset, int enemywidth, int enemyheight, int spacing) {
        this.enemywidth = enemywidth;
        this.enemyheight = enemyheight;
        this.spacing = spacing;
        this.yOffset = yOffset;

        timer = new Timer(300, this);
        timer.start();
    }

    public void resetEnemies() {
        x = 0;
        firstFrame = true;
        enemies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                enemies.add(new SpaceInvaders_Enemy((spacing + enemywidth) * j + (i == 0 ? enemywidth / 6 : 0), (spacing + enemyheight) * i + yOffset, enemywidth - (i == 0 ? enemywidth / 3 : 0), enemyheight, (i == 0 ? 3 : (i < 3 ? 2 : 1))));
            }
        }
    }

    public void randomShot() {
        int i = (int) (Math.random() * enemies.size());
        SpaceInvaders_Enemy chosen = enemies.get(i);
        bullets.add(new Rectangle(chosen.getX(), chosen.getY(), bulletwidth, bulletheight));
    }

    public void moveEnemies(directions direction) {
        for (SpaceInvaders_Enemy e : enemies) {
            e.move(direction, enemywidth / 12);
        }
        firstFrame = !firstFrame;
    }

    public void moveBullets() {
        for (Rectangle r : bullets) {
            r.setLocation(r.x, r.y + 2);
        }
    }

    public ArrayList<SpaceInvaders_Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public ArrayList<Rectangle> getBullets() {
        return new ArrayList<>(bullets);
    }

    public boolean getFirstFrame() {
        return firstFrame;
    }

    public void removeEnemies(SpaceInvaders_Enemy e) {
        enemies.remove(e);
    }

    public void removeBullet(Rectangle r) {
        bullets.remove(r);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentdirection == directions.RIGHT) {
            x++;
            int max = 52;
            if (x <= max) {
                moveEnemies(directions.RIGHT);
            } else {
                x = max;
                currentdirection = directions.LEFT;
                moveEnemies(directions.DOWN);
            }
        } else if (currentdirection == directions.LEFT) {
            x--;
            if (x >= 0) {
                moveEnemies(directions.LEFT);
            } else {
                x = 0;
                currentdirection = directions.RIGHT;
                moveEnemies(directions.DOWN);
                if (timer.getDelay() >= 75)
                    timer.setDelay(timer.getDelay() - 25);
            }
        }
    }
}
