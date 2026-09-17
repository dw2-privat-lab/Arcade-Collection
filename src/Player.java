import java.awt.*;
import java.util.ArrayList;

public class Player {
    private int x,y;
    private int width;
    private ArrayList <Point> shots = new ArrayList<>();

    public Player(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }
    public void shoot(){
        shots.add(new Point(x+width/2,y));
    }
     public void move(boolean Left,boolean Right){
        x -= Left?1:-1;
        x += Right?1:-1;
        for(Point shot:shots){
            shot.setLocation(shot.x,shot.y-=1);
        }
        shots.removeIf(shot -> shot.x < 200);
     }


     public int getX(){
        return x;
     }
     public int getY(){
        return y;
     }
     public Point[] getShots(){
        return shots.toArray(new Point[0]);
     }
}
