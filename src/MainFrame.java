import javax.swing.*;

public class MainFrame extends JFrame {
    SpaceInvaders_mainPanel panel = new SpaceInvaders_mainPanel();
    public MainFrame() {
        add(panel);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        runselectedGame();
    }
    private void runselectedGame(){
        panel.run();
    }
}
