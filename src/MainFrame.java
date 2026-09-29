import Pong.Pong_mainPanel;
import Snake.Snake_mainPanel;
import SpaceInvaders.SpaceInvaders_mainPanel;
import chess.Chess_MainMenu;
import connect4.connect4MainPanel;
import minesweeper.MinesweeperMainpanel;
import twentyfourtyeight.TwentyfourtyeightPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    SpaceInvaders_mainPanel spaceInvaders_panel = new SpaceInvaders_mainPanel(this);
    MainPanel mainPanel = new MainPanel(this);
    Snake_mainPanel snake_panel = new Snake_mainPanel(this);
    Pong_mainPanel pong_panel = new Pong_mainPanel(this);
    Chess_MainMenu Chess =new Chess_MainMenu(this);
    MinesweeperMainpanel minesweeper = new MinesweeperMainpanel(this);
    connect4MainPanel connect4MainPanel = new connect4MainPanel(this);
    TwentyfourtyeightPanel g = new TwentyfourtyeightPanel();
    public MainFrame() {
        add(g);
        stopAllRunning();

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
        }else if (e.getActionCommand().equals("resize")){
            pack();
            if(e.getSource() == minesweeper){
                setLocationRelativeTo(null);
            }
        }
        else {
            stopAllRunning();
            setTitle(e.getActionCommand());
            if (e.getActionCommand().equals("Space Invaders")) {
                spaceInvaders_panel.reset();
                renderPanel(spaceInvaders_panel);
            }
            if (e.getActionCommand().equals("Snake")) {
                snake_panel.reset();
                renderPanel(snake_panel);
            }
            if (e.getActionCommand().equals("Pong")) {
                pong_panel.reset();
                renderPanel(pong_panel);
            }
            if (e.getActionCommand().equals("Chess")) {
                renderPanel(Chess);
            }
            if (e.getActionCommand().equals("Minesweeper")) {
                minesweeper.reset();
                renderPanel(minesweeper);
            }
            if (e.getActionCommand().equals("Connect4")) {
                renderPanel(connect4MainPanel);
            }
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
    private void stopAllRunning(){
        spaceInvaders_panel.pause();
        snake_panel.pause();
        pong_panel.pause();
    }
}