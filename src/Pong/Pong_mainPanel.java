package Pong;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class Pong_mainPanel extends JPanel implements KeyListener, MouseListener,MouseMotionListener {
    private final Ball ball;
    private final Paddle paddleLeft;
    private final Paddle paddleRight;

    private int scoreLeft = 0;
    private int scoreRight = 0;

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private int highlighted = -1;
    private boolean paused = false;

    Image pausedImg = new ImageIcon("resources/spaceInvaders/paused.png").getImage();
    Image quitImg = new ImageIcon("resources/spaceInvaders/quit.png").getImage();
    Image resumeImg = new ImageIcon("resources/spaceInvaders/resume.png").getImage();
    Image returnImg = new ImageIcon("resources/spaceInvaders/return.png").getImage();

    EventListenerList  listenerList = new EventListenerList();

    public Pong_mainPanel(ActionListener actionListener) {
        setPreferredSize(new Dimension(Settings.GameSize, Settings.GameSize));

        listenerList.add(ActionListener.class, actionListener);

        ball = new Ball((Settings.GameSize-Settings.BallSize)/2, (Settings.GameSize-Settings.BallSize)/2);
        paddleLeft = new Paddle(Settings.GameSize / 16, (Settings.GameSize * 15) / 32);
        paddleRight = new Paddle((Settings.GameSize * 14) / 16, (Settings.GameSize * 15) / 32);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        Timer gameTimer = new Timer(16, _ -> {
            if(!paused) {
                movePaddles();
                ball.move();
                //debug_paddlesfollowBall();
                checkBallPadleCollision();
                checkScoring();
                repaint();
            }
        });
        gameTimer.start();
    }
    public void reset(){
        scoreLeft = 0;
        scoreRight = 0;
        ball.reset();
        paddleLeft.setLocation(Settings.GameSize/ 16, (Settings.GameSize * 15) / 32);
        paddleRight.setLocation(Settings.GameSize -Settings.GameSize/ 16, (Settings.GameSize * 15) / 32);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(scoreLeft), Settings.GameSize/2- Settings.ScoreOffset, 30);
        g.drawString(String.valueOf(scoreRight), Settings.GameSize/2+ Settings.ScoreOffset, 30);

        g.fillRect(Settings.GameSize/2- Settings.LineWidth/2,0, Settings.LineWidth/2, Settings.GameSize);

        g.fillRect((int)paddleLeft.getX(),(int) paddleLeft.getY(), Settings.BallSize, Settings.GameSize / 16);
        g.fillRect((int)paddleRight.getX(),(int) paddleRight.getY(), Settings.BallSize, Settings.GameSize / 16);

        g.fillRect((int)ball.getX(),(int)ball.getY(),Settings.BallSize,Settings.BallSize);

        if(paused){
            g.setColor(new Color(46, 46, 46, 102));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(pausedImg, (getWidth()  -  43 * 5 )/ 2, 99, 43 * 5, 11 * 5, null);
            if (highlighted == 1)
                g.drawImage(quitImg, (int) ((double) getWidth() / 2 - 16 * 5), 100 + 55, 55, 55, null);
            else
                g.drawImage(quitImg, (int) ((double) getWidth() / 2 - 15 * 5), 100 + 60, 45, 45, null);
            if (highlighted == 2)
                g.drawImage(resumeImg, (int) ((double) getWidth() / 2 - 5.5 * 5), 100 + 55, 55, 55, null);
            else
                g.drawImage(resumeImg, (int) ((double) getWidth() / 2 - 4.5 * 5), 100 + 60, 45, 45, null);
            if (highlighted == 3)
                g.drawImage(returnImg, (int) ((double) getWidth() / 2 + 5 * 5), 100 + 55, 55, 55, null);
            else
                g.drawImage(returnImg, (int) ((double) getWidth() / 2 + 6 * 5), 100 + 60, 45, 45, null);

        }
    }

    private void checkBallPadleCollision(){
        if(paddleLeft.intersects(ball.getBounds())){
            ball.setDirection(180-ball.getDirection());
            ball.setExactX(paddleLeft.getX()+Settings.BallSize);
        }
        if(paddleRight.intersects(ball.getBounds())){
            ball.setDirection(180-ball.getDirection());
            ball.setExactX(paddleRight.getX()-Settings.BallSize);
        }
    }

    private void checkScoring(){
        if(ball.getX()<0){
            ball.reset();
            scoreRight++;
        }
        if (ball.getX()+Settings.BallSize> Settings.GameSize) {
            ball.reset();
            scoreLeft++;
        }
    }
    void debug_paddlesfollowBall(){
        paddleLeft.setLocation((int) paddleLeft.getX(), (int) ball.getY());
        paddleRight.setLocation((int) paddleRight.getX(), (int) ball.getY());
    }
    private void movePaddles(){
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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (new Rectangle((int) ((double) getWidth() / 2 - 65), 160, 45, 45).contains(e.getPoint()))
                System.exit(1);
            if (new Rectangle((int) ((double) getWidth() / 2 - 4.5*5), 160, 45, 45).contains(e.getPoint())) {
                paused = false;
            }
            if (new Rectangle((int) ((double) getWidth() / 2 + 30), 160, 45, 45).contains(e.getPoint())) {
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
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        highlighted = -1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 65), 160, 45, 45).contains(e.getPoint()))
            highlighted = 1;
        if (new Rectangle((int) ((double) getWidth() / 2 - 4.5*5), 160 , 45, 45).contains(e.getPoint()))
            highlighted = 2;
        if (new Rectangle((int) ((double) getWidth() / 2 + 30), 160 , 45, 45).contains(e.getPoint()))
            highlighted = 3;
        repaint();
    }
}
