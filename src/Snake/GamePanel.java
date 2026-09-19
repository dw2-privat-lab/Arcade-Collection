package Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    Snake snake = new Snake();
    Apple apple = new Apple();
    private int score = 0;
    private boolean paused = false;

    public GamePanel() {
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setVisible(true);
        this.setPreferredSize(new Dimension(GameOptions.WIDTH, GameOptions.HEIGHT));

        Timer timer;
        timer = new Timer(GameOptions.gameSpeed, _ -> {
            if (!paused) {
                tick();
            } else {
                repaint();
            }
        });
        timer.start();
    }

    public void tick() {
        snake.moveSnake();
        int appleNumber = apple.getTouchingApple(snake.getSnakeList().getLast());
        if (appleNumber >= 0) {
            apple.getApples().remove(appleNumber);
            apple.addApplePoint(snake.getSnakeList());
            score++;
            snake.growSnake();
        }
        if (snake.isRestart()) {
            paused = true;
            score=0;
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);//clear everything

        g.setColor(GameOptions.backgroundColor);//paint Background
        g.fillRect(0, 0, GameOptions.WIDTH, GameOptions.HEIGHT);

        for (Rectangle applePoint : apple.getApples()) {
            g.setColor(Color.RED);
            g.drawOval((int) applePoint.getX(), (int) applePoint.getY(), (int) applePoint.getWidth(), (int) applePoint.getHeight());
        }

        for (Rectangle snakeElement : snake.getSnakeList()) {//Paint Snake.Snake
            if (snakeElement == snake.getSnakeList().getLast()) {
                g.setColor(GameOptions.snakeHeadColor);
            } else {
                g.setColor(GameOptions.snakeBaseColor);
            }
            g.fillRect(snakeElement.x, snakeElement.y, (int) snakeElement.getWidth(), (int) snakeElement.getHeight());
        }

        g.setColor(Color.black);//Paint Score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 30);

        if (paused || snake.isRestart()) { //Paused or Restart Overlay
            g.setColor(GameOptions.pausedOverlayColor);
            g.fillRect(0, 0, GameOptions.WIDTH, GameOptions.HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 200));

            if (snake.isRestart()) {
                g.drawString("You died", (GameOptions.WIDTH / 2) - (g.getFontMetrics().stringWidth("You Died") / 2), (GameOptions.HEIGHT / 2) - ((int) g.getFont().getSize2D() / 2));
            } else {
                g.drawString("Paused", (GameOptions.WIDTH / 2) - (g.getFontMetrics().stringWidth("Paused") / 2), (GameOptions.HEIGHT / 2) - ((int) g.getFont().getSize2D() / 2));
            }

        }
    }
    public void reset(){
        snake.reset();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (snake.getCurrentDirection() != directions.DOWN) {
                    snake.setNextDirection(directions.UP);
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (snake.getCurrentDirection() != directions.UP) {
                    snake.setNextDirection(directions.DOWN);
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (snake.getCurrentDirection() != directions.RIGHT) {
                    snake.setNextDirection(directions.LEFT);
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (snake.getCurrentDirection() != directions.LEFT) {
                    snake.setNextDirection(directions.RIGHT);
                }
                break;
            case KeyEvent.VK_SPACE:
                paused = !paused;
                if (snake.isRestart()) {
                    snake.reset();
                    snake.setRestart(false);
                    paused = false;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
