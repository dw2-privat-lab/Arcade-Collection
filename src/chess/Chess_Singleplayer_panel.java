package chess;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Chess_Singleplayer_panel extends JPanel implements MouseListener, MouseMotionListener {
    public int tileSize = 70;
    int highlightX, highlightY ;
    int hoveredX, hoveredY ;
    int x1, y1, x2, y2;
    boolean firstClick = true;
    Schachlogik spiel = new Schachlogik();
    int Color;
    private boolean customBackground = true;
    private String Spritetype = "Neo";
    private String backgroundName = "Burled_Wood";

    private boolean newGameHovered = false;
    private boolean mousePressed;
    private boolean highlighted ;
    private int Menuhighlight = 1;

    EventListenerList listenerList = new EventListenerList();

    public Chess_Singleplayer_panel(ActionListener actionListener)  {
        listenerList.add(ActionListener.class, actionListener);
        spiel.reset();
        setPreferredSize(new Dimension(tileSize * 9, tileSize * 8));
        addMouseListener(this);
        addMouseMotionListener(this);
        checkBackgroundFiles();
        checkSpriteFiles();
        repaint();
    }
    public void reset(){
        spiel.reset();
    }

    private void checkBackgroundFiles() {
        if (customBackground) {
            File tempBoard = new File("resources/chess/chess/backgrounds/" + backgroundName + "/board.png");
            File tempSelection = new File("resources/chess/backgrounds/" + backgroundName + "/selection.png");
            if (!(tempBoard.exists() && tempSelection.exists())) {
                customBackground = false;
                System.out.println("Required Image Files for Custom Background (no Tiles) not Found");
            }
        }
        if (!customBackground) {
            File tempdark = new File("resources/chess/backgrounds/" + backgroundName + "/dark.png");
            File templight = new File("resources/chess/backgrounds/" + backgroundName + "/light.png");
            if (!(tempdark.exists() && templight.exists())) {
                System.out.println("Required Image Files for Custom Background (using Tiles) not Found");

                File tempBoard = new File("resources/chess/backgrounds/" + backgroundName + "/board.png");
                File tempSelection = new File("resources/chess/backgrounds/" + backgroundName + "/selection.png");
                if (!(tempBoard.exists() && tempSelection.exists())) {
                    backgroundName = "Example";
                    System.out.println("No Image Files for Custom Background Found; reverting to Original Images");
                }
                customBackground = true;
            }
        }
    }

    private void checkSpriteFiles() {
        int SpriteCount = 0;
        for (int i = -6; i <= 6; i++) {
            File tempSprite = new File("resources/chess/Sprites/" + Spritetype + "/" + i + ".png");
            if (tempSprite.exists())
                SpriteCount++;
        }
        if (SpriteCount != 12) {
            System.out.println("Sprites not found");
            Spritetype = "Example";
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        setBackground(new Color(53, 57, 57, 255));
        super.paintComponent(g);
        g.drawRect(0, 0, getWidth() , getHeight());
        paintGamefield(g);
        paintPossibleMoves(g);
        if(mousePressed&& highlighted)
            paintDraggedPiece(g);
        if (spiel.chooseNewPiece)
            paintPieceChangeWindow(g);
        if (spiel.checkmate||spiel.stalemate)
            paintStopScreen(g);
        paintMenu(g);
    }

    private void paintGamefield(Graphics g) {
        boolean whitebackground = true;
        ImageIcon backgroundTile;
        if (customBackground) {
            backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/board.png");
            g.drawImage(backgroundTile.getImage(), 0, 0, 8 * tileSize, 8 * tileSize, null);
        }
        for (int col = 0; col < spiel.Schachfeld.length; col++) {
            whitebackground = !whitebackground;
            for (int row = 0; row < spiel.Schachfeld[0].length; row++) {
                whitebackground = !whitebackground;

                if (!customBackground) {
                    if (whitebackground)
                        backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/light.png");
                    else
                        backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/dark.png");
                    g.drawImage(backgroundTile.getImage(), col * tileSize , row * tileSize, tileSize, tileSize, null);
                }

                ImageIcon tileimage = new ImageIcon("resources/chess/Sprites/" + Spritetype + "/" + spiel.Schachfeld[row][col] + ".png");
                if ((highlightX == col && highlightY == row&& highlighted) || ((hoveredX == col && hoveredY == row) && !spiel.chooseNewPiece)) {
                    if(!mousePressed)
                        g.drawImage(tileimage.getImage(), col * tileSize - 5 , row * tileSize - 5 , tileSize + 10, tileSize + 10, null);
                } else {
                    g.drawImage(tileimage.getImage(), col * tileSize , row * tileSize , tileSize, tileSize, null);
                }
            }
        }
    }

    private void paintPieceChangeWindow(Graphics g) {
        g.setColor(new Color(46, 45, 45, 160));
        g.fillRect(0, 0, getWidth(), getHeight());

        Color = spiel.whiteMoves?-1:1;

        int round = 1;
        boolean whitebackground = true;
        ImageIcon backgroundTile;
        if (customBackground) {
            backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/selection.png");
            g.drawImage(backgroundTile.getImage(), 3 * tileSize , 3 * tileSize , 2 * tileSize, 2 * tileSize, null);
        }

        for (int i = 0; i < 2; i++) {
            whitebackground = !whitebackground;
            for (int j = 0; j < 2; j++) {
                whitebackground = !whitebackground;

                //Draw Background Tiles
                if (!customBackground) {
                    if (whitebackground)
                        backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/light.png");
                    else
                        backgroundTile = new ImageIcon("resources/chess/backgrounds/" + backgroundName + "/dark.png");
                    g.drawImage(backgroundTile.getImage(), (3 + j) * tileSize , (3 + i) * tileSize , tileSize, tileSize, null);
                }

                //Draw selection sprites
                round++;
                ImageIcon spriteImage = new ImageIcon("resources/chess/Sprites/" + Spritetype + "/" + round * Color + ".png");
                if (hoveredX == j + 3 && hoveredY == i + 3)
                    g.drawImage(spriteImage.getImage(), (3 + j) * tileSize - 5  , (3 + i) * tileSize - 5  , tileSize + 10, tileSize + 10, null);
                else
                    g.drawImage(spriteImage.getImage(), (3 + j) * tileSize  , (3 + i) * tileSize , tileSize, tileSize, null);
            }
        }
    }

    private void paintStopScreen(Graphics g) {
        int arc = (int) (3.5 * tileSize / 10);
        g.setColor(new Color(40, 50, 50, 161));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(53, 57, 57, 255));
        g.fillRoundRect((int) (2.5 * tileSize), (int) (2.5 * tileSize), 3 * tileSize, 2 * tileSize, arc, arc);

        g.setColor(new Color(255, 255, 255, 255));
        g.setFont(new Font("Arial", Font.BOLD, 3 * tileSize / 10));
        if(spiel.checkmate){
            String winner = spiel.whiteMoves ? "White" : "Black";
            g.drawString(winner + " Won!", (int) (3.175 * tileSize), 3 * tileSize);
            g.setFont(new Font("Arial", Font.PLAIN, (int) (1.5 * tileSize / 10)));
            g.drawString("by Checkmate", (int) (3.5 * tileSize), (int) (3.25 * tileSize));
        }else {
            g.drawString("Draw", (int) (3.6 * tileSize), 3 * tileSize);
            g.setFont(new Font("Arial", Font.PLAIN, (int) (1.5 * tileSize / 10)));
            g.drawString("by Stalemate", (int) (3.55 * tileSize), (int) (3.25 * tileSize));
        }

        g.setColor(new Color(72, 92, 59, 255));
        g.fillRoundRect((int) (2.8 * tileSize), (int) (3.5 * tileSize), (int) (2.4 * tileSize), (int) (0.75 * tileSize), arc, arc);
        g.setColor(new Color(255, 255, 255, 255));
        g.setFont(new Font("Arial", Font.BOLD, 2 * tileSize / 10));
        g.drawString("New Game", (int) (3.5 * tileSize), (int) (3.94 * tileSize));
        if (newGameHovered) {
            g.drawRoundRect((int) (2.8 * tileSize), (int) (3.5 * tileSize), (int) (2.4 * tileSize), (int) (0.75 * tileSize), arc, arc);
        }
    }
    private void paintMenu(Graphics g) {
        Image menuImage = new ImageIcon("resources/chess/Menu"+(Menuhighlight==1?"_selected":"")+".png").getImage();
        Image settingsImage = new ImageIcon("resources/chess/SettingsIcon"+(Menuhighlight==2?"_selected":"")+".png").getImage();
        Image resetImage = new ImageIcon("resources/chess/reset"+(Menuhighlight==3?"_selected":"")+".png").getImage();
        g.drawImage(menuImage, (int) (8.25*tileSize), (int) (0.25*tileSize), tileSize/2, tileSize/2, null);
        g.drawImage(settingsImage, (int) (8.25*tileSize), (int) (1.25*tileSize), tileSize/2, tileSize/2, null);
        g.drawImage(resetImage, (int) (8.25*tileSize), (int) (2.25*tileSize), tileSize/2, tileSize/2, null);
    }
    private void paintPossibleMoves(Graphics g) {
        if(!(highlightX==-1||highlightY==-1)){
            java.util.ArrayList<int[]> allMoves;
            if(Math.abs(spiel.Schachfeld[highlightY][highlightX])==6){
                allMoves = new java.util.ArrayList<>();
                for(int i=-1;i<=1;i++){
                    for(int j=-1;j<=1;j++){
                        if(!(i==0 && j==0)){
                            if(highlightX+i>=0&&highlightX+i<8&&highlightY+j>=0&&highlightY+j<8) {
                                allMoves.add(new int[]{highlightX + i, highlightY + j});
                            }
                        }
                    }
                }
                if(spiel.UsedTiles[highlightY][highlightX]==0){
                    allMoves.add(new int[]{highlightX+2, highlightY});
                    allMoves.add(new int[]{highlightX-2, highlightY});
                }
            }else{
                allMoves= spiel.generatePseudoMoves(highlightX,highlightY);
            }

            for (int[] Point : allMoves) {
                int pointX = (Point[0] * tileSize);
                int pointY = (Point[1] * tileSize);
                if(spiel.canMove(highlightX,highlightY,Point[0],Point[1])) {
                    int Size = (tileSize/3);
                    g.setColor(new Color(153, 156, 156, 134));
                    g.fillOval(pointX+(tileSize-Size)/2, pointY+(tileSize-Size)/2, Size, Size);
                }
            }
        }
    }
    private void paintDraggedPiece(Graphics g) {
        Point mouse = getMousePosition();
        ImageIcon tileImage = new ImageIcon("resources/chess/Sprites/" + Spritetype + "/" + spiel.Schachfeld[highlightY][highlightX] + ".png");

        g.drawImage(tileImage.getImage(), mouse.x - 5 - tileSize/2 , mouse.y - 5 - tileSize/2 , tileSize + 10, tileSize + 10, null);

    }

    public void highlight(int x, int y) {
        highlighted = true;
        highlightX = x;
        highlightY = y;
    }

    public void unhighlight() {
        highlighted = false;
        highlightX = -1;
        highlightY = -1;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int MouseX = e.getX() / tileSize;
        int MouseY = e.getY() / tileSize;

        if (e.getButton() == MouseEvent.BUTTON1) {
                if(new Rectangle((int) (8.25*tileSize), (int) (0.25*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
                    fireActionPerformed();
                    return;
                }
            if(new Rectangle((int) (8.25*tileSize), (int) (1.25*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
                //code zum settings öffnen
                return;
            }
            if(new Rectangle((int) (8.25*tileSize), (int) (2.25*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
                spiel.reset();
                return;
            }






            if(MouseX<0||MouseX>7||MouseY<0||MouseY>7) {
                repaint();
                return;
            }
            if (!(spiel.chooseNewPiece || spiel.checkmate||spiel.stalemate)) {
                if (firstClick) {
                    if((spiel.whiteMoves? 1:-1)*spiel.Schachfeld[MouseY][MouseX]>0) {
                        mousePressed = true;
                        x1 = MouseX;
                        y1 = MouseY;
                        highlight(x1, y1);
                        firstClick = false;
                    }
                }
                else {
                    x2 = MouseX;
                    y2 = MouseY;
                    //Überprüfung, ob das zweite feld nicht dieselbe Farbe hat
                    if (spiel.Schachfeld[y1][x1] * spiel.Schachfeld[y2][x2] <= 0||(Math.abs(spiel.Schachfeld[y1][x1])== 6 &&Math.abs( spiel.Schachfeld[y2][x2])==4)) {
                        spiel.move(x1, y1, x2, y2);
                        unhighlight();
                        firstClick = true;
                        mousePressed = false;
                    }
                    else {
                        //selber Code wie beim ersten click
                        x1 = MouseX;
                        y1 = MouseY;
                        highlight(x1, y1);
                        firstClick = false;
                        mousePressed = true;
                    }
                }
            }
            //Umgewandelte Figur auswählen
            if (spiel.chooseNewPiece) {
                if ((MouseX == 3 || MouseX == 4) && (MouseY == 3 || MouseY == 4)) {
                    int[][] pieces = {{2, 3}, {4, 5}};
                    spiel.choosePiece(pieces[MouseY - 3][MouseX - 3] * Color);
                }
            }

            if (spiel.checkmate||spiel.stalemate) {
                Rectangle rect = new Rectangle((int) (2.75 * tileSize), (int) (3.45 * tileSize), (int) (2.5 * tileSize), (int) (0.85 * tileSize));
                Point p = new Point(e.getX(), e.getY());
                if (rect.contains(p))
                    spiel.reset();
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        int MouseX = e.getX() / tileSize;
        int MouseY = e.getY() / tileSize;

        if(MouseX<0||MouseX>7||MouseY<0||MouseY>7){
            repaint();
            return;
        }
        mousePressed = false;
        x2 = MouseX;
        y2 = MouseY;
        if (spiel.Schachfeld[y1][x1] * spiel.Schachfeld[y2][x2] <= 0||(Math.abs(spiel.Schachfeld[y1][x1])== 6 &&Math.abs( spiel.Schachfeld[y2][x2])==4)) {
            if(spiel.canMove(x1, y1, x2, y2)){
            spiel.move(x1, y1, x2, y2);
            unhighlight();
            firstClick = true;
            }
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Menuhighlight=-1;
        for(int i = 0; i<3;i++){
            if(new Rectangle((int) (8.25*tileSize), (int) ((i+0.25)*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
                Menuhighlight = i + 1;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
                return;
            }
        }

        if (spiel.checkmate || spiel.stalemate) {
            Rectangle rect = new Rectangle((int) (2.75 * tileSize), (int) (3.45 * tileSize), (int) (2.5 * tileSize), (int) (0.85 * tileSize));
            Point p = new Point(e.getX(), e.getY());
            newGameHovered = rect.contains(p);
            if (newGameHovered)
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            else
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            hoveredX = -1;
            hoveredY = -1;
        } else {

            hoveredX = e.getX() / tileSize;
            hoveredY = e.getY() / tileSize;
            if (hoveredX < 0 || hoveredY < 0 || hoveredX > 7 || hoveredY > 7) {
                mousePressed = false;
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                hoveredX = -1;
                hoveredY = -1;
                repaint();
                return;
            }

            if (spiel.Schachfeld[hoveredY][hoveredY] * (spiel.whiteMoves ? 1 : -1) > 0)
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                hoveredX = -1;
                hoveredY = -1;
            }
        }
        repaint();
    }
    private void fireActionPerformed() {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "return"));
            }
        }
    }
}
