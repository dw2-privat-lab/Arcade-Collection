import javax.swing.*;

void main() {
    JFrame frame = new JFrame();
    RenderingPanel panel = new RenderingPanel();
    frame.add(panel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    panel.run();
}
