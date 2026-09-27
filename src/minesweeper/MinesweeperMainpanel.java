package minesweeper;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MinesweeperMainpanel extends JPanel {
    Gamepanel gamepanel;
    EventListenerList listenerList = new EventListenerList();

    public MinesweeperMainpanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        gamepanel = new Gamepanel();
        this.setPreferredSize(new Dimension((int) gamepanel.getPreferredSize().getWidth(),(int) gamepanel.getPreferredSize().getHeight()+30));


        JRadioButton easy = new JRadioButton("Easy");
        JRadioButton medium = new JRadioButton("Medium");
        JRadioButton hard = new JRadioButton("Hard");
        JButton returnBtn = new JButton("Return");
        JButton reset = new JButton("Reset");


        easy.addActionListener(_ -> {
            gamepanel.diff = difficulty.EASY;
            reset();
        });
        medium.addActionListener(_ -> {
            gamepanel.diff = difficulty.MEDIUM;
            reset();
        });
        hard.addActionListener(_ -> {
            gamepanel.diff = difficulty.HARD;
            reset();
        });
        reset.addActionListener(_ -> gamepanel.prepareNewBoard());
        returnBtn.addActionListener(_ -> fireActionPerformed("return"));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(easy);
        buttonGroup.add(medium);
        buttonGroup.add(hard);
        easy.setSelected(true);
        reset.setPreferredSize(new Dimension(100,20));
        returnBtn.setPreferredSize(new Dimension(100,20));
        add(reset);
        add(returnBtn);

        easy.setBackground(new Color(94, 203, 226));
        medium.setBackground(new Color(94, 203, 226));
        hard.setBackground(new Color(94, 203, 226));

        JLabel difficultylabel = new JLabel("Difficulty:");
        gamepanel.add(difficultylabel);
        gamepanel.add(easy);
        gamepanel.add(medium);
        gamepanel.add(hard);
        gamepanel.addTimerlabel();

        add(gamepanel);

        setVisible(true);
    }
    private void fireActionPerformed(String action) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
            }
        }
    }
    public void reset() {
        gamepanel.prepareNewBoard();
        this.setPreferredSize(new Dimension((int) gamepanel.getPreferredSize().getWidth(),(int) gamepanel.getPreferredSize().getHeight()+30));
        fireActionPerformed("resize");
    }
}
