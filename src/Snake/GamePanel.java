package Snake;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    Snake snake = new Snake();
    Apple apple = new Apple();
    private int score = 0;
    private boolean paused = false;
    private final int scale = 4;

    private int highlighted=-1;

    EventListenerList listenerList = new EventListenerList();


    Image gameOver = new ImageIcon("resources/spaceInvaders/gameOver.png").getImage();
    Image pausedImg = new ImageIcon("resources/spaceInvaders/paused.png").getImage();
    Image quitImg = new ImageIcon("resources/spaceInvaders/quit.png").getImage();
    Image resumeImg = new ImageIcon("resources/spaceInvaders/resume.png").getImage();
    Image returnImg = new ImageIcon("resources/spaceInvaders/return.png").getImage();

    public GamePanel(ActionListener a) {
        listenerList.add(ActionListener.class, a);

        this.setFocusable(true);
        this.addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

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

            g.setColor(new Color(46, 46, 46, 102));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage((snake.isRestart() ? gameOver : pausedImg), getWidth() / 2 - ((snake.isRestart() ? 61 : 43) * scale) / 2, 99, (snake.isRestart() ? 61 : 43) * scale, 11 * scale, null);
            if (highlighted == 1)
                g.drawImage(quitImg, (int) ((double) getWidth() / 2 - 16 * scale), 100 + 11 * scale, 11 * scale, 11 * scale, null);
            else
                g.drawImage(quitImg, (int) ((double) getWidth() / 2 - 15 * scale), 100 + 12 * scale, 9 * scale, 9 * scale, null);
            if (highlighted == 2)
                g.drawImage(resumeImg, (int) ((double) getWidth() / 2 - 5.5 * scale), 100 + 11 * scale, 11 * scale, 11 * scale, null);
            else
                g.drawImage(resumeImg, (int) ((double) getWidth() / 2 - 4.5 * scale), 100 + 12 * scale, 9 * scale, 9 * scale, null);
            if (highlighted == 3)
                g.drawImage(returnImg, (int) ((double) getWidth() / 2 + 5 * scale), 100 + 11 * scale, 11 * scale, 11 * scale, null);
            else
                g.drawImage(returnImg, (int) ((double) getWidth() / 2 + 6 * scale), 100 + 12 * scale, 9 * scale, 9 * scale, null);
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
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                if (snake.isRestart()) {
                    snake.reset();
                    snake.setRestart(false);
                    paused = false;
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        highlighted = -1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 15 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 4.5 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 2;
        if (new Rectangle((int) ((double) getWidth() / 2 + 6 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 3;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (new Rectangle((int) ((double) getWidth() / 2 - 15 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
                System.exit(1);
            if (new Rectangle((int) ((double) getWidth() / 2 - 4.5 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint())) {
                if (snake.isRestart())
                    reset();
                else {
                    paused = false;
                }
            }
            highlighted = 2;
            if (new Rectangle((int) ((double) getWidth() / 2 + 6 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint())) {
                fireActionPerformed();
            }
        }
    }

    private void fireActionPerformed() {
        Object[] listeners = listenerList.getListenerList();

        ActionEvent event = null;


        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (event == null) {
                    event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "return");
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
