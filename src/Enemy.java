public class Enemy {
    private int x,y;
    private int width,height;
    public Enemy(int x,int y,int width,int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void move( directions direction,int stepSize){
        switch (direction){
            case LEFT:
                x -= stepSize;
                break;
            case RIGHT:
                x += stepSize;
                break;
            case DOWN:
                y += stepSize;
        }
    }
}
