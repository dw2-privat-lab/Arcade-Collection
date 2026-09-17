import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SpaceInvaders_EnemyController implements ActionListener {
    ArrayList <SpaceInvaders_Enemy>  enemies = new ArrayList<>();
    ArrayList <Rectangle>  bullets = new ArrayList<>();
    private final int enemywidth;
    private final int enemyheight;
    private final int spacing;

    private directions currentdirection = directions.RIGHT;
    private final int max = 5;
    private int x = 0;

    private final int bulletwidth = 5;
    private final int bulletheight = 10;

    public SpaceInvaders_EnemyController(int enemywidth, int enemyheight, int spacing) {
        this.enemywidth = enemywidth;
        this.enemyheight = enemyheight;
        this.spacing = spacing;

        Timer timer = new Timer(1000,this);
        timer.start();
    }

    public void resetEnemies()
    {
        enemies = new ArrayList();
    }

    public void randomShot()
    {
        int i = (int) (Math.random()*enemies.size());
        SpaceInvaders_Enemy chosen = enemies.get(i);
        bullets.add(new Rectangle(chosen.getX(),chosen.getY(),bulletwidth,bulletheight));
    }

    public void removeBullet(Object o){
        bullets.remove(o);
    }

    public void addEnemies(int Amount,int rows){
    boolean perfectAmount = Amount%rows==0;
        for(int row=0;row+(perfectAmount?0:1)<rows;row++){
            for(int indexOnRow=0;indexOnRow<rows-(perfectAmount?0:1)/Amount;indexOnRow++){
                enemies.add(new SpaceInvaders_Enemy(indexOnRow*(enemywidth+spacing),row*(enemyheight+spacing),enemywidth,enemyheight));
            }
        }
        if(!perfectAmount){
            for(int i=0;i<Amount%rows;i++){
                int customOffset = (rows-1/Amount)/(Amount%rows)-1;
                enemies.add(new SpaceInvaders_Enemy((i+customOffset)*(enemywidth+spacing),(rows-1)*(enemyheight+spacing),enemywidth,enemyheight));
            }
        }
    }

    public void moveEnemies(directions direction){
        for(SpaceInvaders_Enemy e:enemies){
            e.move(direction,enemywidth);
        }
    }
    public void moveBullets(){
        for (Rectangle r: bullets){
            r.setLocation(r.x,r.y+2);
        }
    }

    public ArrayList<SpaceInvaders_Enemy> getEnemies(){
        return new ArrayList<>(enemies);
    }
    public ArrayList<Rectangle> getBullets(){
        return new ArrayList<>(bullets);
    }

    public void removeEnemie(Object o){
        enemies.remove(o);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (currentdirection == directions.RIGHT) {
            x++;
            if (x <= max) {
                moveEnemies(directions.RIGHT);
            } else {
                x = max;
                currentdirection = directions.LEFT;
                moveEnemies(directions.DOWN);
            }
        }
        else if (currentdirection == directions.LEFT) {
            x--;
            if (x >= 0) {
                moveEnemies(directions.LEFT);
            } else {
                x = 0;
                currentdirection = directions.RIGHT;
                moveEnemies(directions.DOWN);
            }
        }
    }
}
