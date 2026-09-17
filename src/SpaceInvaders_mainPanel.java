import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SpaceInvaders_mainPanel extends JPanel implements KeyListener {
    boolean pressedLeft = false;
    boolean pressedRight = false;
    boolean pressedSpace = false;
    private final int playerWidth = 50;
    private final int playerHeight = 50;
    SpaceInvaders_Player spaceInvadersPlayer = new SpaceInvaders_Player(200,500,playerWidth);
    private int hearts = 3;
    private int shootCooldown;


    SpaceInvaders_EnemyController spaceInvadersEnemyController = new SpaceInvaders_EnemyController(20,10,20);

    public SpaceInvaders_mainPanel()
    {
        setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
        setFocusable(true);
        addKeyListener(this);
        spaceInvadersEnemyController.addEnemies(100,10);
    }

    public void run()
    {
        while(true)
        {
            if(Math.random()>0.995)
                spaceInvadersEnemyController.randomShot();
            tick();
            repaint();
        }
    }

    private void tick()
    {
        spaceInvadersPlayer.move(pressedLeft, pressedRight);
        spaceInvadersPlayer.moveBullets();
        spaceInvadersEnemyController.moveBullets();
        checkPlayersBulletCollision();
        checkEnemiesBulletCollision();
        shootCooldown--;
        if(pressedSpace&&shootCooldown<=0){
            shootCooldown = 100;
            spaceInvadersPlayer.shoot()  ;
        }

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkEnemiesBulletCollision()
    {
        ArrayList<Rectangle> bullets = spaceInvadersEnemyController.getBullets();
        for (Rectangle bullet : bullets) {
            if(bullet.intersects(new Rectangle(spaceInvadersPlayer.getX(), spaceInvadersPlayer.getY(),playerWidth,playerHeight))) {
                hearts--;
                spaceInvadersEnemyController.removeBullet(bullet);
            }
        }
    }

    private void checkPlayersBulletCollision()
    {
        ArrayList <SpaceInvaders_Enemy> enemies = spaceInvadersEnemyController.getEnemies();
        Rectangle[] bullets = spaceInvadersPlayer.getBullets();
        for(Rectangle bullet:bullets){
            for(SpaceInvaders_Enemy e:enemies){
                if(new Rectangle(e.getX(),e.getY(),e.getWidth(),e.getHeight()).intersects(bullet)){
                    spaceInvadersEnemyController.removeEnemie(e);
                    spaceInvadersPlayer.removeShot(bullet);
                    break;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            pressedLeft = true;
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            pressedRight = true;
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
            pressedSpace = true;
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
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());


        ArrayList <SpaceInvaders_Enemy> renderedEnemies = spaceInvadersEnemyController.getEnemies();
        g.setColor(Color.WHITE);
        for(SpaceInvaders_Enemy e: renderedEnemies ){
            g.drawRect(e.getX(),e.getY(),e.getWidth(),e.getHeight());
        }

        ArrayList<Rectangle> enemieBullets = spaceInvadersEnemyController.getBullets();
        for(Rectangle bullet:enemieBullets){
            g.drawRect(bullet.x,bullet.y,bullet.width,bullet.height);
        }

        g.drawRect(spaceInvadersPlayer.getX(), spaceInvadersPlayer.getY(),playerWidth,playerHeight);

        Rectangle[] playershots = spaceInvadersPlayer.getBullets();
        for(Rectangle p: playershots){
            g.drawRect(p.x,p.y,p.width,p.height);
        }

        g.setColor(Color.RED);
        for(int i = 0; i<hearts;i++){
            g.drawRect(10+i*40,10,20,20);
        }
    }
}
