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

            getContentPane().removeAll();
            getContentPane().add(mainPanel);
            getContentPane().revalidate();
            pack();
            setLocationRelativeTo(null);
            getContentPane().repaint();
        }

        if(e.getActionCommand().equals("Space Invaders")) {
            getContentPane().removeAll();
            getContentPane().add(panel);
            getContentPane().revalidate();
            pack();
            setLocationRelativeTo(null);
            getContentPane().repaint();
            panel.requestFocusInWindow();
            panel.reset();
        }
    }
}