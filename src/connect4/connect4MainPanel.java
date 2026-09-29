package connect4;

import SoundTools.SoundPlayer;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class connect4MainPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener,ActionListener {
    int menu = 1;
    int hoveredButton = 1;

    Rectangle btnLocally = new Rectangle(250, 220, 300, 60);
    Rectangle btnOnline  = new Rectangle(250, 300, 300, 60);
    Rectangle btnLeave   = new Rectangle(250, 380, 300, 60);

    private final Rectangle windowBounds    = new Rectangle(180, 110, 440, 390);
    private final Rectangle hostGameHitbox  = new Rectangle(220, 170, 360, 45);

    private final Rectangle ipAddressHitbox = new Rectangle(220, 245, 260, 45);
    private final Rectangle gameCodeHitbox  = new Rectangle(490, 245, 90, 45);
    private final Rectangle HostIpAddressHitbox = new Rectangle(220, 245, 360, 45);

    private final Rectangle joinGameHitbox  = new Rectangle(220, 305, 360, 45);
    private final Rectangle closeHitbox     = new Rectangle(220, 365, 360, 45);

    RenderMessage renderMessage = new RenderMessage(this);
    Thread ErrorRepaintThread = new Thread(renderMessage);

    Color c4DarkBlue = new Color(15, 30, 90);
    Color c4BoardBlue = new Color(30, 60, 160);
    Color c4Yellow = new Color(255, 215, 0);
    Color c4Red = new Color(220, 50, 50);

    EventListenerList listenerList = new EventListenerList();

    private boolean ipAddressFocussed = false;
    private String ipAddress = "";
    private boolean gameCodeFocused = false;
    private String gameCode = "";

    connect4GamePanel gamePanel = new connect4GamePanel(this);

    public connect4MainPanel(ActionListener actionListener) {
        setPreferredSize(new Dimension(800, 600));
        listenerList.add(ActionListener.class, actionListener);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        ErrorRepaintThread.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 20;

        g.setColor(c4DarkBlue);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (menu == 1) {
            g.setColor(c4Yellow);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("Connect4", (getWidth() - fm.stringWidth("Connect4")) / 2, 140);

            g.setColor(c4BoardBlue);
            fillRoundRect(btnLocally, arc, arc, g);
            fillRoundRect(btnOnline, arc, arc, g);

            g.setColor(c4Red);
            fillRoundRect(btnLeave, arc, arc, g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            fm = g.getFontMetrics();
            g.drawString("Play Locally", btnLocally.x + (btnLocally.width - fm.stringWidth("Play Locally")) / 2, btnLocally.y + 37);
            g.drawString("Play Online", btnOnline.x + (btnOnline.width - fm.stringWidth("Play Online")) / 2, btnOnline.y + 37);
            g.drawString("Leave", btnLeave.x + (btnLeave.width - fm.stringWidth("Leave")) / 2, btnLeave.y + 37);

            if (hoveredButton == 1) {
                g.setColor(c4Yellow);
                drawRoundRect(btnLocally, arc, arc, g);
            }
            if (hoveredButton == 2) {
                g.setColor(c4Yellow);
                drawRoundRect(btnOnline, arc, arc, g);
            }
            if (hoveredButton == 3) {
                g.setColor(Color.WHITE);
                drawRoundRect(btnLeave, arc, arc, g);
            }
        }
        else if (menu == 2) {
            paintOnlineWindow(g);
        }
        else if (menu==3){
            paintHostWindow(g);
        }
        if(!renderMessage.getMessages().isEmpty())
            paintErrors((Graphics2D) g);
    }

    private void paintErrors(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        for (int i = 0; i < 3; i++) {
            if(i<renderMessage.getMessages().size()){
                g2d.setColor(c4Red);
                g2d.fillRoundRect(530,550-i*55,260,45,10,10);
                g2d.setColor(Color.WHITE);

                String[] lines = renderMessage.getMessage(i).split("\n");
                int yOffset = 0;
                int yDistributionOffset =lines.length==1?28:18;

                for (String line : lines) {
                    g2d.drawString(line, 535, 550-i*55 + yOffset+yDistributionOffset);
                    yOffset += g2d.getFontMetrics().getHeight();
                }
            }
        }
    }

    private void paintOnlineWindow(Graphics g) {
        int Arc = 20;
        int elementArc = 10;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(c4DarkBlue);
        fillRoundRect(windowBounds, Arc, Arc,g);
        g.setColor(c4BoardBlue);
        drawRoundRect(windowBounds, Arc, Arc,g);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g.getFontMetrics();
        String title = "Play Online";
        g.setColor(c4Yellow);
        g.drawString(title, windowBounds.x + (windowBounds.width - fontMetrics.stringWidth(title)) / 2, windowBounds.y + 42);

        g.setColor(c4BoardBlue);
        fillRoundRect(hostGameHitbox, elementArc, elementArc,g);
        if (hoveredButton == 4) {
            g.setColor(c4Yellow);
            drawRoundRect(hostGameHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String hostText = "Host Game";
        g.drawString(hostText, hostGameHitbox.x + (hostGameHitbox.width - fontMetrics.stringWidth(hostText)) / 2, hostGameHitbox.y + 28);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fontMetrics = g.getFontMetrics();
        g.setColor(new Color(180, 180, 200));
        g.drawString("— OR —", windowBounds.x + (windowBounds.width - fontMetrics.stringWidth("— OR —")) / 2, 235);

        g.setColor(new Color(10, 20, 50));
        fillRoundRect(ipAddressHitbox, elementArc, elementArc,g);
        if (ipAddressFocussed || hoveredButton == 5) {
            g.setColor(c4Yellow);
            drawRoundRect(ipAddressHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        String textToDraw = ipAddress + (ipAddressFocussed ? "|" : "");
        int textWidth = fm.stringWidth(textToDraw);
        int maxVisibleWidth = ipAddressHitbox.width - 20;

        int textX = ipAddressHitbox.x + 10;
        if (textWidth > maxVisibleWidth) {
            textX -= (textWidth - maxVisibleWidth);
        }

        Shape originalClip = g.getClip();
        g.clipRect(ipAddressHitbox.x + 5, ipAddressHitbox.y, ipAddressHitbox.width - 10, ipAddressHitbox.height);

        if (ipAddress.isEmpty() && !ipAddressFocussed) {
            g.setColor(new Color(130, 140, 160));
            g.drawString("IP / Domain Address", ipAddressHitbox.x + 10, ipAddressHitbox.y + 27);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(textToDraw, textX, ipAddressHitbox.y + 27);
        }
        g.setClip(originalClip);

        g.setColor(new Color(10, 20, 50));
        fillRoundRect(gameCodeHitbox, elementArc, elementArc,g);
        if (gameCodeFocused || hoveredButton == 6) {
            g.setColor(c4Yellow);
            drawRoundRect(gameCodeHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        if (gameCode.isEmpty() && !gameCodeFocused) {
            g.setColor(new Color(130, 140, 160));
            g.drawString("Code", gameCodeHitbox.x + 10, gameCodeHitbox.y + 27);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(gameCode + (gameCodeFocused ? "|" : ""), gameCodeHitbox.x + 10, gameCodeHitbox.y + 27);
        }

        g.setColor(c4BoardBlue);
        fillRoundRect(joinGameHitbox, elementArc, elementArc,g);
        if (hoveredButton == 7) {
            g.setColor(c4Yellow);
            drawRoundRect(joinGameHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String joinText = "Join Game";
        g.drawString(joinText, joinGameHitbox.x + (joinGameHitbox.width - fontMetrics.stringWidth(joinText)) / 2, joinGameHitbox.y + 28);

        g.setColor(c4Red);
        fillRoundRect(closeHitbox, elementArc, elementArc,g);
        if (hoveredButton == 8) {
            g.setColor(Color.WHITE);
            drawRoundRect(closeHitbox, elementArc, elementArc,g);
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

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(c4DarkBlue);
        fillRoundRect(windowBounds, Arc, Arc,g);
        g.setColor(c4BoardBlue);
        drawRoundRect(windowBounds, Arc, Arc,g);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fontMetrics = g.getFontMetrics();
        String title = "Host Game";
        g.setColor(c4Yellow);
        g.drawString(title, windowBounds.x + (windowBounds.width - fontMetrics.stringWidth(title)) / 2, windowBounds.y + 42);

        g.setColor(c4BoardBlue);
        fillRoundRect(hostGameHitbox, elementArc, elementArc,g);
        if (hoveredButton == 4) {
            g.setColor(c4Yellow);
            drawRoundRect(hostGameHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String hostText = "Host server locally";
        g.drawString(hostText, hostGameHitbox.x + (hostGameHitbox.width - fontMetrics.stringWidth(hostText)) / 2, hostGameHitbox.y + 28);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fontMetrics = g.getFontMetrics();
        g.setColor(new Color(180, 180, 200));
        g.drawString("— OR —", windowBounds.x + (windowBounds.width - fontMetrics.stringWidth("— OR —")) / 2, 235);

        g.setColor(new Color(10, 20, 50));
        fillRoundRect(HostIpAddressHitbox, elementArc, elementArc,g);
        if (ipAddressFocussed || hoveredButton == 5) {
            g.setColor(c4Yellow);
            drawRoundRect(HostIpAddressHitbox, elementArc, elementArc,g);
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
            g.setColor(new Color(130, 140, 160));
            g.drawString("IP / Domain Address of Host ChessServer", HostIpAddressHitbox.x + 10, HostIpAddressHitbox.y + 27);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(textToDraw, textX, HostIpAddressHitbox.y + 27);
        }
        g.setClip(originalClip);

        g.setColor(c4BoardBlue);
        fillRoundRect(joinGameHitbox, elementArc, elementArc,g);
        if (hoveredButton == 7) {
            g.setColor(c4Yellow);
            drawRoundRect(joinGameHitbox, elementArc, elementArc,g);
        }
        g.setFont(new Font("Arial", Font.BOLD, 18));
        fontMetrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String joinText = "Host server on external server";
        g.drawString(joinText, joinGameHitbox.x + (joinGameHitbox.width - fontMetrics.stringWidth(joinText)) / 2, joinGameHitbox.y + 28);

        g.setColor(c4Red);
        fillRoundRect(closeHitbox, elementArc, elementArc,g);
        if (hoveredButton == 8) {
            g.setColor(Color.WHITE);
            drawRoundRect(closeHitbox, elementArc, elementArc,g);
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

    private void fireActionPerformed(String action) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        if (menu == 1) {
            if (btnLocally.contains(p)) hoveredButton = 1;
            else if (btnOnline.contains(p)) hoveredButton = 2;
            else if (btnLeave.contains(p)) hoveredButton = 3;
            else hoveredButton = 0;
        }
        if (menu == 2) {
            if (hostGameHitbox.contains(p)) hoveredButton = 4;
            else if (ipAddressHitbox.contains(p)) hoveredButton = 5;
            else if (gameCodeHitbox.contains(p)) hoveredButton = 6;
            else if (joinGameHitbox.contains(p)) hoveredButton = 7;
            else if (closeHitbox.contains(p)) hoveredButton = 8;
            else hoveredButton = 0;
        }
        if (menu == 3) {
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
            if (btnLocally.contains(p)) {
                gamePanel.reset();
                add(gamePanel);
                menu = 0;
                setPreferredSize(gamePanel.getPreferredSize());
                fireActionPerformed("resize");
                fireActionPerformed("Connect4 - Local Game");
            } else if (btnOnline.contains(p)) {
                menu = 2;
                hoveredButton = 0;
                fireActionPerformed("Connect4 - Online Options");
            } else if (btnLeave.contains(p)) {
                fireActionPerformed("return");
            }
        }
        else if (menu == 2) {
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
                if(!ipAddress.isEmpty()&&!gameCode.isEmpty())
                    gamePanel.joinGame(ipAddress, gameCode);
                else renderMessage.addMessage((ipAddress.isEmpty()?gameCode.isEmpty()?"Code & IP/Domain ":"IP/Domain ": "Code " )+"is empty",15);
            } else if (closeHitbox.contains(p)) {
                menu = 1;
                ipAddressFocussed = false;
                gameCodeFocused = false;
            } else {
                ipAddressFocussed = false;
                gameCodeFocused = false;
            }
        }
        else if (menu == 3) {
            if (!windowBounds.contains(p)) {
                menu = 1;
                ipAddressFocussed = false;
                gameCodeFocused = false;
            } else if (hostGameHitbox.contains(p)) {
                fireActionPerformed("Connect4 - Host Game");
                gamePanel.hostGameServer();
                gamePanel.joinAsHostGame("localhost");
            } else if (HostIpAddressHitbox.contains(p)) {
                ipAddressFocussed = true;
            } else if (joinGameHitbox.contains(p)) {
                fireActionPerformed("Connect4 - Online Game");
                if(!ipAddress.isEmpty())
                    gamePanel.joinAsHostGame(ipAddress);
                else renderMessage.addMessage("No IP/Domain entered",15);
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

    @Override
    public void keyTyped(KeyEvent e) {
        if (menu == 2) {
            char c = e.getKeyChar();

            if (ipAddressFocussed) {
                if (c == KeyEvent.VK_BACK_SPACE) {
                    if (!ipAddress.isEmpty()) {
                        ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                    }
                } else if (c == KeyEvent.VK_ENTER) {
                    gameCodeFocused = true;
                    ipAddressFocussed = false;
                } else if ((Character.isLetterOrDigit(c) || c == '.' || c == '-') && ipAddress.length() < 253) {
                    ipAddress += c;
                }
                repaint();
            } else if (gameCodeFocused) {
                if (c == KeyEvent.VK_BACK_SPACE) {
                    if (!gameCode.isEmpty()) {
                        gameCode = gameCode.substring(0, gameCode.length() - 1);
                    }
                } else if (c == KeyEvent.VK_ENTER) {
                    if(!ipAddress.isEmpty()&&!gameCode.isEmpty())
                        gamePanel.joinGame(ipAddress, gameCode);
                    else renderMessage.addMessage((ipAddress.isEmpty()?gameCode.isEmpty()?"Code & IP/Domain ":"IP/Domain ": "Code " )+"is empty",15);
                } else if (Character.isLetterOrDigit(c) && gameCode.length() < 5) {
                    gameCode += c;
                }
                repaint();
            }
        }if (menu == 3) {
            char c = e.getKeyChar();

            if (ipAddressFocussed) {
                if (c == KeyEvent.VK_BACK_SPACE) {
                    if (!ipAddress.isEmpty()) {
                        ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                    }
                } else if (c == KeyEvent.VK_ENTER) {
                    if(!ipAddress.isEmpty())
                        gamePanel.joinAsHostGame(ipAddress);
                } else if ((Character.isLetterOrDigit(c) || c == '.' || c == '-') && ipAddress.length() < 253) {
                    ipAddress += c;
                }
                repaint();
            }
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == gamePanel) {
            if(e.getActionCommand().equals("RENDER ME")) {
                add(gamePanel);
                menu = 0;
                setPreferredSize(gamePanel.getPreferredSize());
                fireActionPerformed("resize");
            } else if (e.getActionCommand().equals("return")) {
                menu = 1;
                removeAll();
                setPreferredSize(new Dimension(800,600));
                fireActionPerformed("resize");
            }
        } else if (e.getActionCommand().equals("ERROR")) {
            renderMessage.addMessage(e.getSource().toString(),40);
        }
    }
}