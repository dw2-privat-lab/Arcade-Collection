package chess;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Chess_MainMenu extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {

    private final Chess_Singleplayer_panel chess_singleplayer_panel = new Chess_Singleplayer_panel(this);
    private final ClientPanel clientPanel = new ClientPanel(this);

    Color defaultGray = new Color(53, 57, 57, 255);
    Color defaultGreen = new Color(72, 92, 59, 255);
    Color darkPanelGray = new Color(38, 41, 41, 255);
    Color darkButtonGray = new Color(80, 85, 85, 255);

    // 1: Main Menu, 2: Online Client, 3: Host Options
    private int panelSelected = 1;
    private int menu = 1;

    private final Rectangle playLocallyHitbox = new Rectangle(250, 220, 300, 60);
    private final Rectangle playOnlineHitbox  = new Rectangle(250, 300, 300, 60);
    private final Rectangle leaveHitbox       = new Rectangle(250, 380, 300, 60);

    private final Rectangle ipAddressHitbox     = new Rectangle(220, 245, 260, 45);
    private final Rectangle gameCodeHitbox      = new Rectangle(490, 245, 90, 45);
    private final Rectangle HostIpAddressHitbox = new Rectangle(220, 245, 360, 45);
    private final Rectangle joinGameHitbox      = new Rectangle(220, 305, 360, 45);
    private final Rectangle closeHitbox         = new Rectangle(220, 365, 360, 45);
    private final Rectangle windowBounds        = new Rectangle(180, 110, 440, 380);
    private final Rectangle hostGameHitbox      = new Rectangle(220, 170, 360, 50);

    private boolean ipAddressFocussed = false;
    private String ipAddress = "";
    private boolean gameCodeFocused = false;
    private String gameCode = "";

    // 0: NIX, 1: Play locally, 2: Play online, 3: Leave, 4: Host Game, 5: IP Box, 6: Code Box, 7: Join/Host Ext, 8: Back
    private int hoveredButton = 0;

    private ChessServer activeServer;

    EventListenerList listenerList = new EventListenerList();
    connect4.RenderMessage renderMessage = new connect4.RenderMessage(this);
    Thread ErrorRepaintThread = new Thread(renderMessage);

    public Chess_MainMenu(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        ErrorRepaintThread.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (panelSelected == 1) {
            if (menu == 1) {
                setBackground(defaultGray);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 42));
                FontMetrics titleMetrics = g.getFontMetrics();
                String title = "Chess";
                g.drawString(title, (getWidth() - titleMetrics.stringWidth(title)) / 2, 140);

                int arc = 20;

                g.setColor(defaultGreen);
                fillRoundRect(playLocallyHitbox, arc, arc, g);
                fillRoundRect(playOnlineHitbox, arc, arc, g);
                g.setColor(darkButtonGray);
                fillRoundRect(leaveHitbox, arc, arc, g);

                g.setColor(Color.WHITE);
                if (hoveredButton == 1) {
                    drawRoundRect(playLocallyHitbox, arc, arc, g);
                }
                if (hoveredButton == 2) {
                    drawRoundRect(playOnlineHitbox, arc, arc, g);
                }
                if (hoveredButton == 3) { // FIX: Was 7 previously
                    drawRoundRect(leaveHitbox, arc, arc, g);
                }

                g.setFont(new Font("Arial", Font.BOLD, 22));
                FontMetrics fontMetrics = g.getFontMetrics();
                g.drawString("Play locally", playLocallyHitbox.x + (playLocallyHitbox.width - fontMetrics.stringWidth("Play locally")) / 2, playLocallyHitbox.y + 37);
                g.drawString("Play online", playOnlineHitbox.x + (playOnlineHitbox.width - fontMetrics.stringWidth("Play online")) / 2, playOnlineHitbox.y + 37);
                g.drawString("Leave", leaveHitbox.x + (leaveHitbox.width - fontMetrics.stringWidth("Leave")) / 2, leaveHitbox.y + 37);
            } else if (menu == 2) {
                paintOnlineWindow(g);
            } else if (menu == 3) {
                paintHostWindow(g);
            }

            if (!renderMessage.getMessages().isEmpty()) {
                paintErrors((Graphics2D) g);
            }
        }
    }

    private void paintErrors(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        List<String> messagesSnapshot = new ArrayList<>(renderMessage.getMessages());
        for (int i = 0; i < 3 && i < messagesSnapshot.size(); i++) {
            g2d.setColor(defaultGreen);
            g2d.fillRoundRect(530, 550 - i * 55, 260, 45, 10, 10);
            g2d.setColor(Color.WHITE);

            String[] lines = messagesSnapshot.get(i).split("\n");
            int yOffset = 0;
            int yDistributionOffset = (lines.length == 1) ? 28 : 18;

            for (String line : lines) {
                g2d.drawString(line, 535, 550 - i * 55 + yOffset + yDistributionOffset);
                yOffset += g2d.getFontMetrics().getHeight();
            }
        }
    }

    private void paintOnlineWindow(Graphics g) {
        int Arc = 20;
        int elementArc = 10;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(defaultGray);
        fillRoundRect(windowBounds, Arc, Arc, g);
        g.setColor(Color.WHITE);
        drawRoundRect(windowBounds, Arc, Arc, g);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g.getFontMetrics();
        String title = "Play Online";
        g.drawString(title, windowBounds.x + (windowBounds.width - fontMetrics.stringWidth(title)) / 2, windowBounds.y + 42);

        g.setColor(defaultGreen);
        fillRoundRect(hostGameHitbox, elementArc, elementArc, g);
        if (hoveredButton == 4) {
            g.setColor(Color.WHITE);
            drawRoundRect(hostGameHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String hostText = "Host Game";
        g.drawString(hostText, hostGameHitbox.x + (hostGameHitbox.width - fontMetrics.stringWidth(hostText)) / 2, hostGameHitbox.y + 31);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        fontMetrics = g.getFontMetrics();
        g.setColor(new Color(180, 180, 180));
        g.drawString("— OR —", windowBounds.x + (windowBounds.width - fontMetrics.stringWidth("— OR —")) / 2, 238);

        g.setColor(darkPanelGray);
        fillRoundRect(ipAddressHitbox, elementArc, elementArc, g);
        if (ipAddressFocussed || hoveredButton == 5) {
            g.setColor(Color.WHITE);
            drawRoundRect(ipAddressHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        String ipTextToDraw = ipAddress + (ipAddressFocussed ? "|" : "");
        int textWidth = fm.stringWidth(ipTextToDraw);
        int maxVisibleWidth = ipAddressHitbox.width - 20;

        int textX = ipAddressHitbox.x + 10;
        if (textWidth > maxVisibleWidth) {
            textX -= (textWidth - maxVisibleWidth);
        }

        Shape originalClip = g.getClip();
        g.clipRect(ipAddressHitbox.x + 5, ipAddressHitbox.y, ipAddressHitbox.width - 10, ipAddressHitbox.height);

        if (ipAddress.isEmpty() && !ipAddressFocussed) {
            g.setColor(new Color(150, 150, 150));
            g.drawString("IP Address", ipAddressHitbox.x + 10, ipAddressHitbox.y + 28);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(ipTextToDraw, textX, ipAddressHitbox.y + 28);
        }
        g.setClip(originalClip);

        g.setColor(darkPanelGray);
        fillRoundRect(gameCodeHitbox, elementArc, elementArc, g);
        if (gameCodeFocused || hoveredButton == 6) {
            g.setColor(Color.WHITE);
            drawRoundRect(gameCodeHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        if (gameCode.isEmpty() && !gameCodeFocused) {
            g.setColor(new Color(150, 150, 150));
            g.drawString("Code", gameCodeHitbox.x + 10, gameCodeHitbox.y + 28);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(gameCode + (gameCodeFocused ? "|" : ""), gameCodeHitbox.x + 10, gameCodeHitbox.y + 28);
        }

        g.setColor(defaultGreen);
        fillRoundRect(joinGameHitbox, elementArc, elementArc, g);
        if (hoveredButton == 7) {
            g.setColor(Color.WHITE);
            drawRoundRect(joinGameHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String joinText = "Join Game";
        g.drawString(joinText, joinGameHitbox.x + (joinGameHitbox.width - fontMetrics.stringWidth(joinText)) / 2, joinGameHitbox.y + 28);

        g.setColor(darkButtonGray);
        fillRoundRect(closeHitbox, elementArc, elementArc, g);
        if (hoveredButton == 8) {
            g.setColor(Color.WHITE);
            drawRoundRect(closeHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 16));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String backText = "Back";
        g.drawString(backText, closeHitbox.x + (closeHitbox.width - fontMetrics.stringWidth(backText)) / 2, closeHitbox.y + 28);
    }

    private void paintHostWindow(Graphics g) {
        int Arc = 20;
        int elementArc = 10;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(defaultGray);
        fillRoundRect(windowBounds, Arc, Arc, g);
        g.setColor(Color.WHITE);
        drawRoundRect(windowBounds, Arc, Arc, g);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g.getFontMetrics();
        String title = "Host Game";
        g.drawString(title, windowBounds.x + (windowBounds.width - fontMetrics.stringWidth(title)) / 2, windowBounds.y + 42);

        g.setColor(defaultGreen);
        fillRoundRect(hostGameHitbox, elementArc, elementArc, g);
        if (hoveredButton == 4) {
            g.setColor(Color.WHITE);
            drawRoundRect(hostGameHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String hostText = "Host server locally";
        g.drawString(hostText, hostGameHitbox.x + (hostGameHitbox.width - fontMetrics.stringWidth(hostText)) / 2, hostGameHitbox.y + 32);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        fontMetrics = g.getFontMetrics();
        g.setColor(new Color(180, 180, 200));
        g.drawString("— OR —", windowBounds.x + (windowBounds.width - fontMetrics.stringWidth("— OR —")) / 2, 238);

        g.setColor(darkPanelGray);
        fillRoundRect(HostIpAddressHitbox, elementArc, elementArc, g);
        if (ipAddressFocussed || hoveredButton == 5) {
            g.setColor(Color.WHITE);
            drawRoundRect(HostIpAddressHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        String textToDraw = ipAddress + (ipAddressFocussed ? "|" : "");
        int textWidth = fm.stringWidth(textToDraw);
        int maxVisibleWidth = HostIpAddressHitbox.width - 20;

        int textX = HostIpAddressHitbox.x + 10;
        if (textWidth > maxVisibleWidth) {
            textX -= (textWidth - maxVisibleWidth);
        }

        Shape originalClip = g.getClip();
        g.clipRect(HostIpAddressHitbox.x + 5, HostIpAddressHitbox.y, HostIpAddressHitbox.width - 10, HostIpAddressHitbox.height);

        if (ipAddress.isEmpty() && !ipAddressFocussed) {
            g.setColor(new Color(150, 150, 150));
            g.drawString("IP / Domain Address of Host ChessServer", HostIpAddressHitbox.x + 10, HostIpAddressHitbox.y + 28);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(textToDraw, textX, HostIpAddressHitbox.y + 27);
        }
        g.setClip(originalClip);

        g.setColor(defaultGreen);
        fillRoundRect(joinGameHitbox, elementArc, elementArc, g);
        if (hoveredButton == 7) {
            g.setColor(Color.WHITE);
            drawRoundRect(joinGameHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String joinText = "Host server on external server";
        g.drawString(joinText, joinGameHitbox.x + (joinGameHitbox.width - fontMetrics.stringWidth(joinText)) / 2, joinGameHitbox.y + 28);

        g.setColor(darkButtonGray);
        fillRoundRect(closeHitbox, elementArc, elementArc, g);
        if (hoveredButton == 8) {
            g.setColor(Color.WHITE);
            drawRoundRect(closeHitbox, elementArc, elementArc, g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 16));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String backText = "Back";
        g.drawString(backText, closeHitbox.x + (closeHitbox.width - fontMetrics.stringWidth(backText)) / 2, closeHitbox.y + 28);
    }

    private void fillRoundRect(Rectangle r, int arcWidth, int arcHeight, Graphics g) {
        g.fillRoundRect(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
    }

    private void drawRoundRect(Rectangle r, int arcWidth, int arcHeight, Graphics g) {
        g.drawRoundRect(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
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
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        if (menu == 1) {
            if (playLocallyHitbox.contains(p)) hoveredButton = 1;
            else if (playOnlineHitbox.contains(p)) hoveredButton = 2;
            else if (leaveHitbox.contains(p)) hoveredButton = 3;
            else hoveredButton = 0;
        } else if (menu == 2) {
            if (hostGameHitbox.contains(p)) hoveredButton = 4;
            else if (ipAddressHitbox.contains(p)) hoveredButton = 5;
            else if (gameCodeHitbox.contains(p)) hoveredButton = 6;
            else if (joinGameHitbox.contains(p)) hoveredButton = 7;
            else if (closeHitbox.contains(p)) hoveredButton = 8;
            else hoveredButton = 0;
        } else if (menu == 3) {
            if (hostGameHitbox.contains(p)) hoveredButton = 4;
            else if (HostIpAddressHitbox.contains(p)) hoveredButton = 5;
            else if (joinGameHitbox.contains(p)) hoveredButton = 7;
            else if (closeHitbox.contains(p)) hoveredButton = 8;
            else hoveredButton = 0;
        }

        if (hoveredButton == 5 || hoveredButton == 6) {
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else if (hoveredButton != 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        if (menu == 1) {
            if (playLocallyHitbox.contains(p)) {
                chess_singleplayer_panel.reset();
                add(chess_singleplayer_panel);
                menu = 0;
                setPreferredSize(chess_singleplayer_panel.getPreferredSize());
                fireActionPerformed("resize");
                fireActionPerformed("Connect4 - Local Game");
            } else if (playOnlineHitbox.contains(p)) {
                menu = 2;
                hoveredButton = 0;
                fireActionPerformed("Connect4 - Online Options");
            } else if (leaveHitbox.contains(p)) {
                fireActionPerformed("return");
            }
        } else if (menu == 2) {
            if (!windowBounds.contains(p)) {
                menu = 1;
                ipAddressFocussed = false;
                gameCodeFocused = false;
            } else if (hostGameHitbox.contains(p)) {
                fireActionPerformed("Connect4 - Host Game Options");
                menu = 3;
                ipAddressFocussed = false;
                gameCodeFocused = false;
            } else if (ipAddressHitbox.contains(p)) {
                ipAddressFocussed = true;
                gameCodeFocused = false;
            } else if (gameCodeHitbox.contains(p)) {
                gameCodeFocused = true;
                ipAddressFocussed = false;
            } else if (joinGameHitbox.contains(p)) {
                executeJoin();
            } else if (closeHitbox.contains(p)) {
                menu = 1;
                ipAddressFocussed = false;
                gameCodeFocused = false;
            } else {
                ipAddressFocussed = false;
                gameCodeFocused = false;
            }
        } else if (menu == 3) {
            if (!windowBounds.contains(p)) {
                menu = 1;
                ipAddressFocussed = false;
            } else if (hostGameHitbox.contains(p)) {
                fireActionPerformed("Connect4 - Host Game");
                hostGameServer();
                clientPanel.host("localhost");
            } else if (HostIpAddressHitbox.contains(p)) {
                ipAddressFocussed = true;
            } else if (joinGameHitbox.contains(p)) {
                fireActionPerformed("Connect4 - Online Game");
                if (!ipAddress.isEmpty())
                    clientPanel.host(ipAddress);
                else renderMessage.addMessage("No IP/Domain entered", 15);
            } else if (closeHitbox.contains(p)) {
                menu = 2;
                fireActionPerformed("Connect4 - Online Options");
                ipAddressFocussed = false;
            } else {
                ipAddressFocussed = false;
            }
        }
        repaint();
    }

    private void executeJoin() {
        if (!ipAddress.isEmpty() && !gameCode.isEmpty()) {
            clientPanel.join(ipAddress, gameCode);
        } else {
            renderMessage.addMessage((ipAddress.isEmpty() ? gameCode.isEmpty() ? "Code & IP/Domain " : "IP/Domain " : "Code ") + "is empty", 15);
        }
    }

    private void hostGameServer() {
        if (activeServer != null) {
            activeServer.stopServer();
        }

        activeServer = new ChessServer();
        Thread gameServerThread = new Thread(activeServer);
        gameServerThread.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (ipAddressFocussed) {
            if ( c == KeyEvent.VK_BACK_SPACE) {
                if (!ipAddress.isEmpty()) {
                    ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                }
            } else if ( c == KeyEvent.VK_ENTER) {
                ipAddressFocussed=false;
                gameCodeFocused=true;

            } else if (!Character.isISOControl(c) && ipAddress.length() < 45) {
                ipAddress += c;
            }
            repaint();
        } else if (menu == 2 && gameCodeFocused) {
            if ( c == KeyEvent.VK_BACK_SPACE) {
                if (!gameCode.isEmpty()) {
                    gameCode = gameCode.substring(0, gameCode.length() - 1);
                }
            } else if ( c == KeyEvent.VK_ENTER) {
                executeJoin();
            } else if (Character.isLetterOrDigit(c) && gameCode.length() < 5) {
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
        if ("return".equals(e.getActionCommand())) {
            removeAll();
            panelSelected = 1;
            menu = 1;
            setPreferredSize(new Dimension(800, 600));
            fireActionPerformed("resize");
        }
        if ("Joined_Game".equals(e.getActionCommand())) {
            panelSelected = 3;
            switchToPanel(clientPanel);
        }
    }
}