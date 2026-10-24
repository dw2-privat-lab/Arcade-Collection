package PacMan;

import commonTools.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PacMan_MainPanel extends JPanel {
    int tilesize = 20;
    int ghostSize = (int) (tilesize*1.8);
    int ghostOffset = (ghostSize-tilesize)/2;
    int temp=0;
    Board board;
    PacMan_player player;
    Image[][][] ghostSprites = new Image[5][4][2];
    Image boardImage = ImageLoader.loadImage(this.getClass(),"PacMan/board.png");
    Image[] scaredGhosts = new Image[4];
    int[][] renderBoard;
    boolean ghostFrame1 = true;

    public PacMan_MainPanel() {
        board = new Board();
        player = board.getPlayer();
        setPreferredSize(new Dimension(28 * tilesize, 31 * tilesize));
        Timer t = new Timer(33, e -> repaint());
        t.start();
        setFocusable(true);
        bindDirection(KeyEvent.VK_LEFT, Directions.LEFT);
        bindDirection(KeyEvent.VK_RIGHT, Directions.RIGHT);
        bindDirection(KeyEvent.VK_UP, Directions.UP);
        bindDirection(KeyEvent.VK_DOWN, Directions.DOWN);
        loadSprites();
    }

    private void loadSprites(){
        for(int i = 0; i < 4; i++){
            for(Directions dir : Directions.values()){
                ghostSprites[i][dir.ordinal()][0] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/" + (i + 1) + "_" + dir + "_Frame" +1+ ".png");
                ghostSprites[i][dir.ordinal()][1] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/" + (i + 1) + "_" + dir + "_Frame" +2+ ".png");
            }
        }
        for(Directions dir : Directions.values()){
            ghostSprites[4][dir.ordinal()][0] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/dead_" + dir + ".png");
        }
        scaredGhosts[0]= ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_blue_0.png");
        scaredGhosts[1]= ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_blue_1.png");
        scaredGhosts[2]= ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_white_0.png");
        scaredGhosts[3]= ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_white_1.png");
    }

    private void bindDirection(int keyCode, Directions direction) {
        String key = "move-" + direction.name().toLowerCase();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), key);
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.setNextDirection(direction);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderBoard = board.getBoard();
        for (int i = 0; i < renderBoard.length; i++) {
            for (int j = 0; j < renderBoard[0].length; j++) {
                switch (renderBoard[i][j]) {
                    case -1 -> g.setColor(Color.RED);
                    case 0 -> g.setColor(Color.GREEN);
                    case 1 -> g.setColor(Color.YELLOW);
                    case 2 -> g.setColor(Color.BLUE);
                }
                g.fillRect(j * tilesize, i * tilesize, tilesize, tilesize);
            }
        }
        g.drawImage(boardImage, 0, 0,getWidth(),getHeight(), this);
        g.setColor(Color.YELLOW);
        g.fillRect((int) (board.getPlayer().x * tilesize), (int) (board.getPlayer().y * tilesize), tilesize, tilesize);
        for (PacMan_Ghost ghost : board.getGhosts()) {
            if(ghost.isDead())
                g.drawImage(ghostSprites[4][ghost.current_direction.ordinal()][0],(int) (ghost.x * tilesize- ghostOffset), (int) (ghost.y * tilesize- ghostOffset), ghostSize, ghostSize, this);
            else if(ghost.isFrightend)
                g.drawImage(scaredGhosts[ghostFrame1?0:1],(int) (ghost.x * tilesize- ghostOffset), (int) (ghost.y * tilesize- ghostOffset), ghostSize, ghostSize, this);

            else {
                g.drawImage(ghostSprites[ghost.type - 1][ghost.current_direction.ordinal()][ghostFrame1 ? 0 : 1], (int) (ghost.x * tilesize- ghostOffset), (int) (ghost.y * tilesize- ghostOffset), ghostSize, ghostSize, this);

                g.setColor(ghost.type == 1 ? new Color(200, 0, 0) : ghost.type == 2 ? new Color(200, 0, 200) : ghost.type == 3 ? new Color(0, 200, 200) : new Color(200, 145, 0));
                g.fillRect((int) (ghost.x * tilesize), (int) (ghost.y * tilesize), tilesize, tilesize);
            }
        }
        temp++;
        if(temp >=10) {
            ghostFrame1 = !ghostFrame1;
            temp = 0;
        }
    }
}
