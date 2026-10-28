package minesweeper;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinesweeperMainpanel extends JPanel {
    private static final Color backgroundColor = new Color(20, 23, 26);
    private static final int controlsHeight = 64;
    private static final Color panelColor = new Color(35, 39, 42);
    private static final Color selectedColor = new Color(100, 132, 105);
    private static final Color textColor = new Color(225, 228, 226);
    private static final Font uiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private final EventListenerList listenerList = new EventListenerList();
    private final Gamepanel gamepanel;
    private final Rectangle easyHitbox = new Rectangle();
    private final Rectangle mediumHitbox = new Rectangle();
    private final Rectangle hardHitbox = new Rectangle();
    private final Rectangle resetHitbox = new Rectangle();
    private final Rectangle returnHitbox = new Rectangle();
    private difficulty selectedDifficulty = difficulty.EASY;
    private int hoveredButton = -1;

    public MinesweeperMainpanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        gamepanel = new Gamepanel();
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        add(createControlBar(), BorderLayout.NORTH);
        add(gamepanel, BorderLayout.CENTER);
        updatePreferredSize();
    }

    private JPanel createControlBar() {
        JPanel controls = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                g.setColor(textColor);
                g.drawString("MINESWEEPER", 16, 27);
                paintButton(g, easyHitbox, "EASY", difficulty.EASY);
                paintButton(g, mediumHitbox, "MEDIUM", difficulty.MEDIUM);
                paintButton(g, hardHitbox, "HARD", difficulty.HARD);
                paintButton(g, resetHitbox, "RESET", null);
                paintButton(g, returnHitbox, "RETURN", null);
                g.dispose();
            }
        };
        controls.setPreferredSize(new Dimension(100, controlsHeight));
        controls.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                layoutHitboxes(controls.getWidth() > 0 ? controls.getWidth() : getPreferredSize().width);
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                if (easyHitbox.contains(e.getPoint())) setDifficulty(difficulty.EASY);
                else if (mediumHitbox.contains(e.getPoint())) setDifficulty(difficulty.MEDIUM);
                else if (hardHitbox.contains(e.getPoint())) setDifficulty(difficulty.HARD);
                else if (resetHitbox.contains(e.getPoint())) gamepanel.prepareNewBoard();
                else if (returnHitbox.contains(e.getPoint())) fireActionPerformed("return");
                controls.repaint();
            }
        });
        controls.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                layoutHitboxes(controls.getWidth() > 0 ? controls.getWidth() : getPreferredSize().width);
                hoveredButton = buttonAt(e.getPoint());
                controls.setCursor(Cursor.getPredefinedCursor(hoveredButton == -1 ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                controls.repaint();
            }
        });
        controls.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutHitboxes(controls.getWidth());
            }
        });
        return controls;
    }

    private void layoutHitboxes(int width) {
        int height = 22;
        int groupWidth = 64 + 4 + 84 + 4 + 64;
        int left = Math.max(8, (width - groupWidth) / 2);
        easyHitbox.setBounds(left, 38, 64, height);
        mediumHitbox.setBounds(left + 68, 38, 84, height);
        hardHitbox.setBounds(left + 156, 38, 64, height);
        resetHitbox.setBounds(Math.max(138, width - 156), 10, 66, height);
        returnHitbox.setBounds(Math.max(210, width - 86), 10, 76, height);
    }

    private int buttonAt(Point point) {
        if (easyHitbox.contains(point)) return 0;
        if (mediumHitbox.contains(point)) return 1;
        if (hardHitbox.contains(point)) return 2;
        if (resetHitbox.contains(point)) return 3;
        if (returnHitbox.contains(point)) return 4;
        return -1;
    }

    private void paintButton(Graphics2D g, Rectangle hitbox, String label, difficulty difficulty) {
        boolean selected = difficulty != null && selectedDifficulty == difficulty;
        int button = buttonAt(new Point(hitbox.x + 1, hitbox.y + 1));
        boolean hovered = button == hoveredButton;
        g.setColor(selected ? selectedColor : hovered ? new Color(55, 62, 65) : panelColor);
        g.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        g.setColor(selected ? Color.WHITE : textColor);
        g.setFont(uiFont);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(label, hitbox.x + (hitbox.width - metrics.stringWidth(label)) / 2,
                hitbox.y + (hitbox.height + metrics.getAscent() - metrics.getDescent()) / 2);
    }

    public void reset() {
        gamepanel.prepareNewBoard();
        updatePreferredSize();
        fireActionPerformed("resize");
    }

    private void setDifficulty(difficulty nextDifficulty) {
        if (selectedDifficulty == nextDifficulty) return;
        selectedDifficulty = nextDifficulty;
        gamepanel.diff = nextDifficulty;
        gamepanel.prepareNewBoard();
        updatePreferredSize();
        fireActionPerformed("resize");
    }

    private void updatePreferredSize() {
        int width = Settings.getArraywidth(gamepanel.diff) * Settings.tilesize + Settings.Xoffset * 2;
        int height = Settings.getArrayheight(gamepanel.diff) * Settings.tilesize + 52 + Settings.Yoffset + controlsHeight;
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    private void fireActionPerformed(String action) {
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
        }
    }
}
