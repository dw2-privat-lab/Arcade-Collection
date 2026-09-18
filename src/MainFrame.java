import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    SpaceInvaders_mainPanel panel = new SpaceInvaders_mainPanel(this);
    MainPanel mainPanel = new MainPanel(this);

    public MainFrame() {
        add(mainPanel);

        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        runselectedGame();
    }

    private void runselectedGame() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("return")) {
            // 1. Alles vom ContentPane entfernen
            getContentPane().removeAll();
            // 2. Neues Panel zum ContentPane hinzufügen
            getContentPane().add(mainPanel);

            // 3. Layout neu berechnen
            getContentPane().revalidate();

            // 4. Größe anpassen
            setResizable(true);
            pack();
            setResizable(false);

            // 5. Zentrieren und neu zeichnen
            setLocationRelativeTo(null);
            getContentPane().repaint();
        }

        if(e.getActionCommand().equals("Space Invaders")) {
            // 1. Alles vom ContentPane entfernen
            getContentPane().removeAll();
            // 2. Neues Panel zum ContentPane hinzufügen
            getContentPane().add(panel);

            // 3. Layout neu berechnen
            getContentPane().revalidate();

            // 4. Größe anpassen
            setResizable(true);
            pack();
            setResizable(false);

            // 5. Zentrieren und neu zeichnen
            setLocationRelativeTo(null);
            getContentPane().repaint();

            // 6. WICHTIG: Fokus anfordern, damit der KeyListener (Steuerung) funktioniert!
            panel.requestFocusInWindow();
            panel.reset();
        }
    }
}