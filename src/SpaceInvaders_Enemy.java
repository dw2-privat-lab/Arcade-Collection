public class SpaceInvaders_Enemy {
    private int x,y;
    private final int width,height;
    private int type;
    public SpaceInvaders_Enemy(int x, int y, int width, int height, int type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
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
    public int getType() {
        return type;
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
                y += height+stepSize;
        }
    }
}
