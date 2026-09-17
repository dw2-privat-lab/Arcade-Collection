import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class RenderingPanel extends JPanel implements KeyListener {
    boolean moveLeft = false;
    boolean moveRight = false;
    private final int playerWidth = 50;
    private final int playerHeight = 50;
    Player player = new Player(200,500,playerWidth);

    EnemyController enemies = new EnemyController(20,10,5);
    public RenderingPanel()
    {
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        addKeyListener(this);
        enemies.addEnemies(100,10);

    }
    public void run()
    {
        while(true)
        {
            tick();
            repaint();
        }
    }

    private void tick()
    {
        //player.move(moveLeft,moveRight);
        player.move(moveLeft,moveRight);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveLeft = true;
                break;
                case KeyEvent.VK_RIGHT:
                    moveRight = true;
                    break;
                    case KeyEvent.VK_SPACE:
                        player.shoot();
                    break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveLeft = false;
                break;
                case KeyEvent.VK_RIGHT:
                moveRight = false;
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        ArrayList <Enemy> renderedEnemies = enemies.getEnemies();
        for(Enemy e: renderedEnemies ){
            g.drawRect(e.getX(),e.getY(),e.getWidth(),e.getHeight());
        }
        g.drawRect(player.getX(),player.getY(),playerWidth,playerHeight);
        Point[] playershots = player.getShots();
        for(Point p: playershots){
            g.drawRect(p.x-5,p.y-5,10,10);
        }
    }
}
