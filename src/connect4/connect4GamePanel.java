package connect4;

import commonTools.SoundPlayer;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class connect4GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private final connect4_gameLogic gameLogic = new connect4_gameLogic();
    private int yellowScore = 0;
    private int redScore = 0;
    onlineHandler onlineHandler;
    String roomCode = "";
    boolean isWaiting = true;
    boolean disconnect = false;
    private boolean online = true;
    private boolean isYellow = false;
    private final Rectangle returnHitbox = new Rectangle(15, 150, 90, 35);
    private final Rectangle muteHitbox = new Rectangle(15, 195, 35, 35);
    private boolean returnHitboxhovered = false;
    private boolean muteHitboxhovered = false;
    private boolean muted = false;
    Image muteImg = new ImageIcon("resources/Muted.png").getImage();
    Image notMuteImg = new ImageIcon("resources/notMuted.png").getImage();

    private connect4Server activeServer;
    EventListenerList listenerList = new EventListenerList();
    public connect4GamePanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(800, 500));
        setBackground(new Color(30, 30, 30));
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void hostGameServer(){
        if (activeServer != null) {
            activeServer.stopServer();
        }

        activeServer = new connect4Server();
        Thread gameServerThread = new Thread(activeServer);
        gameServerThread.start();
    }
    public void joinAsHostGame(String host){
        reset();
        onlineHandler = new onlineHandler(host,55555,true,null,this);
        Thread onlineThread = new Thread(onlineHandler);
        onlineThread.start();
        online = true;
        isWaiting = true;
    }
    public void joinGame(String host,String Code){
        isWaiting = false;
        reset();
        onlineHandler = new onlineHandler(host,55555,false,Code,this);
        Thread onlineThread = new Thread(onlineHandler);
        onlineThread.start();
        online = true;
    }

    public void reset(){
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        disconnect = false;
        isYellow = false;
        yellowScore = 0;
        redScore = 0;
        gameLogic.resetField();
        online = false;
        isWaiting = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int sideWidth = 120;
        int boardWidth = getWidth() - (sideWidth * 2);
        int boardHeight = getHeight();

        drawSide(g2d, 0, online?isYellow?"YOU":"OPPONENT":"YELLOW", yellowScore, Color.YELLOW, gameLogic.firstPlayerPlaying);
        drawSide(g2d, getWidth() - sideWidth, online?isYellow?"OPPONENT":"YOU":"RED", redScore, Color.RED, !gameLogic.firstPlayerPlaying);

        int cols = gameLogic.gameField.length;
        int rows = gameLogic.gameField[0].length;
        double cellW = (double) boardWidth / cols;
        double cellH = (double) boardHeight / rows;
        double tokenSize = Math.min(cellW, cellH) * 0.75;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                int token = gameLogic.gameField[x][y];
                double drawX = sideWidth + x * cellW + (cellW - tokenSize) / 2;
                double drawY = y * cellH + (cellH - tokenSize) / 2;

                if (token == 1) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fill(new Ellipse2D.Double(drawX, drawY, tokenSize, tokenSize));
                } else if (token == -1) {
                    g2d.setColor(Color.RED);
                    g2d.fill(new Ellipse2D.Double(drawX, drawY, tokenSize, tokenSize));
                }
            }
        }

        Area boardArea = new Area(new RoundRectangle2D.Double(sideWidth, 0, boardWidth, boardHeight, 15, 15));
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                double holeX = sideWidth + x * cellW + (cellW - tokenSize) / 2;
                double holeY = y * cellH + (cellH - tokenSize) / 2;
                boardArea.subtract(new Area(new Ellipse2D.Double(holeX, holeY, tokenSize, tokenSize)));
            }
        }

        g2d.setColor(new Color(20, 50, 150));
        g2d.fill(boardArea);

        if (gameLogic.won != 0) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(sideWidth, 0, boardWidth, boardHeight);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
            String text;
            if(online)
                text = (gameLogic.won == 1 ?isYellow? "YOU WIN!" : "YOUR OPPONENT WINS!":isYellow?"YOUR OPPONENT WINS!":"YOU WIN!");
            else
                text = (gameLogic.won == 1 ? "YELLOW" : "RED") + " WINS!";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(text, sideWidth + (boardWidth - fm.stringWidth(text)) / 2, boardHeight / 2);
        }
        if(isWaiting)
            drawWaitingOverlay((Graphics2D) g);
        if(disconnect)
            drawDisconnectionScreen((Graphics2D) g);


        g2d.setColor(new Color(220, 50, 50));
        g2d.fillRoundRect(returnHitbox.x, returnHitbox.y, returnHitbox.width, returnHitbox.height, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fmReturn = g2d.getFontMetrics();
        g2d.drawString("Return", returnHitbox.x + (returnHitbox.width - fmReturn.stringWidth("Return")) / 2, returnHitbox.y + 22);
        if(returnHitboxhovered){
            g2d.setColor(new Color(255, 255, 255));
        }

        g2d.setColor(new Color(57, 57, 57));
        g2d.fillRoundRect(muteHitbox.x, muteHitbox.y, muteHitbox.width, muteHitbox.height, 10, 10);

        if(muted)
            g2d.drawImage(muteImg,muteHitbox.x+5, muteHitbox.y+5,muteHitbox.width-10,muteHitbox.height-10, this);
        else g2d.drawImage(notMuteImg,muteHitbox.x+5, muteHitbox.y+5,muteHitbox.width-10,muteHitbox.height-10, this);
        if(muteHitboxhovered){
            g2d.setColor(new Color(255, 255, 255));
            g2d.drawRoundRect(muteHitbox.x, muteHitbox.y, muteHitbox.width, muteHitbox.height, 10, 10);
        }
    }

    private void drawSide(Graphics2D g2d, int x, String name, int score, Color color, boolean active) {
        g2d.setColor(color);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawString(name, x + 25, 50);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2d.drawString("Score: " + score, x + 25, 80);

        if (active && gameLogic.won == 0) {
            g2d.setColor(color);
            g2d.fillOval(x + 50, 100, 20, 20);
        }
    }

    private void drawWaitingOverlay(Graphics2D g2d){
        g2d.setColor(new Color(0, 0, 0, 115));
        g2d.fillRect(0,0,getWidth(),getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("Waiting for players!", (int) ((getWidth()-fm.getStringBounds("Waiting for players!",g2d).getWidth())/2),getHeight()/2);
        g2d.drawString("This is your room Code: "+roomCode, (int) ((getWidth()-fm.getStringBounds("This is your room Code: "+roomCode,g2d).getWidth())/2),getHeight()/2+35);
    }

    private void drawDisconnectionScreen(Graphics2D g2d){
        g2d.setColor(new Color(0, 0, 0, 115));
        g2d.fillRect(0,0,getWidth(),getHeight());
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("Your Opponent has disconnected", (int) ((getWidth()-fm.getStringBounds("Your Opponent has disconnected",g2d).getWidth())/2),getHeight()/2);
        g2d.drawString("Exit to start a new game", (int) ((getWidth()-fm.getStringBounds("Exit to start a new game",g2d).getWidth())/2),getHeight()/2+35);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (returnHitbox.contains(e.getPoint())) {
            if(onlineHandler!=null)
                onlineHandler.closeConnection();
            fireActionPerformed("return");
            return;
        }
        if(muteHitbox.contains(e.getPoint())) {
            muted = !muted;
            repaint();
        }
        if(isWaiting||disconnect)
            return;
        int sideWidth = 120;
        int boardWidth = getWidth() - (sideWidth * 2);

        if (gameLogic.won != 0) {
            if (gameLogic.won == 1) yellowScore++;
            else if (gameLogic.won == -1) redScore++;
            gameLogic.resetField();
            repaint();
            return;
        }

        int clickX = e.getX() - sideWidth;
        if (clickX >= 0 && clickX < boardWidth) {
            int col = clickX / (boardWidth / gameLogic.gameField.length);
            if(!online) {
                gameLogic.move(col);
                if(!muted)
                    SoundPlayer.playSound("resources/click.wav");
            }
            else if(gameLogic.firstPlayerPlaying==isYellow) onlineHandler.sendMove(col);
            repaint();
        }
    }

    private void fireActionPerformed(String action) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
            }
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Incoming Move" -> {
                gameLogic.move((Integer) e.getSource());
                repaint();
                if(!muted)
                    SoundPlayer.playSound("resources/click.wav");
                return;
            }
            case "Incoming Color" -> {
                isYellow = (boolean) e.getSource();
                repaint();
            }
            case "JOINED" -> fireActionPerformed("RENDER ME");
            case "RoomHosted" -> {
                roomCode = e.getSource().toString();
                fireActionPerformed("RENDER ME");
            }
            case "FRIEND_JOINED" -> isWaiting = false;
            case "DISCONNECTED" -> disconnect = true;
            case "ERROR" -> {
                Object[] listeners = listenerList.getListenerList();
                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == ActionListener.class) {
                        ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(e.getSource().toString(), ActionEvent.ACTION_PERFORMED,"ERROR"));
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        returnHitboxhovered = returnHitbox.contains(e.getPoint());
        muteHitboxhovered = muteHitbox.contains(e.getPoint());
        if(returnHitboxhovered) setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(muteHitboxhovered) setCursor(new Cursor(Cursor.HAND_CURSOR));
        else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }
}