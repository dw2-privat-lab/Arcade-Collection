package twentyfourtyeight;

import SpaceInvaders.directions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TwentyfourtyeightPanel extends JPanel implements KeyListener {
    GameLogic gameLogic = new GameLogic();
    int tilesize = 40;
    public TwentyfourtyeightPanel() {
        setPreferredSize(new Dimension(4*tilesize+10, 4*tilesize+10));
        addKeyListener(this);
        setFocusable(true);
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int[][] temp = gameLogic.getGameField();
        for(int i = 0; i < temp.length; i++){
            for(int j = 0; j < temp[0].length; j++){
                g.drawString(temp[j][i]+"",i*tilesize+10,j*tilesize+10);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                gameLogic.move(directions.UP);
                break;
            case KeyEvent.VK_DOWN:
                gameLogic.move(directions.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                gameLogic.move(directions.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                gameLogic.move(directions.RIGHT);
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
