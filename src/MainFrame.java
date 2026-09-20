import Pong.Pong_mainPanel;
import Snake.Snake_mainPanel;
import SpaceInvaders.SpaceInvaders_mainPanel;
import chess.Chess_Singleplayer_panel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    SpaceInvaders_mainPanel spaceInvaders_panel = new SpaceInvaders_mainPanel(this);
    MainPanel mainPanel = new MainPanel(this);
    Snake_mainPanel snake_panel = new Snake_mainPanel(this);
    Pong_mainPanel pong_panel = new Pong_mainPanel(this);
    Chess_Singleplayer_panel chess_Singleplayer_panel = new Chess_Singleplayer_panel(this);

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
            setTitle("Game Selection");
            renderPanel(mainPanel);
        }

        if (e.getActionCommand().equals("Space Invaders")) {
            setTitle("Space Invaders");
            renderPanel(spaceInvaders_panel);
            spaceInvaders_panel.reset();
        }
        if (e.getActionCommand().equals("Snake")) {
            setTitle("Snake");
            renderPanel(snake_panel);
            snake_panel.reset();
        }
        if (e.getActionCommand().equals("Pong")) {
            setTitle("Pong");
            renderPanel(pong_panel);
            pong_panel.reset();
        }
        if (e.getActionCommand().equals("Chess")) {
            setTitle("Chess");
            renderPanel(chess_Singleplayer_panel);
            chess_Singleplayer_panel.reset();
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