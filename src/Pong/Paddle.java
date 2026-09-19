package Pong;

import java.awt.*;

public class Paddle extends Rectangle {

    public Paddle(int x, int y) {
        super(x, y, Settings.GameSize/64, Settings.GameSize/16);
    }

    public void move(direction thisDirection){
        if (thisDirection == direction.UP){
            if(y>0){
                y-= Settings.GameSize/64;
            }
        }
        else if (thisDirection == direction.DOWN){
            if(y< Settings.GameSize-(Settings.GameSize/12)){
                y+= Settings.GameSize/64;
            }
        }
    }
}
