package chess;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Chess_Singleplayer_panel extends JPanel implements MouseListener, MouseMotionListener {
    public int tileSize = 80;

    boolean highlighted ;
    int highlightX, highlightY =-1;
    int hoveredX, hoveredY =-1;

    boolean newGameHovered = false;
    boolean mousePressed;
    protected int Menuhighlight = -1;

    int x1, y1, x2, y2;
    boolean firstClick = true;

    Schachlogik spiel = new Schachlogik();
    int Color;

    private String Spritetype = "Neo";
    private String backgroundName = "Green";
    private boolean SettingsOpened = false;
    private int settingsPieceSelected = 0;
    private int settingsBoardSelected = 1;
    private boolean firstSettingsTabOpened = true;

    Color defaultGray = new Color(53, 57, 57, 255);

    protected Image boardImg;
    private Image selectionImg;

    protected Map<Integer, Image> spriteCache = new HashMap<>();
    protected final Image menuImage = new ImageIcon("resources/chess/Menu.png").getImage();
    protected final Image settingsImage = new ImageIcon("resources/chess/SettingsIcon.png").getImage();
    private final Image resetImage = new ImageIcon("resources/chess/reset.png").getImage();
    protected final Image menuImageSelected = new ImageIcon("resources/chess/Menu_selected.png").getImage();
    protected final Image settingsImageSelected = new ImageIcon("resources/chess/SettingsIcon_selected.png").getImage();
    private final Image resetImageSelected = new ImageIcon("resources/chess/reset_selected.png").getImage();



    EventListenerList listenerList = new EventListenerList();

    public Chess_Singleplayer_panel(ActionListener actionListener)  {
        listenerList.add(ActionListener.class, actionListener);
        spiel.reset();

        reloadBoard();
        reloadSprites();
        setPreferredSize(new Dimension(tileSize * 9, tileSize * 8));
        addMouseListener(this);
        addMouseMotionListener(this);
        repaint();
    }
    public void reset(){
        spiel.reset();
        highlighted = false;
    }

    public void reloadSprites(){
        spriteCache.clear();
        for (int i = -6; i <= 6; i++) {
            File tempSprite = new File("resources/chess/Sprites/" + Spritetype + "/" + i + ".png");
            if (tempSprite.exists()) {
                spriteCache.put(i, new ImageIcon(tempSprite.getPath()).getImage());
            }
        }
        repaint();
    }
    public void reloadBoard(){
        boardImg = new ImageIcon("resources/chess/backgrounds/"+backgroundName+"/board.png").getImage();
        selectionImg = new ImageIcon("resources/chess/backgrounds/"+backgroundName+"/selection.png").getImage();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(defaultGray);
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
        if (SettingsOpened)
            paintSettings(g);
    }

    protected void paintGamefield(Graphics g) {
        g.drawImage(boardImg, 0, 0, 8 * tileSize, 8 * tileSize, null);

        for (int col = 0; col < spiel.Schachfeld.length; col++) {
            for (int row = 0; row < spiel.Schachfeld[0].length; row++) {
                if ((highlightX == col && highlightY == row&& highlighted) || ((hoveredX == col && hoveredY == row) && !spiel.chooseNewPiece)) {
                    if(!mousePressed)
                        g.drawImage(spriteCache.get(spiel.Schachfeld[row][col]), col * tileSize - 5 , row * tileSize - 5 , tileSize + 10, tileSize + 10, null);
                } else {
                    g.drawImage(spriteCache.get(spiel.Schachfeld[row][col]), col * tileSize , row * tileSize , tileSize, tileSize, null);
                }
            }
        }
    }

    protected void paintPieceChangeWindow(Graphics g) {
        g.setColor(new Color(46, 45, 45, 160));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.drawImage(selectionImg, 3 * tileSize , 3 * tileSize , 2 * tileSize, 2 * tileSize, null);
        Color = spiel.whiteMoves?1:-1;

        int round = 1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                round++;
                if (hoveredX == j + 3 && hoveredY == i + 3)
                    g.drawImage(spriteCache.get(round*Color), (3 + j) * tileSize - 5  , (3 + i) * tileSize - 5  , tileSize + 10, tileSize + 10, null);
                else
                    g.drawImage(spriteCache.get(round*Color), (3 + j) * tileSize  , (3 + i) * tileSize , tileSize, tileSize, null);
            }
        }
    }

    private void paintStopScreen(Graphics g) {
        int arc = (int) (3.5 * tileSize / 10);
        g.setColor(new Color(53, 57, 57, 161));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(defaultGray);
        g.fillRoundRect((int) (2.5 * tileSize), (int) (2.5 * tileSize), 3 * tileSize, 2 * tileSize, arc, arc);

        g.setColor(java.awt.Color.WHITE);
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

    protected void paintMenu(Graphics g) {
        g.drawImage((Menuhighlight==1?menuImageSelected:menuImage), (int) (8.25*tileSize), (int) (0.25*tileSize), tileSize/2, tileSize/2, null);
        g.drawImage((Menuhighlight==2?settingsImageSelected:settingsImage), (int) (8.25*tileSize), (int) (1.25*tileSize), tileSize/2, tileSize/2, null);
        g.drawImage((Menuhighlight==3?resetImageSelected:resetImage), (int) (8.25*tileSize), (int) (2.25*tileSize), tileSize/2, tileSize/2, null);
    }

    private void paintSettings(Graphics g) {
        int arc = (int) (3.5 * tileSize / 10);

        g.setColor(defaultGray);
        g.fillRoundRect(tileSize, tileSize, 6 * tileSize, 5 * tileSize, arc, arc);

        g.setFont(new Font("Arial", Font.BOLD, 3 * tileSize / 10));
        g.setColor(java.awt.Color.WHITE);
        g.drawString("Settings", (int) (1.5 * tileSize), (int) (1.6 * tileSize));

        g.setColor(new Color(104, 104, 104, 255));
        g.drawLine((int) (1.5 * tileSize), (int) (1.8 * tileSize), (int) (6.5 * tileSize), (int) (1.8 * tileSize));


        if(firstSettingsTabOpened){
            g.fillRoundRect((int) (1.4 * tileSize), (int) (2.175 * tileSize),tileSize,(int)(0.3*tileSize), arc/2, arc/2);
            paintBoardSelector(g);
        }
        else{
            g.fillRoundRect((int) (4.15 * tileSize), (int) (2.175 * tileSize),tileSize,(int)(0.3*tileSize), arc/2, arc/2);
            paintPieceSelector(g);
        }

        g.setFont(new Font("Arial", Font.BOLD, tileSize / 5));
        g.setColor(java.awt.Color.WHITE);
        g.drawString("Board", (int) (1.5 * tileSize), (int) (2.4 * tileSize));

        g.drawString("Pieces", (int) (4.25 * tileSize), (int) (2.4 * tileSize));
    }

    private void paintBoardSelector(Graphics g) {
        int arc = (int) (3.5 * tileSize / 10);

        g.drawImage(new ImageIcon("resources/chess/backgrounds/Burled_Wood/selection.png").getImage(),(int)(1.4*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Green/selection.png").getImage(),(int)(2.8*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Default/selection.png").getImage(),(int)(4.2*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Dark_Blue/selection.png").getImage(),(int)(5.6*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Purple/selection.png").getImage(),(int)(1.4*tileSize), (int) (4.4*tileSize),tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Orange/selection.png").getImage(),(int)(2.8*tileSize),(int) (4.4*tileSize),tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Stone/selection.png").getImage(),(int)(4.2*tileSize),(int) (4.4*tileSize),tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/backgrounds/Sky_&_Sea/selection.png").getImage(),(int)(5.6*tileSize),(int) (4.4*tileSize),tileSize,tileSize,null);
        g.setColor(new Color(72, 92, 59, 255));
        g.drawRoundRect((int)((settingsBoardSelected%4)*1.4*tileSize+1.3*tileSize),(int)(settingsBoardSelected<4?2.9*tileSize:4.3*tileSize), (int) (tileSize*1.2), (int) (tileSize*1.2),arc,arc);
    }

    private void paintPieceSelector(Graphics g) {
        int arc = (int) (3.5 * tileSize / 10);

        g.drawImage(new ImageIcon("resources/chess/sprites/Neo/1.png").getImage(),(int)(1.4*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/sprites/Default/1.png").getImage(),(int)(2.8*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/sprites/Wood/1.png").getImage(),(int)(4.2*tileSize),3*tileSize,tileSize,tileSize,null);
        g.drawImage(new ImageIcon("resources/chess/sprites/Neon/1.png").getImage(),(int)(5.6*tileSize),3*tileSize,tileSize,tileSize,null);
        g.setColor(new Color(72, 92, 59, 255));
        g.drawRoundRect((int)(settingsPieceSelected*1.4*tileSize+1.3*tileSize),(int)(2.9*tileSize), (int) (tileSize*1.2), (int) (tileSize*1.2),arc,arc);
        }

    protected void paintPossibleMoves(Graphics g) {
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

    protected void paintDraggedPiece(Graphics g) {
        Point mouse = getMousePosition();
        g.drawImage(spriteCache.get(spiel.Schachfeld[highlightY][highlightX]), mouse.x - 5 - tileSize/2 , mouse.y - 5 - tileSize/2 , tileSize + 10, tileSize + 10, null);
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

    protected void sendMove(int x, int y,int toX, int toY) {
        spiel.move(x,y,toX,toY);
    }

    protected void leave(){
        fireActionPerformed();
        repaint();
    }

    private void checkMenuClick(Point e){
        if(new Rectangle((int) (8.25*tileSize), (int) (0.25*tileSize), tileSize/2, tileSize/2).contains(e)) {
            leave();
            return;
        }
        if(new Rectangle((int) (8.25*tileSize), (int) (1.25*tileSize), tileSize/2, tileSize/2).contains(e)) {
            SettingsOpened =!SettingsOpened;
            repaint();
            return;
        }
        if(new Rectangle((int) (8.25*tileSize), (int) (2.25*tileSize), tileSize/2, tileSize/2).contains(e)) {
            sendReset();
            repaint();
        }
        if(SettingsOpened) {
            if (new Rectangle((int) (1.4 * tileSize), (int) (2.175 * tileSize), tileSize, (int) (0.3 * tileSize)).contains(e))
                firstSettingsTabOpened = true;
            if (new Rectangle((int) (4.15 * tileSize), (int) (2.175 * tileSize), tileSize, (int) (0.3 * tileSize)).contains(e))
                firstSettingsTabOpened = false;
            if (firstSettingsTabOpened) {
                if (new Rectangle((int) (1.4 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 0;
                    backgroundName = "Burled_Wood";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (2.8 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 1;
                    backgroundName = "Green";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (4.2 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 2;
                    backgroundName = "Default";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (5.6 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 3;
                    backgroundName = "Dark_Blue";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (1.4 * tileSize), (int) (4.4 * tileSize), tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 4;
                    backgroundName = "Purple";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (2.8 * tileSize), (int) (4.4 * tileSize), tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 5;
                    backgroundName = "Orange";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (4.2 * tileSize), (int) (4.4 * tileSize), tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 6;
                    backgroundName = "Stone";
                    reloadBoard();
                    return;
                }
                if (new Rectangle((int) (5.6 * tileSize), (int) (4.4 * tileSize), tileSize, tileSize).contains(e)) {
                    settingsBoardSelected = 7;
                    backgroundName = "Sky_&_Sea";
                    reloadBoard();
                }
            } else {
                if (new Rectangle((int) (1.4 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsPieceSelected = 0;
                    Spritetype = "Neo";
                    reloadSprites();
                    return;
                }
                if (new Rectangle((int) (2.8 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsPieceSelected = 1;
                    Spritetype = "Default";
                    reloadSprites();
                    return;
                }
                if (new Rectangle((int) (4.2 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsPieceSelected = 2;
                    Spritetype = "Wood";
                    reloadSprites();
                    return;
                }
                if (new Rectangle((int) (5.6 * tileSize), 3 * tileSize, tileSize, tileSize).contains(e)) {
                    settingsPieceSelected = 3;
                    Spritetype = "Neon";
                    reloadSprites();
                }
            }
        }
    }

    protected void sendReset(){
        reset();
        repaint();
    }

    protected void checkMoveClick(int MouseX, int MouseY) {
        //check if click is on the board
        if(MouseX<0||MouseX>7||MouseY<0||MouseY>7) {
            repaint();
            return;
        }
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
            if (spiel.Schachfeld[y1][x1] * spiel.Schachfeld[y2][x2] <= 0||(Math.abs(spiel.Schachfeld[y1][x1])== 6 &&Math.abs( spiel.Schachfeld[y2][x2])==4)) {
                sendMove(x1, y1, x2, y2);
                unhighlight();
                firstClick = true;
                mousePressed = false;
            }
            else {
                x1 = MouseX;
                y1 = MouseY;
                highlight(x1, y1);
                firstClick = false;
                mousePressed = true;
            }
        }
    }
    protected void checkChooseNewPiece(int MouseX, int MouseY) {
        if ((MouseX == 3 || MouseX == 4) && (MouseY == 3 || MouseY == 4)) {
            int[][] pieces = {{2, 3}, {4, 5}};
            spiel.choosePiece(pieces[MouseY - 3][MouseX - 3] * Color);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int MouseX = e.getX() / tileSize;
        int MouseY = e.getY() / tileSize;

        if (e.getButton() == MouseEvent.BUTTON1) {
            checkMenuClick(e.getPoint());

            if (!(spiel.chooseNewPiece || spiel.checkmate||spiel.stalemate)) {
                checkMoveClick(MouseX, MouseY);
            }

            if (spiel.chooseNewPiece) {
                checkChooseNewPiece(MouseX, MouseY);
            }

            if (spiel.checkmate||spiel.stalemate) {
                if (new Rectangle((int) (2.75 * tileSize), (int) (3.45 * tileSize), (int) (2.5 * tileSize), (int) (0.85 * tileSize)).contains(e.getPoint()))
                    reset();
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
        mouseReleasedCheck();
        repaint();
    }

    protected void mouseReleasedCheck(){
        if (spiel.Schachfeld[y1][x1] * spiel.Schachfeld[y2][x2] <= 0||(Math.abs(spiel.Schachfeld[y1][x1])== 6 &&Math.abs( spiel.Schachfeld[y2][x2])==4)) {
            if(spiel.canMove(x1, y1, x2, y2)){
                sendMove(x1, y1, x2, y2);
                unhighlight();
                firstClick = true;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        repaint();
    }
    protected void highlightReset(Point e){
        Menuhighlight = 3;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        Menuhighlight=-1;
        for(int i = 0; i<2;i++){
            if(new Rectangle((int) (8.25*tileSize), (int) ((i+0.25)*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
                Menuhighlight = i + 1;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
                return;
            }
        }
        if(new Rectangle((int) (8.25*tileSize), (int) ((2.25)*tileSize), tileSize/2, tileSize/2).contains(e.getX(), e.getY())) {
            highlightReset(e.getPoint());
            return;
        }

        if (spiel.checkmate || spiel.stalemate) {
            newGameHovered = new Rectangle((int) (2.75 * tileSize), (int) (3.45 * tileSize), (int) (2.5 * tileSize), (int) (0.85 * tileSize)).contains(e.getPoint());
            if (newGameHovered)
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            else
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            hoveredX = -1;
            hoveredY = -1;
            repaint();
        } else {
            if(spiel.chooseNewPiece)
                highlightChoosePiece(e.getPoint());
            else
                mouseHoveredCheck(e.getPoint());
        }
        repaint();
    }
    protected void highlightChoosePiece(Point p){
        hoveredX = (int) (p.getX() / tileSize);
        hoveredY = (int) (p.getY() / tileSize);
        if(new Rectangle(3*tileSize,3*tileSize,2*tileSize,2*tileSize).contains(p)){
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
    protected void mouseHoveredCheck(Point p){

        hoveredX = (int) (p.getX() / tileSize);
        hoveredY = (int) (p.getY() / tileSize);
        if (hoveredX < 0 || hoveredY < 0 || hoveredX > 7 || hoveredY > 7) {
            mousePressed = false;
            hoveredX = -1;
            hoveredY = -1;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            repaint();
            return;
        }

        if (spiel.Schachfeld[hoveredY][hoveredX] * (spiel.whiteMoves ? 1 : -1) > 0)
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            hoveredX = -1;
            hoveredY = -1;
        }
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
