package Pong;

import java.awt.*;

public class Ball extends Rectangle {
    private double exactX;
    private double exactY;

    private double direction;

    private final double speed = (double) Settings.GameSize / 70;
    private final int startX;
    private final int startY;
    public Ball(int x, int y) {
        super(x, y,Settings.BallSize, Settings.BallSize);
        startX = x;
        startY = y;

        this.exactX = x;
        this.exactY = y;

        direction = Math.random() * 240;
        if(direction>60){
            direction+=60;
        }
        if(direction>240){
            direction+=60;
        }
    }

    public void move() {
        double radians = Math.toRadians(direction);

        exactX += Math.cos(radians) * speed;
        exactY += Math.sin(radians) * speed;

        this.x = (int) exactX;
        this.y = (int) exactY;

        bounce();
    }

    private void bounce() {
        int ballSize = Settings.BallSize;

        if (exactY <= 0) {
            exactY = 0;
            direction = -direction;
        }

        if (exactY >= Settings.GameSize-ballSize) {
            exactY = Settings.GameSize-ballSize;
            direction = -direction;
        }
        direction = (direction + 360) % 360;
    }

    void reset(){
        exactX = startX;
        exactY = startY;

        direction = Math.random() * 240;
        if(direction>60){
            direction+=60;
        }
        if(direction>240){
            direction+=60;
        }
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }
    public void setExactX(double exactX) {
        this.exactX = exactX;
    }
    public double getDirection() {
        return direction;
    }
}
