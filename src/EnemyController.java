import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class EnemyController implements ActionListener {
    ArrayList <Enemy>  enemies = new ArrayList<>();
    private int enemywidth;
    private int enemyheight;
    private int spacing;
    private directions currentdirection = directions.RIGHT;
    private final int max = 5;
    private int x = 0;

    public EnemyController(int enemywidth, int enemyheight, int spacing) {
        this.enemywidth = enemywidth;
        this.enemyheight = enemyheight;
        this.spacing = spacing;

        Timer timer = new Timer(100,this);
        timer.start();
    }

    public void resetEnemies()
    {
        enemies = new ArrayList();
    }

    public void addEnemies(int Amount,int rows){
    boolean perfectAmount = Amount%rows==0;
        for(int row=0;row+(perfectAmount?0:1)<rows;row++){
            for(int indexOnRow=0;indexOnRow<rows-(perfectAmount?0:1)/Amount;indexOnRow++){
                enemies.add(new Enemy(indexOnRow*(enemywidth+spacing),row*(enemyheight+spacing),enemywidth,enemyheight));
            }
        }
        if(!perfectAmount){
            for(int i=0;i<Amount%rows;i++){
                int customOffset = (rows-1/Amount)/(Amount%rows)-1;
                enemies.add(new Enemy((i+customOffset)*(enemywidth+spacing),(rows-1)*(enemyheight+spacing),enemywidth,enemyheight));
            }
        }
    }

    public void moveEnemies(directions direction){
        for(Enemy e:enemies){
            e.move(direction,enemywidth);
        }
    }
    public ArrayList<Enemy> getEnemies(){
        return enemies;
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
