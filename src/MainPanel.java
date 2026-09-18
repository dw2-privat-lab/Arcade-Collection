import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
    protected EventListenerList listenerList = new EventListenerList();

    public MainPanel(ActionListener actionListener) {
        setPreferredSize(new Dimension(100,100));
        setSize(100,100);
        listenerList.add(ActionListener.class, actionListener);
        Button button = new Button("Space Invaders");
        button.addActionListener(e -> {fireActionPerformed("Space Invaders");});
        add(button);

    }

    // Methode zum Registrieren eines Listeners
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    // Methode zum Entfernen eines Listeners
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    protected void fireActionPerformed(String command) {
        // Holt alle registrierten Listener und deren Typen als Array
        Object[] listeners = listenerList.getListenerList();

        ActionEvent event = null;

        // Das Array enthält abwechselnd den Klassentyp und die Listener-Instanz
        // Wir laufen von hinten nach vorne durch
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                // Event erst erstellen, wenn mindestens ein Listener existiert (Lazy Instantiation)
                if (event == null) {
                    event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
                }
                // Listener aufrufen
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }
}
