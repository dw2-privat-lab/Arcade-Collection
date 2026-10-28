package Pong;

import java.awt.*;

public class Ball extends Rectangle {
    private final double speed = (double) Settings.GameSize / 86;
    private final int startX;
    private final int startY;
    private double exactX;
    private double exactY;
    private double direction;

    public Ball(int x, int y) {
        super(x, y, Settings.BallSize, Settings.BallSize);
        startX = x;
        startY = y;
        exactX = x;
        exactY = y;
        resetDirection();
    }

    public void move() {
        double radians = Math.toRadians(direction);
        exactX += Math.cos(radians) * speed;
        exactY += Math.sin(radians) * speed;
        x = (int) exactX;
        y = (int) exactY;
        bounce();
    }

    private void bounce() {
        int ballSize = Settings.BallSize;
        if (exactY <= 0) {
            exactY = 0;
            direction = -direction;
        } else if (exactY >= Settings.GameSize - ballSize) {
            exactY = Settings.GameSize - ballSize;
            direction = -direction;
        }
        direction = normalize(direction);
        x = (int) exactX;
        y = (int) exactY;
    }

    private void resetDirection() {
        double horizontalAngle = 25 + Math.random() * 35;
        boolean movingRight = Math.random() < 0.5;
        boolean movingDown = Math.random() < 0.5;
        if (movingRight) {
            direction = movingDown ? horizontalAngle : 360 - horizontalAngle;
        } else {
            direction = movingDown ? 180 - horizontalAngle : 180 + horizontalAngle;
        }
    }

    public void bounceFromPaddle(Paddle paddle, boolean leftPaddle) {
        double ballCenter = exactY + Settings.BallSize / 2.0;
        double paddleCenter = paddle.getCenterY();
        double halfHeight = paddle.height / 2.0;
        double impact = Math.clamp((ballCenter - paddleCenter) / halfHeight, -1.0, 1.0);
        double verticalAngle = impact * 60.0;
        direction = leftPaddle ? verticalAngle : 180.0 - verticalAngle;
        direction = normalize(direction);
        exactX = leftPaddle ? paddle.x + paddle.width : paddle.x - Settings.BallSize;
        x = (int) exactX;
    }

    void reset() {
        exactX = startX;
        exactY = startY;
        x = startX;
        y = startY;
        resetDirection();
    }

    private double normalize(double angle) {
        angle %= 360.0;
        return angle < 0 ? angle + 360.0 : angle;
    }

    public double getExactX() {
        return exactX;
    }

    public void setExactX(double exactX) {
        this.exactX = exactX;
        x = (int) exactX;
    }

    public double getExactY() {
        return exactY;
    }

    public void setExactY(double exactY) {
        this.exactY = exactY;
        y = (int) exactY;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = normalize(direction);
    }
}
