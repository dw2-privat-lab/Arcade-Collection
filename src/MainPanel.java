import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
    protected EventListenerList listenerList = new EventListenerList();

    public MainPanel(ActionListener actionListener) {
        JLabel label = new JLabel("Select a Game:");
        add(label);

        listenerList.add(ActionListener.class, actionListener);
        Button spaceInvaders = new Button("Space Invaders");
        spaceInvaders.addActionListener(_ -> fireActionPerformed("Space Invaders"));
        add(spaceInvaders);
        Button snake = new Button("Snake");
        snake.addActionListener(_ -> fireActionPerformed("Snake"));
        add(snake);
        Button pong = new Button("Pong");
        pong.addActionListener(_ -> fireActionPerformed("Pong"));
        add(pong);
        Button chess = new Button("Chess");
        chess.addActionListener(_ -> fireActionPerformed("Chess"));
        add(chess);
    }

    protected void fireActionPerformed(String command) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
            }
        }
    }
}
