package SpaceInvaders;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SpaceInvaders_mainPanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    private final int scale = 4;
    private final int playerWidth = 13 * scale;
    private final int playerHeight = 8 * scale;
    private final SpaceInvaders_Player spaceInvadersPlayer;
    boolean pressedLeft = false;
    boolean pressedRight = false;
    boolean pressedSpace = false;
    ArrayList<Point> destroyAnimation = new ArrayList<>();
    boolean running = true;
    boolean gameOver = false;
    Image playerImg = new ImageIcon("resources/spaceInvaders/ship.png").getImage();
    Image explosion = new ImageIcon("resources/spaceInvaders/explosion.png").getImage();
    Image gameOverImg = new ImageIcon("resources/spaceInvaders/gameOver.png").getImage();
    Image pausedImg = new ImageIcon("resources/spaceInvaders/paused.png").getImage();
    Image quitImg = new ImageIcon("resources/spaceInvaders/quit.png").getImage();
    Image resumeImg = new ImageIcon("resources/spaceInvaders/resume.png").getImage();
    Image returnImg = new ImageIcon("resources/spaceInvaders/return.png").getImage();
    Image scoreImg = new ImageIcon("resources/spaceInvaders/score.png").getImage();
    SpaceInvaders_EnemyController spaceInvadersEnemyController = new SpaceInvaders_EnemyController(15 * scale, 12 * scale, 8 * scale, 4 * scale);
    EventListenerList listenerList = new EventListenerList();
    private int hearts = 3;
    private double enemiedifficulty = 0.995;
    private int score = 0;
    private int highlighted = 3;

    public SpaceInvaders_mainPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(224 * scale, 150 * scale));
        setFocusable(true);
        addKeyListener(this);
        spaceInvadersEnemyController.resetEnemies();
        spaceInvadersPlayer = new SpaceInvaders_Player(getPreferredSize().width / 2 - playerWidth / 2, getPreferredSize().height - 2 * playerHeight, playerWidth, getPreferredSize().width);
        addMouseListener(this);
        addMouseMotionListener(this);
        Timer timer = new Timer(20, this);
        timer.start();
    }

    protected void fireActionPerformed() {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "return"));
            }
        }
    }

    public void reset() {
        running = true;
        gameOver = false;
        score = 0;
        hearts = 3;
        enemiedifficulty = 0.995;
        spaceInvadersEnemyController.resetEnemies();
        spaceInvadersEnemyController.timer.start();
    }


    private void checkEnemiesBulletCollision() {
        ArrayList<Rectangle> bullets = spaceInvadersEnemyController.getBullets();
        for (Rectangle bullet : bullets) {
            if (bullet.intersects(new Rectangle(spaceInvadersPlayer.getX(), spaceInvadersPlayer.getY(), playerWidth, playerHeight))) {
                hearts--;
                spaceInvadersEnemyController.removeBullet(bullet);
                if (hearts <= -1) {
                    gameOver = true;
                    running = false;
                    spaceInvadersEnemyController.timer.stop();
                }
            }
        }
    }

    private void checkPlayersBulletCollision() {
        ArrayList<SpaceInvaders_Enemy> enemies = spaceInvadersEnemyController.getEnemies();
        Rectangle[] bullets = spaceInvadersPlayer.getBullets();
        if (bullets.length > 0)
            for (SpaceInvaders_Enemy e : enemies) {
                if (new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight()).intersects(bullets[0])) {
                    destroyAnimation.add(new Point(e.getX(), e.getY()));
                    score += e.getType() * 10;
                    spaceInvadersEnemyController.removeEnemies(e);
                    spaceInvadersPlayer.removeShot(bullets[0]);
                    break;
                }
            }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            pressedLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            pressedRight = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            pressedSpace = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (running) {
                running = false;
                spaceInvadersEnemyController.timer.stop();
                repaint();
            } else {
                spaceInvadersEnemyController.timer.start();
                running = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                pressedLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                pressedRight = false;
                break;
            case KeyEvent.VK_SPACE:
                pressedSpace = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());


        ArrayList<SpaceInvaders_Enemy> renderedEnemies = spaceInvadersEnemyController.getEnemies();
        g.setColor(Color.WHITE);

        boolean frame = spaceInvadersEnemyController.getFirstFrame();
        for (SpaceInvaders_Enemy e : renderedEnemies) {
            ImageIcon EnemyTile = new ImageIcon();
            if (e.getType() == 1)
                EnemyTile = new ImageIcon("resources/spaceInvaders/octopusFrame" + (frame ? 1 : 2) + ".png");
            if (e.getType() == 2)
                EnemyTile = new ImageIcon("resources/spaceInvaders/crabFrame" + (frame ? 1 : 2) + ".png");
            if (e.getType() == 3)
                EnemyTile = new ImageIcon("resources/spaceInvaders/squidFrame" + (frame ? 1 : 2) + ".png");
            g.drawImage(EnemyTile.getImage(), e.getX(), e.getY(), e.getWidth(), e.getHeight(), null);
        }

        ArrayList<Rectangle> enemiesBullets = spaceInvadersEnemyController.getBullets();
        for (Rectangle bullet : enemiesBullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        for (Point p : destroyAnimation) {
            g.drawImage(explosion, p.x, p.y, 13 * scale, 8 * scale, null);
        }
        destroyAnimation.clear();

        g.drawImage(playerImg, spaceInvadersPlayer.getX(), spaceInvadersPlayer.getY(), playerWidth, playerHeight, null);

        String renderedScore = score + "";
        for (int i = 0; i < renderedScore.length(); i++) {
            g.drawImage(scoreImg, 800 - 34 * scale, 5, 34 * scale, 8 * scale, null);
            g.drawImage(new ImageIcon("resources/spaceInvaders/numbers/" + renderedScore.charAt(i) + ".png").getImage(), i * 8 * scale + 800, 5, 8 * scale, 8 * scale, null);
        }
        Rectangle[] playershots = spaceInvadersPlayer.getBullets();
        for (Rectangle p : playershots) {
            g.fillRect(p.x, p.y, p.width, p.height);
        }
        for (int i = 0; i < hearts; i++) {
            g.drawImage(playerImg, (hearts + playerWidth + 4) * i + 4 * scale, 10, (int) (playerWidth * 0.75), (int) (playerHeight * 0.75), null);
        }

        if (!running || gameOver) {
            g.setColor(new Color(46, 46, 46, 102));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage((gameOver ? gameOverImg : pausedImg), getWidth() / 2 - ((gameOver ? 61 : 43) * scale) / 2, 99, (gameOver ? 61 : 43) * scale, 11 * scale, null);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            spaceInvadersPlayer.move(pressedLeft, pressedRight);

            if (Math.random() > enemiedifficulty)
                spaceInvadersEnemyController.randomShot();
            enemiedifficulty -= 0.000005;

            spaceInvadersPlayer.moveBullets();
            spaceInvadersEnemyController.moveBullets();

            checkPlayersBulletCollision();
            checkEnemiesBulletCollision();

            if (pressedSpace)
                spaceInvadersPlayer.shoot();


            repaint();
        }
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
                if (gameOver)
                    reset();
                else {
                    spaceInvadersEnemyController.timer.start();
                    running = true;
                }
            }
            highlighted = 2;
            if (new Rectangle((int) ((double) getWidth() / 2 + 6 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint())) {
                fireActionPerformed();
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
        if (new Rectangle((int) ((double) getWidth() / 2 - 15 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 4.5 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 2;
        if (new Rectangle((int) ((double) getWidth() / 2 + 6 * scale), 100 + 12 * scale, 9 * scale, 9 * scale).contains(e.getPoint()))
            highlighted = 3;
        repaint();
    }
}
