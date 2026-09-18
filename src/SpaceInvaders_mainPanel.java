import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SpaceInvaders_mainPanel extends JPanel implements KeyListener, ActionListener {
    boolean pressedLeft = false;
    boolean pressedRight = false;
    boolean pressedSpace = false;

    private final int scale = 5;
    private final int playerWidth = 13*scale;
    private final int playerHeight = 8*scale;
    SpaceInvaders_Player spaceInvadersPlayer = new SpaceInvaders_Player(200,500,playerWidth);
    private int hearts = 3;


    boolean running = true;

    Image playerImg = new ImageIcon("resources/spaceInvaders/ship.png").getImage();

    SpaceInvaders_EnemyController spaceInvadersEnemyController = new SpaceInvaders_EnemyController(10*scale,12*scale,8*scale,4*scale);

    public SpaceInvaders_mainPanel()
    {
        setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
        setFocusable(true);
        addKeyListener(this);
        spaceInvadersEnemyController.resetEnemies();
        Timer timer = new Timer(5,this);
        timer.start();
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
    public void keyTyped(KeyEvent e) {}

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

        boolean frame = spaceInvadersEnemyController.getFirstFrame();
        for(SpaceInvaders_Enemy e: renderedEnemies ){
            ImageIcon EnemyTile = new ImageIcon();
            if(e.getType()==1)
                EnemyTile = new ImageIcon("resources/spaceInvaders/octopusFrame"+(frame?1:2)+".png");
            if(e.getType()==2)
                EnemyTile = new ImageIcon("resources/spaceInvaders/crabFrame"+(frame?1:2)+".png");
            if(e.getType()==3)
                EnemyTile = new ImageIcon("resources/spaceInvaders/squidFrame"+(frame?1:2)+".png");
            g.drawImage(EnemyTile.getImage(),e.getX(),e.getY(),e.getWidth(),e.getHeight(),null);
        }

        ArrayList<Rectangle> enemieBullets = spaceInvadersEnemyController.getBullets();
        for(Rectangle bullet:enemieBullets){
            g.drawRect(bullet.x,bullet.y,bullet.width,bullet.height);
        }

        g.drawImage(playerImg,spaceInvadersPlayer.getX(),spaceInvadersPlayer.getY(),playerWidth,playerHeight,null);

        Rectangle[] playershots = spaceInvadersPlayer.getBullets();
        for(Rectangle p: playershots){
            g.drawRect(p.x,p.y,p.width,p.height);
        }

        g.setColor(Color.RED);
        for(int i = 0; i<hearts;i++){
            g.drawRect(10+i*40,10,20,20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            spaceInvadersPlayer.move(pressedLeft, pressedRight);

            if (Math.random() > 0.995)
                spaceInvadersEnemyController.randomShot();

            spaceInvadersPlayer.moveBullets();
            spaceInvadersEnemyController.moveBullets();

            checkPlayersBulletCollision();
            checkEnemiesBulletCollision();


            if (pressedSpace)
                spaceInvadersPlayer.shoot();


            repaint();
        }
    }
}
