package Pong;

import commonTools.ImageLoader;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class Pong_mainPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private final Ball ball;
    private final Paddle paddleLeft;
    private final Paddle paddleRight;
    private final Timer gameTimer;
    Image pausedImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/paused.png");
    Image quitImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/quit.png");
    Image resumeImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/resume.png");
    Image returnImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/return.png");
    EventListenerList listenerList = new EventListenerList();
    private int scoreLeft = 0;
    private int scoreRight = 0;
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private int highlighted = -1;
    private boolean paused = false;

    public Pong_mainPanel(ActionListener actionListener) {
        setPreferredSize(new Dimension(Settings.GameSize, Settings.GameSize));
        listenerList.add(ActionListener.class, actionListener);
        ball = new Ball((Settings.GameSize - Settings.BallSize) / 2, (Settings.GameSize - Settings.BallSize) / 2);
        paddleLeft = new Paddle(Settings.GameSize / 16, (Settings.GameSize - Settings.GameSize / 16) / 2);
        paddleRight = new Paddle(Settings.GameSize - Settings.GameSize / 16 - Settings.GameSize / 64, (Settings.GameSize - Settings.GameSize / 16) / 2);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        gameTimer = new Timer(16, _ -> {
            if (!paused) {
                movePaddles();
                ball.move();
                checkBallPadleCollision();
                checkScoring();
                repaint();
            }
        });
        gameTimer.start();
    }

    public void pause() {
        paused = true;
    }

    public void reset() {
        scoreLeft = 0;
        scoreRight = 0;
        ball.reset();
        paddleLeft.setLocation(Settings.GameSize / 16, (Settings.GameSize - paddleLeft.height) / 2);
        paddleRight.setLocation(Settings.GameSize - Settings.GameSize / 16 - paddleRight.width, (Settings.GameSize - paddleRight.height) / 2);
        wPressed = false;
        sPressed = false;
        upPressed = false;
        downPressed = false;
        highlighted = -1;
        paused = false;
        if (!gameTimer.isRunning()) gameTimer.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Settings.GameSize / 20));
        FontMetrics scoreMetrics = g2d.getFontMetrics();
        String leftScore = String.valueOf(scoreLeft);
        String rightScore = String.valueOf(scoreRight);
        int scoreY = Settings.GameSize / 12;
        int leftScoreCenter = Settings.GameSize / 2 - Settings.ScoreOffset;
        int rightScoreCenter = Settings.GameSize / 2 + Settings.ScoreOffset;
        g2d.drawString(leftScore, leftScoreCenter - scoreMetrics.stringWidth(leftScore) / 2, scoreY);
        g2d.drawString(rightScore, rightScoreCenter - scoreMetrics.stringWidth(rightScore) / 2, scoreY);

        int lineWidth = Math.max(2, Settings.LineWidth / 3);
        for (int y = 0; y < Settings.GameSize; y += Settings.LineWidth * 2) {
            g2d.fillRect(Settings.GameSize / 2 - lineWidth / 2, y, lineWidth, Settings.LineWidth);
        }

        g2d.fillRect(paddleLeft.x, paddleLeft.y, paddleLeft.width, paddleLeft.height);
        g2d.fillRect(paddleRight.x, paddleRight.y, paddleRight.width, paddleRight.height);
        g2d.fillRect(ball.x, ball.y, Settings.BallSize, Settings.BallSize);

        if (paused) paintPauseMenu(g2d);
        g2d.dispose();
    }

    private void paintPauseMenu(Graphics2D g) {
        g.setColor(new Color(46, 46, 46, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(pausedImg, (getWidth() - 43 * 5) / 2, 99, 43 * 5, 11 * 5, this);
        if (highlighted == 1)
            g.drawImage(quitImg, getWidth() / 2 - 16 * 5, 155, 55, 55, this);
        else
            g.drawImage(quitImg, getWidth() / 2 - 15 * 5, 160, 45, 45, this);
        if (highlighted == 2)
            g.drawImage(resumeImg, getWidth() / 2 - (int) (5.5 * 5), 155, 55, 55, this);
        else
            g.drawImage(resumeImg, getWidth() / 2 - (int) (4.5 * 5), 160, 45, 45, this);
        if (highlighted == 3)
            g.drawImage(returnImg, getWidth() / 2 + 5 * 5, 155, 55, 55, this);
        else
            g.drawImage(returnImg, getWidth() / 2 + 6 * 5, 160, 45, 45, this);
    }

    private void checkBallPadleCollision() {
        Rectangle ballBounds = ball.getBounds();
        if (ball.getDirection() > 90 && ball.getDirection() < 270 && paddleLeft.intersects(ballBounds)) {
            ball.bounceFromPaddle(paddleLeft, true);
            ball.setExactY(Math.clamp(ball.getExactY(), 0, Settings.GameSize - Settings.BallSize));
        } else if ((ball.getDirection() < 90 || ball.getDirection() > 270) && paddleRight.intersects(ballBounds)) {
            ball.bounceFromPaddle(paddleRight, false);
            ball.setExactY(Math.clamp(ball.getExactY(), 0, Settings.GameSize - Settings.BallSize));
        }
    }

    private void checkScoring() {
        if (ball.x + Settings.BallSize < 0) {
            scoreRight++;
            ball.reset();
        } else if (ball.x > Settings.GameSize) {
            scoreLeft++;
            ball.reset();
        }
    }

    private void movePaddles() {
        if (wPressed) paddleLeft.move(direction.UP);
        if (sPressed) paddleLeft.move(direction.DOWN);
        if (upPressed) paddleRight.move(direction.UP);
        if (downPressed) paddleRight.move(direction.DOWN);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = true;
            case KeyEvent.VK_S -> sPressed = true;
            case KeyEvent.VK_UP -> upPressed = true;
            case KeyEvent.VK_DOWN -> downPressed = true;
            case KeyEvent.VK_ESCAPE -> paused = !paused;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = false;
            case KeyEvent.VK_S -> sPressed = false;
            case KeyEvent.VK_UP -> upPressed = false;
            case KeyEvent.VK_DOWN -> downPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && paused) {
            if (new Rectangle(getWidth() / 2 - 65, 160, 45, 45).contains(e.getPoint())) {
                gameTimer.stop();
                System.exit(1);
            } else if (new Rectangle((int) ((double) getWidth() / 2 - 4.5 * 5), 160, 45, 45).contains(e.getPoint())) {
                paused = false;
            } else if (new Rectangle(getWidth() / 2 + 30, 160, 45, 45).contains(e.getPoint())) {
                gameTimer.stop();
                fireActionPerformed();
            }
            repaint();
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

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        highlighted = -1;
        if (new Rectangle(getWidth() / 2 - 65, 160, 45, 45).contains(e.getPoint())) highlighted = 1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 4.5 * 5), 160, 45, 45).contains(e.getPoint()))
            highlighted = 2;
        if (new Rectangle(getWidth() / 2 + 30, 160, 45, 45).contains(e.getPoint())) highlighted = 3;
        repaint();
    }
}
