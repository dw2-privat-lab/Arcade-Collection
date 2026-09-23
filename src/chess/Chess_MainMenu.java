package chess;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class Chess_MainMenu extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {

    private Chess_Singleplayer_panel chess_singleplayer_panel = new Chess_Singleplayer_panel(this);
    private ClientPanel clientPanel = new ClientPanel(this);

    Color defaultGray = new Color(53, 57, 57, 255);
    Color defaultGreen = new Color(72, 92, 59, 255);
    Color darkPanelGray = new Color(38, 41, 41, 255);
    Color darkButtonGray = new Color(80, 85, 85, 255);

    // 1: Main Menu, 2: Singleplayer, 3: Online Client
    private int panelSelected = 1;

    private final Rectangle playLocallyHitbox = new Rectangle(250, 220, 300, 60);
    private final Rectangle playOnlineHitbox = new Rectangle(250, 300, 300, 60);
    private final Rectangle leaveHitbox = new Rectangle(250, 380, 300, 60);

    private final Rectangle windowBounds = new Rectangle(180, 110, 440, 380);
    private final Rectangle hostGameHitbox = new Rectangle(220, 180, 360, 50);
    private final Rectangle gameCodeHitbox = new Rectangle(220, 280, 160, 45);
    private final Rectangle joinGameHitbox = new Rectangle(390, 280, 190, 45);
    private final Rectangle closeHitbox = new Rectangle(220, 350, 360, 45);

    private boolean online = false;
    private boolean gameCodeFocused = false;
    private String gameCode = "";

    // 0: NIX, 1: Play locally, 2: Play online, 3: Host Game, 4: Join Game, 5: Back, 6: Game Code Box, 7: Leave
    private int hoveredButton = 0;

    EventListenerList listenerList = new EventListenerList();

    public Chess_MainMenu(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout()); // Set layout manager so child panels expand properly
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        // ALWAYS call super.paintComponent(g) first to handle standard background & child painting
        super.paintComponent(g);

        if (panelSelected == 1) {
            setBackground(defaultGray);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            FontMetrics titleMetrics = g.getFontMetrics();
            String title = "Chess";
            g.drawString(title, (getWidth() - titleMetrics.stringWidth(title)) / 2, 140);

            int arc = 20;

            g.setColor(defaultGreen);
            g.fillRoundRect(playLocallyHitbox.x, playLocallyHitbox.y, playLocallyHitbox.width, playLocallyHitbox.height, arc, arc);
            g.fillRoundRect(playOnlineHitbox.x, playOnlineHitbox.y, playOnlineHitbox.width, playOnlineHitbox.height, arc, arc);

            g.setColor(darkButtonGray);
            g.fillRoundRect(leaveHitbox.x, leaveHitbox.y, leaveHitbox.width, leaveHitbox.height, arc, arc);

            g.setColor(Color.WHITE);
            if (hoveredButton == 1) {
                g.drawRoundRect(playLocallyHitbox.x, playLocallyHitbox.y, playLocallyHitbox.width, playLocallyHitbox.height, arc, arc);
            }
            if (hoveredButton == 2) {
                g.drawRoundRect(playOnlineHitbox.x, playOnlineHitbox.y, playOnlineHitbox.width, playOnlineHitbox.height, arc, arc);
            }
            if (hoveredButton == 7) {
                g.drawRoundRect(leaveHitbox.x, leaveHitbox.y, leaveHitbox.width, leaveHitbox.height, arc, arc);
            }

            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fontMetrics = g.getFontMetrics();
            g.drawString("Play locally", playLocallyHitbox.x + (playLocallyHitbox.width - fontMetrics.stringWidth("Play locally")) / 2, playLocallyHitbox.y + 37);
            g.drawString("Play online", playOnlineHitbox.x + (playOnlineHitbox.width - fontMetrics.stringWidth("Play online")) / 2, playOnlineHitbox.y + 37);
            g.drawString("Leave", leaveHitbox.x + (leaveHitbox.width - fontMetrics.stringWidth("Leave")) / 2, leaveHitbox.y + 37);

            if (online) {
                paintOnlineWindow(g);
            }
        }
    }

    private void paintOnlineWindow(Graphics g) {
        int Arc = 20;
        int elementArc = 10;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(defaultGray);
        g.fillRoundRect(windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height, Arc, Arc);
        g.setColor(Color.WHITE);
        g.drawRoundRect(windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height, Arc, Arc);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g.getFontMetrics();
        String title = "Play Online";
        g.drawString(title, windowBounds.x + (windowBounds.width - fontMetrics.stringWidth(title)) / 2, windowBounds.y + 45);

        g.setColor(defaultGreen);
        g.fillRoundRect(hostGameHitbox.x, hostGameHitbox.y, hostGameHitbox.width, hostGameHitbox.height, elementArc, elementArc);
        if (hoveredButton == 3) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(hostGameHitbox.x, hostGameHitbox.y, hostGameHitbox.width, hostGameHitbox.height, elementArc, elementArc);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String hostText = "Host Game";
        g.drawString(hostText, hostGameHitbox.x + (hostGameHitbox.width - fontMetrics.stringWidth(hostText)) / 2, hostGameHitbox.y + 31);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fontMetrics = g.getFontMetrics();
        g.setColor(new Color(180, 180, 180));
        g.drawString("— OR —", windowBounds.x + (windowBounds.width - fontMetrics.stringWidth("— OR —")) / 2, 258);

        g.setColor(darkPanelGray);
        g.fillRoundRect(gameCodeHitbox.x, gameCodeHitbox.y, gameCodeHitbox.width, gameCodeHitbox.height, elementArc, elementArc);
        if (gameCodeFocused || hoveredButton == 6) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(gameCodeHitbox.x, gameCodeHitbox.y, gameCodeHitbox.width, gameCodeHitbox.height, elementArc, elementArc);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameCode.isEmpty() && !gameCodeFocused) {
            g.setColor(new Color(150, 150, 150));
            g.drawString("Game Code", gameCodeHitbox.x + 12, gameCodeHitbox.y + 28);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(gameCode + (gameCodeFocused ? "|" : ""), gameCodeHitbox.x + 12, gameCodeHitbox.y + 28);
        }

        g.setColor(defaultGreen);
        g.fillRoundRect(joinGameHitbox.x, joinGameHitbox.y, joinGameHitbox.width, joinGameHitbox.height, elementArc, elementArc);
        if (hoveredButton == 4) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(joinGameHitbox.x, joinGameHitbox.y, joinGameHitbox.width, joinGameHitbox.height, elementArc, elementArc);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String joinText = "Join Game";
        g.drawString(joinText, joinGameHitbox.x + (joinGameHitbox.width - fontMetrics.stringWidth(joinText)) / 2, joinGameHitbox.y + 28);

        g.setColor(darkButtonGray);
        g.fillRoundRect(closeHitbox.x, closeHitbox.y, closeHitbox.width, closeHitbox.height, elementArc, elementArc);
        if (hoveredButton == 5) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(closeHitbox.x, closeHitbox.y, closeHitbox.width, closeHitbox.height, elementArc, elementArc);
        }
        g.setFont(new Font("Arial", Font.BOLD, 16));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String backText = "Back";
        g.drawString(backText, closeHitbox.x + (closeHitbox.width - fontMetrics.stringWidth(backText)) / 2, closeHitbox.y + 28);
    }

    private void switchToPanel(JPanel targetPanel) {
        removeAll();
        add(targetPanel, BorderLayout.CENTER);
        targetPanel.setVisible(true);
        setPreferredSize(targetPanel.getPreferredSize());
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        revalidate();
        repaint();
        fireActionPerformed("resize");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Point p = e.getPoint();
            if (online) {
                if (hostGameHitbox.contains(p)) {
                    System.out.println("Host Game / Create new Game selected");
                    clientPanel.Host();

                } else if (joinGameHitbox.contains(p)) {
                    System.out.println("Join Game selected with code: " + gameCode);
                    clientPanel.Join(gameCode);

                } else if (gameCodeHitbox.contains(p)) {
                    gameCodeFocused = true;
                    requestFocusInWindow();
                } else if (closeHitbox.contains(p)) {
                    online = false;
                    gameCodeFocused = false;
                } else if (!windowBounds.contains(p)) {
                    online = false;
                    gameCodeFocused = false;
                } else {
                    gameCodeFocused = false;
                }
            } else {
                if (playLocallyHitbox.contains(p)) {
                    panelSelected = 2;
                    switchToPanel(chess_singleplayer_panel);
                } else if (playOnlineHitbox.contains(p)) {
                    online = true;
                } else if (leaveHitbox.contains(p)) {
                    fireActionPerformed("return");
                }
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (panelSelected != 1) return;

        Point p = e.getPoint();

        if (online) {
            if (hostGameHitbox.contains(p)) {
                hoveredButton = 3;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (joinGameHitbox.contains(p)) {
                hoveredButton = 4;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (closeHitbox.contains(p)) {
                hoveredButton = 5;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (gameCodeHitbox.contains(p)) {
                hoveredButton = 6;
                setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            } else {
                hoveredButton = 0;
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        } else {
            if (playLocallyHitbox.contains(p)) {
                hoveredButton = 1;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (playOnlineHitbox.contains(p)) {
                hoveredButton = 2;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (leaveHitbox.contains(p)) {
                hoveredButton = 7;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                hoveredButton = 0;
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (online && gameCodeFocused) {
            char c = e.getKeyChar();
            if (c == KeyEvent.VK_BACK_SPACE) {
                if (!gameCode.isEmpty()) {
                    gameCode = gameCode.substring(0, gameCode.length() - 1);
                }
            } else if (c == KeyEvent.VK_ENTER) {
                System.out.println("Join Game selected with code: " + gameCode);
                panelSelected = 3;
                switchToPanel(clientPanel);
                clientPanel.Join(gameCode);
            } else if (Character.isLetterOrDigit(c) && gameCode.length() < 10) {
                gameCode += c;
            }
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

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if("return".equals(e.getActionCommand())) {
            removeAll();
            panelSelected = 1;
            setPreferredSize(new Dimension(800, 600));
            fireActionPerformed("resize");
        }
        if("Joined_Game".equals(e.getActionCommand())) {
            panelSelected = 3;
            switchToPanel(clientPanel);
        }
    }
}