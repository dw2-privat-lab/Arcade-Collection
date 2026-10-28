import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final String[] games = {"Space Invaders", "Snake", "Pong", "Chess", "Minesweeper", "Connect4", "2048", "Tetris", "Pac-Man"};
    private final EventListenerList listenerList = new EventListenerList();
    private final List<Rectangle> buttons = new ArrayList<>();
    private int hovered = -1;

    public MainPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void layoutButtons() {
        buttons.clear();
        int buttonWidth = Math.min(440, getWidth() - 40);
        int buttonHeight = 38;
        int gap = 7;
        int totalHeight = games.length * buttonHeight + (games.length - 1) * gap;
        int x = (getWidth() - buttonWidth) / 2;
        int y = Math.max(90, (getHeight() - totalHeight) / 2 + 20);
        for (int i = 0; i < games.length; i++) {
            buttons.add(new Rectangle(x, y + i * (buttonHeight + gap), buttonWidth, buttonHeight));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        layoutButtons();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        String title = "Select a Game";
        g.drawString(title, (getWidth() - g.getFontMetrics().stringWidth(title)) / 2, 62);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        for (int i = 0; i < buttons.size(); i++) {
            Rectangle button = buttons.get(i);
            g.setColor(i == hovered ? Color.GRAY : Color.BLACK);
            g.fillRect(button.x, button.y, button.width, button.height);
            g.setColor(Color.WHITE);
            g.drawRect(button.x, button.y, button.width, button.height);
            g.drawString(games[i], button.x + 12, button.y + 25);
        }
    }

    private void select(int index) {
        if (index < 0 || index >= games.length) return;
        fireActionPerformed(games[index]);
    }

    private void fireActionPerformed(String command) {
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(e.getPoint())) {
                select(i);
                return;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hovered = -1;
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(e.getPoint())) {
                hovered = i;
                break;
            }
        }
        setCursor(Cursor.getPredefinedCursor(hovered < 0 ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }
}
