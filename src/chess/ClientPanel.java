package chess;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ClientPanel extends Chess_Singleplayer_panel implements ActionListener {
    String host="localhost";
    int port=8888;
    MessageHandler messageHandler;
    private boolean playsAsWhite=true;
    private String GameCode;
    private boolean waiting = false;
    private boolean disconnect =  false;
    public ClientPanel(ActionListener actionListener){
        super(actionListener);

    }

    public void Host() {
        waiting=true;
        messageHandler = new MessageHandler(host, port, null, true, this);
        new Thread(messageHandler).start();
    }

    public void Join(String code) {
        messageHandler = new MessageHandler(host, port, code, false, this);
        new Thread(messageHandler).start();
    }

    public void setGameCode(String GameCode){
        this.GameCode=GameCode;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void reset(){
        if(messageHandler!=null)
            messageHandler.sendResetRequest();
    }

    @Override
    protected void sendMove(int x,int y, int toX, int toY) {
        if(messageHandler!=null)
            messageHandler.sendMove(new Move(x,y,toX,toY));
    }


     @Override
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(disconnect){
            g.setColor(new Color(53, 57, 57, 187));
            g.fillRect(0, 0, getWidth()-tileSize, getHeight());
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fontMetrics = g.getFontMetrics();
            g.drawString("Your Friend has diconnected", (getWidth()-tileSize - fontMetrics.stringWidth("Your Friend has diconnected")) / 2, getHeight()/2);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            fontMetrics = g.getFontMetrics();
            g.drawString("Leave this Game to start a new one", (getWidth()-tileSize - fontMetrics.stringWidth("Leave this Game to start a new one")) / 2, getHeight()/2+37);
        }else if(waiting){
            g.setColor(new Color(53, 57, 57, 187));
            g.fillRect(0, 0, getWidth()-tileSize, getHeight());
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fontMetrics = g.getFontMetrics();
            g.drawString("Waiting for Second Player", (getWidth()-tileSize - fontMetrics.stringWidth("Waiting for Second Player")) / 2, getHeight()/2);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            fontMetrics = g.getFontMetrics();
            g.drawString("This is your Room Code:"+ GameCode, (getWidth()-tileSize - fontMetrics.stringWidth("This is your Room Code: "+ GameCode)) / 2, getHeight()/2+37);
            g.drawString("Share this code with your Friend", (getWidth()-tileSize - fontMetrics.stringWidth("Share this code with your Friend")) / 2, getHeight()/2+54);
        }
     }

    @Override
    protected void paintGamefield(Graphics g) {
        g.drawImage(boardImg, 0, 0, 8 * tileSize, 8 * tileSize, null);

        for (int col = 0; col < spiel.Schachfeld.length; col++) {
            for (int row = 0; row < spiel.Schachfeld[0].length; row++) {
                if ((highlightX == col && highlightY == (playsAsWhite?row:7-row)&& highlighted) || ((hoveredX == col && hoveredY == (playsAsWhite?row:7-row)) && !spiel.chooseNewPiece)) {
                    if(!mousePressed)
                        g.drawImage(spriteCache.get(spiel.Schachfeld[row][col]), col * tileSize - 5 , (playsAsWhite?row:7-row) * tileSize - 5 , tileSize + 10, tileSize + 10, null);
                } else {
                    g.drawImage(spriteCache.get(spiel.Schachfeld[row][col]), col * tileSize , (playsAsWhite?row:7-row) * tileSize , tileSize, tileSize, null);
                }
            }
        }
    }
    @Override
    protected void paintMenu(Graphics g) {
        g.drawImage((Menuhighlight==1?menuImageSelected:menuImage), (int) (8.25*tileSize), (int) (0.25*tileSize), tileSize/2, tileSize/2, null);
        g.drawImage((Menuhighlight==2?settingsImageSelected:settingsImage), (int) (8.25*tileSize), (int) (1.25*tileSize), tileSize/2, tileSize/2, null);
    }

    @Override
    protected void paintDraggedPiece(Graphics g) {
        Point mouse = getMousePosition();
        g.drawImage(spriteCache.get(spiel.Schachfeld[playsAsWhite?highlightY:7-highlightY][highlightX]), mouse.x - 5 - tileSize/2 , mouse.y - 5 - tileSize/2 , tileSize + 10, tileSize + 10, null);
    }

    @Override
    protected void paintPossibleMoves(Graphics g) {
        int convertedY = playsAsWhite?highlightY:7-highlightY;
        if(!(highlightX==-1||highlightY==-1)) {
            java.util.ArrayList<int[]> allMoves;
            if (Math.abs(spiel.Schachfeld[convertedY][highlightX]) == 6) {
                allMoves = new java.util.ArrayList<>();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (!(i == 0 && j == 0)) {
                            if (highlightX + i >= 0 && highlightX + i < 8 && convertedY + j >= 0 && convertedY + j < 8) {
                                allMoves.add(new int[]{highlightX + i, convertedY + j});
                            }
                        }
                    }
                }
                if (spiel.UsedTiles[convertedY][highlightX] == 0) {
                    allMoves.add(new int[]{highlightX + 2, convertedY});
                    allMoves.add(new int[]{highlightX - 2, convertedY});
                }

            } else {
                allMoves = spiel.generatePseudoMoves(highlightX, convertedY);
            }

            for (int[] Point : allMoves) {
                int pointX = (Point[0] * tileSize);
                int pointY = ((playsAsWhite?Point[1]:7-Point[1]) * tileSize);
                if (spiel.canMove(highlightX, convertedY, Point[0], Point[1])) {
                    int Size = (tileSize / 3);
                    g.setColor(new Color(153, 156, 156, 134));
                    g.fillOval(pointX + (tileSize - Size) / 2, pointY + (tileSize - Size) / 2, Size, Size);
                }
            }
        }
    }

    @Override
    protected void sendReset(){
    }

    @Override
    protected void leave(){
        disconnect = false;
        messageHandler.closeConnection();
        super.leave();
    }


    @Override
    protected void checkMoveClick(int MouseX, int MouseY) {
        if(waiting)
            return;
        if(MouseX<0||MouseX>7||MouseY<0||MouseY>7) {
            repaint();
            return;
        }
        if (firstClick) {
            if((playsAsWhite? 1:-1)*spiel.Schachfeld[(playsAsWhite?MouseY:7-MouseY)][MouseX]>0) {
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
            if (spiel.Schachfeld[(playsAsWhite?y1:7-y1)][x1] * spiel.Schachfeld[(playsAsWhite?y2:7-y2)][x2] <= 0||spiel.Schachfeld[(playsAsWhite?y1:7-y1)][x1]==(playsAsWhite? 6:-6) &&spiel.Schachfeld[(playsAsWhite?y2:7-y2)][x2]==(playsAsWhite? 4:-4)) {
                sendMove(x1, (playsAsWhite?y1:7-y1), x2,(playsAsWhite?y2:7-y2));
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


    @Override
    protected void mouseReleasedCheck(){
        if(waiting)
            return;
        int convertedY1 = playsAsWhite?y1:7-y1;
        int convertedY2 = playsAsWhite?y2:7-y2;
        if (spiel.Schachfeld[convertedY1][x1] * spiel.Schachfeld[convertedY2][x2] <= 0||(Math.abs(spiel.Schachfeld[convertedY1][x1])== 6 &&Math.abs( spiel.Schachfeld[convertedY2][x2])==4)) {
            if(spiel.canMove(x1, convertedY1, x2, convertedY2)){
                sendMove(x1, (convertedY1), x2, (convertedY2));
                unhighlight();
                firstClick = true;
            }
        }
    }
    @Override
    protected void checkResetHovered(Point e){
    }

    @Override
    protected void mouseHoveredCheck(Point p){
        if(waiting)
            return;

        hoveredX = (int) (p.getX() / tileSize);
        hoveredY = (int) (p.getY() / tileSize);
        if (hoveredX < 0 || hoveredY < 0 || hoveredX > 7 || hoveredY > 7) {
            mousePressed = false;
            hoveredX = -1;
            hoveredY = -1;
            repaint();
            return;
        }

        if (spiel.Schachfeld[playsAsWhite?hoveredY:7-hoveredY][hoveredX] * (playsAsWhite ? 1 : -1) > 0)
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else {
            hoveredX = -1;
            hoveredY = -1;
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if ("IncomingMove".equals(e.getActionCommand())) {
            Move receivedMove = (Move) e.getSource();
            spiel.move(receivedMove.x, receivedMove.y, receivedMove.toX, receivedMove.toY);
        }
        else if ("IncomingMessage".equals(e.getActionCommand())) {
            String receivedMessage = (String) e.getSource();
            if ("RESET".equals(receivedMessage)) {
                spiel.reset();
            }
            if("joined".equals(receivedMessage)) {
                Object[] listeners = listenerList.getListenerList();

                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == ActionListener.class) {
                        ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Joined_Game"));
                    }
                }
            }
            if ("DISCONNECT".equals(receivedMessage)) {
                disconnect = true;
            }

        }
        else if ("IncomingColor".equals(e.getActionCommand())) {
            playsAsWhite = (boolean) e.getSource();

        }
        else if ("RoomCodeGenerated".equals(e.getActionCommand())) {
            this.GameCode = (String) e.getSource();
            Object[] listeners = listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ActionListener.class) {
                    ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Joined_Game"));
                }
            }
            System.out.println("Room Created. Tell your opponent to join using code: " + GameCode);
        } else if ("FriendJoined".equals(e.getActionCommand())) {
            waiting = false;
            repaint();
        }
        repaint();
    }
}
