public class Player {
    private int x,y;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;

    }
    public void shoot(){

    }
     public void move(boolean Left,boolean Right){
        x -= Left?1:-1;
        x += Right?1:-1;
     }

     public int getX(){
        return x;
     }
     public int getY(){
        return y;
     }

}
