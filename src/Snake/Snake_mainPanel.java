package Snake;

import commonTools.ImageLoader;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;

public class Snake_mainPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int segmentInset = 2;
    private final int scale = 4;
    private final Snake snake = new Snake();
    private final EventListenerList listenerList = new EventListenerList();
    private final Image gameOver = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/gameOver.png");
    private final Image pausedImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/paused.png");
    private final Image quitImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/quit.png");
    private final Image resumeImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/resume.png");
    private final Image returnImg = ImageLoader.loadImage(this.getClass(), "SpaceInvaders/return.png");
    private final Timer timer;
    private Apple apples = new Apple(snake.getSnakeList());
    private int score = 0;
    private boolean paused = false;
    private boolean won = false;
    private int highlighted = -1;

    public Snake_mainPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        setFocusable(true);
        setBackground(GameOptions.backgroundColor);
        setPreferredSize(new Dimension(GameOptions.WIDTH, GameOptions.HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        timer = new Timer(GameOptions.gameSpeed, _ -> {
            if (!paused && !snake.isGameOver() && !won) tick();
            repaint();
        });
        timer.start();
    }

    public void pause() {
        paused = true;
    }

    public void tick() {
        snake.moveSnake();
        if (snake.isGameOver()) {
            paused = true;
            return;
        }

        int appleNumber = apples.getTouchingApple(snake.getSnakeList().getLast());
        if (appleNumber >= 0) {
            apples.getApples().remove(appleNumber);
            score++;
            snake.growSnake();
            if (!apples.addApple(snake.getSnakeList())) {
                won = true;
                paused = true;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(GameOptions.backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        int margin = GameOptions.playfieldMargin;
        int boardWidth = GameOptions.WIDTH - margin * 2;
        int boardHeight = GameOptions.HEIGHT - margin * 2;
        g.setColor(GameOptions.frameColor);
        g.fillRect(margin - 4, margin - 4, boardWidth + 8, boardHeight + 8);
        g.setColor(GameOptions.boardColor);
        g.fillRect(margin, margin, boardWidth, boardHeight);
        g.setColor(GameOptions.gridColor);
        for (int x = margin + GameOptions.tileSize; x < GameOptions.WIDTH - margin; x += GameOptions.tileSize) {
            g.drawLine(x, margin, x, GameOptions.HEIGHT - margin);
        }
        for (int y = margin + GameOptions.tileSize; y < GameOptions.HEIGHT - margin; y += GameOptions.tileSize) {
            g.drawLine(margin, y, GameOptions.WIDTH - margin, y);
        }

        paintApples(g);
        paintSnake(g);
        paintScore(g);
        if (paused || snake.isGameOver() || won) paintOverlay(g);
        g.dispose();
    }

    private void paintApples(Graphics g) {
        for (Rectangle apple : apples.getApples()) {
            g.setColor(GameOptions.appleColor);
            g.fillRect(apple.x + 5, apple.y + 5, apple.width - 10, apple.height - 10);
        }
    }

    private void paintSnake(Graphics g) {
        java.util.ArrayList<Rectangle> segments = snake.getSnakeList();
        int size = GameOptions.tileSize - segmentInset * 2;
        int offset = segmentInset;
        for (int i = 0; i < segments.size(); i++) {
            Rectangle segment = segments.get(i);
            g.setColor(i == segments.size() - 1 ? GameOptions.snakeHeadColor : GameOptions.snakeBaseColor);
            g.fillRect(segment.x + offset, segment.y + offset, size, size);
        }
    }

    private void paintScore(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        g.setColor(new Color(12, 15, 17));
        g.fillRect(22, 18, 128, 30);
        g.setColor(new Color(210, 214, 205));
        g.drawString("Score", 32, 39);
        g.drawString(String.valueOf(score), 93, 39);
    }

    private void paintOverlay(Graphics g) {
        g.setColor(new Color(8, 12, 15, 190));
        g.fillRect(0, 0, getWidth(), getHeight());
        Image title = won ? gameOver : snake.isGameOver() ? gameOver : pausedImg;
        int titleWidth = won ? 61 : snake.isGameOver() ? 61 : 43;
        int titleX = (getWidth() - titleWidth * scale) / 2;
        g.drawImage(title, titleX, 99, titleWidth * scale, 11 * scale, this);
        if (won) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            drawCentered(g, "YOU WIN", getWidth() / 2, 151);
        }
        paintMenuButton(g, quitImg, 1, getWidth() / 2 - 15 * scale, 100 + 12 * scale, 9 * scale);
        paintMenuButton(g, resumeImg, 2, (int) (getWidth() / 2.0 - 4.5 * scale), 100 + 12 * scale, 9 * scale);
        paintMenuButton(g, returnImg, 3, getWidth() / 2 + 6 * scale, 100 + 12 * scale, 9 * scale);
    }

    private void paintMenuButton(Graphics g, Image image, int button, int x, int y, int size) {
        if (highlighted == button) {
            g.setColor(new Color(255, 255, 255, 35));
            g.fillRect(x - 5, y - 5, size + 10, size + 10);
            g.drawImage(image, x - scale, y - scale, size + 2 * scale, size + 2 * scale, this);
        } else {
            g.drawImage(image, x, y, size, size, this);
        }
    }

    private void drawCentered(Graphics g, String text, int centerX, int y) {
        g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, y);
    }

    public void reset() {
        snake.reset();
        paused = false;
        won = false;
        highlighted = -1;
        score = 0;
        apples = new Apple(snake.getSnakeList());
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> snake.setNextDirection(directions.UP);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> snake.setNextDirection(directions.DOWN);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> snake.setNextDirection(directions.LEFT);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> snake.setNextDirection(directions.RIGHT);
            case KeyEvent.VK_ESCAPE -> {
                if (snake.isGameOver() || won) reset();
                else paused = !paused;
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        highlighted = -1;
        if (!paused && !snake.isGameOver() && !won) return;
        for (int button = 1; button <= 3; button++) {
            if (getMenuBounds(button).contains(e.getPoint())) highlighted = button;
        }
        if (highlighted != -1)
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }

    private Rectangle getMenuBounds(int button) {
        int size = 9 * scale;
        int x = switch (button) {
            case 1 -> getWidth() / 2 - 15 * scale;
            case 2 -> (int) (getWidth() / 2.0 - 4.5 * scale);
            default -> getWidth() / 2 + 6 * scale;
        };
        return new Rectangle(x - 5, 100 + 12 * scale - 5, size + 10, size + 10);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 || (!paused && !snake.isGameOver() && !won)) return;
        for (int button = 1; button <= 3; button++) {
            if (!getMenuBounds(button).contains(e.getPoint())) continue;
            if (button == 1) System.exit(1);
            if (button == 2) {
                if (snake.isGameOver() || won) reset();
                else paused = false;
            }
            if (button == 3) fireActionPerformed();
            repaint();
            return;
        }
    }

    private void fireActionPerformed() {
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "return"));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        highlighted = -1;
        repaint();
    }
}
