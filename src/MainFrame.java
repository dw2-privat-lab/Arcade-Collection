import Snake.GamePanel;
import SpaceInvaders.SpaceInvaders_mainPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    SpaceInvaders_mainPanel spaceInvaders_panel = new SpaceInvaders_mainPanel(this);
    MainPanel mainPanel = new MainPanel(this);
    GamePanel snake_panel = new GamePanel(this);

    public MainFrame() {
        add(mainPanel);

        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("return")) {
            renderPanel(mainPanel);
        }

        if (e.getActionCommand().equals("Space Invaders")) {
            renderPanel(spaceInvaders_panel);
            spaceInvaders_panel.reset();
        }
        if (e.getActionCommand().equals("Snake")) {
            renderPanel(snake_panel);
            snake_panel.reset();
        }
    }
    private void renderPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        getContentPane().revalidate();
        pack();
        setLocationRelativeTo(null);
        getContentPane().repaint();
        panel.requestFocusInWindow();
    }
}