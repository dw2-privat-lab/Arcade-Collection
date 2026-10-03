package twentyfourtyeight;

import SpaceInvaders.directions;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class TwentyfourtyeightPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    GameLogic gameLogic = new GameLogic();
    int tilesize = 77;
    int animationFrame = 10;
    final int MAX_FRAMES = 8;
    final int arc = 7;
    final int Xoffset = arc+5;
    final int Yoffset = 120;
    private Rectangle keepGoingHitbox = new Rectangle(Xoffset + 40, Yoffset + 2 * tilesize + 30, 113, 30);
    private Rectangle startOverHitbox = new Rectangle(Xoffset + 40+tilesize+tilesize/2, Yoffset + 2 * tilesize + 30, 113, 30);
    private int highlighted = 0;
    private final Rectangle scoreBoundingBox = new Rectangle(Xoffset + 4 * tilesize - 195, 15, 95, 35);
    private final Rectangle highscoreBoundingBox = new Rectangle(Xoffset + 4 * tilesize -95, 15, 95, 35);
    private final Rectangle sizeSelectionHitbox = new Rectangle(Xoffset + 4 * tilesize -20, 60, 20, 20);
    private int sizeSelectionHovered = 0;
    private boolean sizeSelectionHitboxFocused = false;
    private final Rectangle newGameHitbox = new Rectangle(Xoffset - arc, 87, 90, 20);
    private final Rectangle returnHitbox = new Rectangle(Xoffset - arc+109, 87, 90, 20);
    EventListenerList listenerList = new EventListenerList();
    public TwentyfourtyeightPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(2*Xoffset+gameLogic.getCustomSize()*tilesize-arc, Yoffset+gameLogic.getCustomSize()*tilesize+Xoffset-arc));
        addKeyListener(this);
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);

        Timer timer = new Timer(16, e -> {
            if (animationFrame < MAX_FRAMES) {
                animationFrame++;
                repaint();
            }
        });
        timer.start();
    }
    private void recalculateBoundingBoxPositions(){
        if(gameLogic.getCustomSize()>3) {
            scoreBoundingBox.setLocation(getWidth() - Xoffset - 195 + arc, 15);
            highscoreBoundingBox.setLocation(getWidth() - Xoffset - 95 + arc, 15);
            sizeSelectionHitbox.setLocation(Xoffset + 4 * tilesize -20, 60);
        }else{
            scoreBoundingBox.setLocation(getWidth() - Xoffset - 95, 10);
            highscoreBoundingBox.setLocation(getWidth() - Xoffset - 95, 50);
            sizeSelectionHitbox.setLocation(Xoffset+73, 60);
        }
        keepGoingHitbox.setLocation((getWidth()-arc)/2-113, Yoffset +gameLogic.getCustomSize()/3 * tilesize + 30);
        startOverHitbox.setLocation((getWidth()+arc)/2, Yoffset +gameLogic.getCustomSize()/3 * tilesize + 30);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", Font.BOLD, 55));
        g.setColor(new Color(118, 109, 100));
        g.drawString("2048",Xoffset-arc,55);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        if(gameLogic.getCustomSize()>3) {

            g.drawString("Join the numbers and get to the ", Xoffset - arc, 75);
            g.setFont(getFont().deriveFont(Font.BOLD));
            g.drawString("2048 tile!", Xoffset - arc + fm.stringWidth("Join the numbers and get to the "), 75);
        }
        g.setColor(new Color(174, 168, 162));
        g.fillRoundRect(scoreBoundingBox.x, scoreBoundingBox.y, scoreBoundingBox.width, scoreBoundingBox.height, arc,arc);
        g.fillRoundRect(highscoreBoundingBox.x, highscoreBoundingBox.y, highscoreBoundingBox.width, highscoreBoundingBox.height, arc,arc);
        g.fillRoundRect(returnHitbox.x,returnHitbox.y, returnHitbox.width, returnHitbox.height, arc,arc);
        g.fillRoundRect(newGameHitbox.x, newGameHitbox.y, newGameHitbox.width, newGameHitbox.height, arc,arc);

        fm = g.getFontMetrics();
        g.setColor(Color.white);
        g.drawString("Score", scoreBoundingBox.x+ (scoreBoundingBox.width-fm.stringWidth("Score"))/2, scoreBoundingBox.y+12);
        g.drawString("Best", highscoreBoundingBox.x+ (highscoreBoundingBox.width-fm.stringWidth("Best"))/2, highscoreBoundingBox.y+12);
        g.drawString("Return",returnHitbox.x+ (returnHitbox.width-fm.stringWidth("Return"))/2,returnHitbox.y+14);
        g.drawString("new Game", newGameHitbox.x+ (newGameHitbox.width-fm.stringWidth("new Game"))/2, newGameHitbox.y+14);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fm = g.getFontMetrics();
        g.drawString(gameLogic.getScore()+"", scoreBoundingBox.x+ (scoreBoundingBox.width-fm.stringWidth(gameLogic.getScore()+""))/2, scoreBoundingBox.y+30);
        g.drawString(gameLogic.getHighscore()+"", highscoreBoundingBox.x+ (highscoreBoundingBox.width-fm.stringWidth(gameLogic.getHighscore()+""))/2, highscoreBoundingBox.y+30);
        if(highlighted == 4)
            g.drawRoundRect(returnHitbox.x,returnHitbox.y, returnHitbox.width, returnHitbox.height, arc,arc);
        if(highlighted == 3)
            g.drawRoundRect(newGameHitbox.x, newGameHitbox.y, newGameHitbox.width, newGameHitbox.height, arc,arc);

        g.setColor(new Color(161, 159, 155, 255));
        g.fillRoundRect(Xoffset-arc, Yoffset-arc, gameLogic.getCustomSize()*tilesize+arc, gameLogic.getCustomSize()*tilesize+arc, arc, arc);
        for (int i = 0; i < gameLogic.getCustomSize(); i++) {
            for (int j = 0; j < gameLogic.getCustomSize(); j++) {
                g.setColor(getColor(0));
                g.fillRoundRect(j * tilesize + Xoffset, i * tilesize + Yoffset, tilesize - arc, tilesize - arc, arc, arc);
            }
        }

        if (animationFrame < MAX_FRAMES) {
            double progress = (double) animationFrame / MAX_FRAMES;

            for (TileAnimation animatedTile : gameLogic.getCurrentAnimations()) {
                double curX = animatedTile.startCol * tilesize + (animatedTile.endCol - animatedTile.startCol) * tilesize * progress;
                double curY = animatedTile.startRow * tilesize + (animatedTile.endRow - animatedTile.startRow) * tilesize * progress;

                drawTile(g, (int) curX + Xoffset, (int) curY + Yoffset, animatedTile.value);
            }
        } else {
            int[][] temp = gameLogic.getGameField();
            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < temp[0].length; j++) {
                    if (temp[i][j] != 0) {
                        drawTile(g, j * tilesize + Xoffset, i * tilesize + Yoffset, temp[i][j]);
                    }
                }
            }
        }
        if(gameLogic.isGameOver())
            drawGameOver((Graphics2D) g);
        else if(gameLogic.isRenderWin())
            drawWinScreen((Graphics2D) g);
        drawSizeSelection(g);
    }
    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(234, 234, 234, 123));
        g.fillRoundRect(Xoffset-arc, Yoffset-arc, gameLogic.getCustomSize()*tilesize+arc, gameLogic.getCustomSize()*tilesize+arc, arc, arc);
        g.setFont(new Font("Arial", Font.BOLD, gameLogic.getCustomSize()==3?45:50));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(112, 112, 112));
        g.drawString("Game Over!", (getWidth()-fm.stringWidth("Game Over!"))/2, Yoffset+gameLogic.getCustomSize()/3*tilesize+20);
        g.setColor(new Color(149, 136, 127));
        g.fillRoundRect((getWidth()-startOverHitbox.width)/2, startOverHitbox.y, startOverHitbox.width, startOverHitbox.height, 5, 5);
        if(highlighted==1) {
            g.setColor(Color.white);
            g.drawRoundRect((getWidth()-startOverHitbox.width)/2, startOverHitbox.y, startOverHitbox.width, startOverHitbox.height, 5, 5);
        }
        g.setFont(new Font("Arial", Font.BOLD, 15));
        fm = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255));
        g.drawString("Try again",(getWidth()-fm.stringWidth("Try again"))/2, startOverHitbox.y+ startOverHitbox.height - fm.getHeight()/2);
    }

    private void drawWinScreen(Graphics2D g) {
        g.setColor(new Color(237, 194, 46, 123));
        g.fillRoundRect(Xoffset-arc, Yoffset-arc, gameLogic.getCustomSize()*tilesize+arc, gameLogic.getCustomSize()*tilesize+arc, arc, arc);
        for(int i = 0; i < gameLogic.getCustomSize(); i++) {
            for(int j = 0; j < gameLogic.getCustomSize(); j++) {
                if(gameLogic.getGameField()[i][j] == 2048) {
                    drawTile(g,j*tilesize + Xoffset, i*tilesize + Yoffset, 2048);
                }
            }
        }
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 250, 238));
        g.drawString("You Win!", (getWidth()-fm.stringWidth("You Win!"))/2, Yoffset+gameLogic.getCustomSize()/3*tilesize+20);
        g.setColor(new Color(149, 136, 127));
        g.fillRoundRect(startOverHitbox.x, startOverHitbox.y, startOverHitbox.width, startOverHitbox.height, 5, 5);
        g.fillRoundRect(keepGoingHitbox.x, keepGoingHitbox.y, keepGoingHitbox.width, keepGoingHitbox.height, 5, 5);
        g.setColor(Color.white);
        if(highlighted==1)
            g.drawRoundRect(startOverHitbox.x, startOverHitbox.y, startOverHitbox.width, startOverHitbox.height, 5, 5);
        if(highlighted==2)
            g.drawRoundRect(keepGoingHitbox.x, keepGoingHitbox.y, keepGoingHitbox.width, keepGoingHitbox.height, 5, 5);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        fm = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255));
        g.drawString("Keep going",keepGoingHitbox.x+(keepGoingHitbox.width-fm.stringWidth("Keep going"))/2, keepGoingHitbox.y+ keepGoingHitbox.height - fm.getHeight()/2);
        g.drawString("Try again",startOverHitbox.x+(startOverHitbox.width-fm.stringWidth("Try again"))/2, startOverHitbox.y+ startOverHitbox.height - fm.getHeight()/2);
    }

    private void drawTile(Graphics g, int x, int y, int value) {
        g.setColor(getColor(value));
        g.fillRoundRect(x, y, tilesize - arc, tilesize - arc, arc, arc);

        if (value == 2 || value == 4) g.setColor(new Color(119, 110, 101));
        else g.setColor(new Color(249, 246, 242));

        g.setFont(new Font("Arial", Font.BOLD, tilesize / 3));
        String text = String.valueOf(value);

        FontMetrics fm = g.getFontMetrics();
        int textX = x + (tilesize - arc - fm.stringWidth(text)) / 2;
        int textY = y + ((tilesize - arc - fm.getHeight()) / 2) + fm.getAscent();

        g.drawString(text, textX, textY);
    }

    private void drawSizeSelection(Graphics g) {
        g.setColor(new Color(181, 181, 181));
        g.fillRoundRect(sizeSelectionHitbox.x,sizeSelectionHitbox.y, sizeSelectionHitbox.width, sizeSelectionHitbox.height, arc, arc);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        if(sizeSelectionHitboxFocused||highlighted==5){
            g.fillRoundRect(sizeSelectionHitbox.x,sizeSelectionHitbox.y,sizeSelectionHitbox.width, 6 * sizeSelectionHitbox.height, arc, arc);
            g.setColor(Color.white);
            g.drawRoundRect(sizeSelectionHitbox.x,sizeSelectionHitbox.y, sizeSelectionHitbox.width, sizeSelectionHitbox.height, arc, arc);

            if(sizeSelectionHovered>=1 && sizeSelectionHovered<=5){
                g.setColor(new Color(0, 0, 0,74));
                g.fillRoundRect(sizeSelectionHitbox.x, sizeSelectionHitbox.y + sizeSelectionHovered * sizeSelectionHitbox.height, sizeSelectionHitbox.width, sizeSelectionHitbox.height, arc, arc);
            }

            g.setColor(new Color(0, 0, 0));
            for(int i =3 ;i<=7;i++)
                g.drawString(i+"",sizeSelectionHitbox.x+6,sizeSelectionHitbox.y+(i-2)*sizeSelectionHitbox.width+15);
        }

        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(0, 0, 0));
        g.drawString("Size selected:",sizeSelectionHitbox.x- fm.stringWidth("Size selected:"),sizeSelectionHitbox.y+15);
        g.drawString(gameLogic.getCustomSize()+"",sizeSelectionHitbox.x+6,sizeSelectionHitbox.y+15);


    }

    private static Color getColor(int value) {
        return switch (value) {
            case 0 -> new Color(204, 200, 192);
            case 2 -> new Color(238, 228, 218);
            case 4 -> new Color(237, 224, 200);
            case 8 -> new Color(242, 177, 121);
            case 16 -> new Color(245, 149, 99);
            case 32 -> new Color(246, 124, 95);
            case 64 -> new Color(246, 94, 59);
            case 128 -> new Color(237, 207, 114);
            case 256 -> new Color(237, 204, 97);
            case 512 -> new Color(237, 200, 80);
            case 1024 -> new Color(237, 197, 63);
            case 2048 -> new Color(237, 194, 46);
            default -> new Color(60, 58, 50);
        };
    }


    @Override
    public void keyPressed(KeyEvent e) {
        sizeSelectionHitboxFocused = false;
        repaint();

        if(gameLogic.isGameOver()) return;
        if (animationFrame < MAX_FRAMES) return;
        if(gameLogic.isRenderWin()) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> gameLogic.move(directions.UP);
            case KeyEvent.VK_DOWN -> gameLogic.move(directions.DOWN);
            case KeyEvent.VK_LEFT -> gameLogic.move(directions.LEFT);
            case KeyEvent.VK_RIGHT -> gameLogic.move(directions.RIGHT);
            default -> { return; }
        }

        animationFrame = 0;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        sizeSelectionHitboxFocused = sizeSelectionHitbox.contains(e.getX(), e.getY());
        if(highlighted==5) {
            if(new Rectangle(sizeSelectionHitbox.x,sizeSelectionHitbox.y+sizeSelectionHitbox.width,sizeSelectionHitbox.width,5*sizeSelectionHitbox.height).contains(e.getPoint())) {
                int selectedIndex = (e.getY() - sizeSelectionHitbox.y - sizeSelectionHitbox.height) / sizeSelectionHitbox.height;
                int newSize = selectedIndex + 3;

                if (newSize >= 3 && newSize <= 7) {
                    gameLogic.setCustomSize(newSize);
                    setPreferredSize(new Dimension(2 * Xoffset + gameLogic.getCustomSize() * tilesize - arc,Yoffset + gameLogic.getCustomSize() * tilesize + Xoffset - arc));
                    revalidate();
                    fireActionPerformed("resize");
                    recalculateBoundingBoxPositions();
                }
                sizeSelectionHitboxFocused = false;
                highlighted = 0;
                repaint();
                mouseMoved(e);
                return;
            }
        }
        if (newGameHitbox.contains(e.getPoint()))gameLogic.reset();
        else if (returnHitbox.contains(e.getPoint()))fireActionPerformed("return");
        else if(gameLogic.isRenderWin()) {
            if (startOverHitbox.contains(e.getPoint())) gameLogic.reset();
            else if (keepGoingHitbox.contains(e.getPoint())) gameLogic.setRenderWin(false);
        }
        else if(gameLogic.isGameOver()) {
            if(new Rectangle((getWidth()-startOverHitbox.width)/2,startOverHitbox.y,startOverHitbox.width,startOverHitbox.height).contains(e.getPoint()))
                gameLogic.reset();
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(sizeSelectionHitbox.contains(e.getPoint())) {
            highlighted = 5;
            sizeSelectionHovered=0;
        }
        else if(highlighted==5&&new Rectangle(sizeSelectionHitbox.x, sizeSelectionHitbox.y + sizeSelectionHitbox.width, sizeSelectionHitbox.width, 5 * sizeSelectionHitbox.height).contains(e.getPoint()))            sizeSelectionHovered = (e.getY() - sizeSelectionHitbox.y) / sizeSelectionHitbox.height;
        else if (newGameHitbox.contains(e.getPoint()))highlighted=3;
        else if (returnHitbox.contains(e.getPoint()))highlighted=4;
        else if(gameLogic.isRenderWin()) {
            if (startOverHitbox.contains(e.getPoint())) highlighted = 1;
            else if (keepGoingHitbox.contains(e.getPoint())) highlighted = 2;
            else highlighted=0;
        } else if (gameLogic.isGameOver()) {
            if(new Rectangle((getWidth()-startOverHitbox.width)/2,startOverHitbox.y,startOverHitbox.width,startOverHitbox.height).contains(e.getPoint()))
                highlighted=1;
            else highlighted=0;
        } else highlighted=0;

        if(highlighted!=0)setCursor(new Cursor(Cursor.HAND_CURSOR));
        else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }


    private void fireActionPerformed(String action) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
}
