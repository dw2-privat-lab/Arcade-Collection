package Snake;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class Snake_mainPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    Snake snake = new Snake();
    Apple apples = new Apple();
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

    public Snake_mainPanel(ActionListener a) {
        listenerList.add(ActionListener.class, a);

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.setPreferredSize(new Dimension(GameOptions.WIDTH, GameOptions.HEIGHT));

        Timer timer;
        timer = new Timer(GameOptions.gameSpeed, _ -> {
            if (!paused) {
                tick();
            }
            repaint();

        });
        timer.start();
    }

    public void tick() {
        snake.moveSnake();
        int appleNumber = apples.getTouchingApple(snake.getSnakeList().getLast());
        if (appleNumber >= 0) {
            apples.getApples().remove(appleNumber);
            apples.addApple(snake.getSnakeList());
            score++;
            snake.growSnake();
        }
        if (snake.isGameOver()) {
            paused = true;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(GameOptions.backgroundColor);//paint Background
        g.fillRect(0, 0, GameOptions.WIDTH, GameOptions.HEIGHT);

        g.setColor(Color.RED);
        for (Rectangle apple : apples.getApples()) {
            g.drawOval((int) apple.getX(), (int) apple.getY(), (int) apple.getWidth(), (int) apple.getHeight());
        }

        g.setColor(GameOptions.snakeBaseColor);
        for (Rectangle snakeElement : snake.getSnakeList()) {
            if (snakeElement == snake.getSnakeList().getLast()) {
                g.setColor(GameOptions.snakeHeadColor);
            }
            g.fillRect(snakeElement.x, snakeElement.y, (int) snakeElement.getWidth(), (int) snakeElement.getHeight());
        }

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 30);

        if (paused || snake.isGameOver()) {

            g.setColor(new Color(46, 46, 46, 102));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.drawImage((snake.isGameOver() ? gameOver : pausedImg), getWidth() / 2 - ((snake.isGameOver() ? 61 : 43) * scale) / 2, 99, (snake.isGameOver() ? 61 : 43) * scale, 11 * scale, null);
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
        paused =false;
        snake.setGameOver(false);
        score = 0;
        apples=new Apple();
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
                if (snake.isGameOver()) {
                    reset();
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
                if (snake.isGameOver())
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

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "return"));
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
