package PacMan;

import commonTools.ImageLoader;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class PacMan_MainPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final EventListenerList listenerList = new EventListenerList();
    private final Rectangle returnButton = new Rectangle();
    int tilesize = 15;
    int ghostSize = (int) (tilesize * 1.8);
    int ghostOffset = (ghostSize - tilesize) / 2;
    int temp = 0;
    int temp2 = 0;
    Board board = new Board();
    PacMan_player player;
    Image[][][] ghostSprites = new Image[5][4][2];
    Image boardImage = ImageLoader.loadImage(this.getClass(), "PacMan/board.png");
    Image[] scaredGhosts = new Image[4];
    Image[][] pacmanFrames = new Image[4][4];
    Image[] pacmanDeathFrames = new Image[11];
    Image[] items = new Image[10];
    int[][] renderBoard;
    boolean ghostFrame1 = true;
    int pacManFrame = 0;
    int pacmanDeathFrame = 0;
    int Yoffset = 40;
    Image scoreImg = ImageLoader.loadImage(this.getClass(), "Tetris/score.png");
    Image highscoreImg = ImageLoader.loadImage(this.getClass(), "Tetris/highscore.png");
    Image returnImg = ImageLoader.loadImage(this.getClass(), "Tetris/return.png");
    HashMap<Character, Image> numbers = new HashMap<>();
    private boolean returnHovered;

    public PacMan_MainPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        loadSprites();
        player = board.getPlayer();
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(28 * tilesize, 31 * tilesize + Yoffset));
        addMouseListener(this);
        addMouseMotionListener(this);

        Timer t = new Timer(33, _ -> {
            repaint();
            temp++;
            temp2++;
            if (temp >= 10) {
                ghostFrame1 = !ghostFrame1;
                temp = 0;
            }
            if (temp2 >= 3) {
                temp2 = 0;
                pacManFrame++;
                if (pacManFrame > 3) {
                    pacManFrame = 0;
                }
                if (board.playDeathAnimaton() && pacmanDeathFrame < pacmanDeathFrames.length) {
                    pacmanDeathFrame++;
                } else if (!board.playDeathAnimaton()) {
                    pacmanDeathFrame = 0;
                }
            }
        });
        t.start();
        bindDirection(KeyEvent.VK_LEFT, Directions.LEFT);
        bindDirection(KeyEvent.VK_RIGHT, Directions.RIGHT);
        bindDirection(KeyEvent.VK_UP, Directions.UP);
        bindDirection(KeyEvent.VK_DOWN, Directions.DOWN);
    }

    public void startGame() {
        board.startGame();
    }

    public void stopGame() {
        board.stopGame();
    }

    private void loadSprites() {
        for (int i = 0; i < 4; i++) {
            for (Directions dir : Directions.values()) {
                ghostSprites[i][dir.ordinal()][0] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/" + (i + 1) + "_" + dir + "_Frame" + 1 + ".png");
                ghostSprites[i][dir.ordinal()][1] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/" + (i + 1) + "_" + dir + "_Frame" + 2 + ".png");
            }
        }
        for (Directions dir : Directions.values()) {
            ghostSprites[4][dir.ordinal()][0] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/dead_" + dir + ".png");
            for (int i = 0; i < 4; i++) {
                pacmanFrames[dir.ordinal()][i] = ImageLoader.loadImage(this.getClass(), "PacMan/pacman/pacman_" + dir + "_" + i + ".png");
            }
        }
        for (int i = 0; i < 11; i++) {
            pacmanDeathFrames[i] = ImageLoader.loadImage(this.getClass(), "PacMan/pacman/pacman_death_" + i + ".png");
        }
        scaredGhosts[0] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_blue_0.png");
        scaredGhosts[1] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_blue_1.png");
        scaredGhosts[2] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_white_0.png");
        scaredGhosts[3] = ImageLoader.loadImage(this.getClass(), "PacMan/ghosts/scared_white_1.png");
        items[0] = ImageLoader.loadImage(this.getClass(), "PacMan/items/pacdot.png");
        items[1] = ImageLoader.loadImage(this.getClass(), "PacMan/items/powerPellet.png");
        for (int i = 0; i < 10; i++) {
            numbers.put((char) ('0' + i), ImageLoader.loadImage(this.getClass(), "Tetris/numbers/" + i + ".png"));
        }
    }

    private void bindDirection(int keyCode, Directions direction) {
        String key = "move-" + direction.name().toLowerCase();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), key);
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.setNextDirection(direction);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderBoard = board.getBoard();

        paintScore(g);
        drawReturnButton(g);
        g.drawImage(boardImage, 0, Yoffset, getWidth(), getHeight() - Yoffset, this);
        for (int i = 0; i < renderBoard.length; i++) {
            for (int j = 0; j < renderBoard[0].length; j++) {
                if (renderBoard[i][j] > 0) {
                    g.drawImage(items[renderBoard[i][j] - 1], j * tilesize, i * tilesize + Yoffset, tilesize, tilesize, this);
                }
            }
        }
        if (!board.playDeathAnimaton()) {
            g.drawImage(pacmanFrames[player.current_direction.ordinal()][pacManFrame], (int) (player.x * tilesize - ghostOffset), (int) (player.y * tilesize - ghostOffset + Yoffset), ghostSize, ghostSize, null);
        } else {
            if (pacmanDeathFrame < pacmanDeathFrames.length) {
                g.drawImage(pacmanDeathFrames[pacmanDeathFrame], (int) (player.x * tilesize - ghostOffset), (int) (player.y * tilesize + Yoffset - ghostOffset), ghostSize, ghostSize, null);
            }
        }
        if (!board.playDeathAnimaton())
            for (PacMan_Ghost ghost : board.getGhosts()) {
                if (ghost.isDead())
                    g.drawImage(ghostSprites[4][ghost.current_direction.ordinal()][0], (int) (ghost.x * tilesize - ghostOffset), (int) (ghost.y * tilesize - ghostOffset + Yoffset), ghostSize, ghostSize, this);
                else if (ghost.isFrightend)
                    g.drawImage(scaredGhosts[ghostFrame1 ? 0 : 1], (int) (ghost.x * tilesize - ghostOffset), (int) (ghost.y * tilesize - ghostOffset + Yoffset), ghostSize, ghostSize, this);
                else {
                    g.drawImage(ghostSprites[ghost.type - 1][ghost.current_direction.ordinal()][ghostFrame1 ? 0 : 1], (int) (ghost.x * tilesize - ghostOffset), (int) (ghost.y * tilesize - ghostOffset + Yoffset), ghostSize, ghostSize, this);
                }
            }
    }

    private void paintScore(Graphics g) {
        int scale = 2;
        int labelHeight = 8 * scale;
        int y = 4;
        int leftCenterX = getWidth() / 4;
        int rightCenterX = getWidth() * 3 / 4;
        returnButton.setBounds((getWidth() - 78) / 2, 12, 78, 16);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), Yoffset);

        drawCenteredLabel(g, highscoreImg, leftCenterX, y, 56 * scale, labelHeight);
        drawCenteredNumber(g, String.valueOf(board.getHighscore()), leftCenterX, y + labelHeight + 2, scale);

        drawCenteredLabel(g, scoreImg, rightCenterX, y, 35 * scale, labelHeight);
        drawCenteredNumber(g, String.valueOf(board.getScore()), rightCenterX, y + labelHeight + 2, scale);
    }

    private void drawReturnButton(Graphics g) {
        if (returnHovered) {
            g.setColor(new Color(255, 255, 255, 55));
            g.fillRect(returnButton.x - 2, returnButton.y - 2, returnButton.width + 4, returnButton.height + 4);
            g.setColor(Color.WHITE);
            g.drawRect(returnButton.x - 2, returnButton.y - 2, returnButton.width + 4, returnButton.height + 4);
        }
        g.drawImage(returnImg, returnButton.x, returnButton.y, returnButton.width, returnButton.height, this);
    }

    private void fireActionPerformed(String command) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(event);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && returnButton.contains(e.getPoint())) {
            fireActionPerformed("return");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        returnHovered = false;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        returnHovered = returnButton.contains(e.getPoint());
        setCursor(Cursor.getPredefinedCursor(returnHovered ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    private void drawCenteredLabel(Graphics g, Image image, int centerX, int y, int width, int height) {
        g.drawImage(image, centerX - width / 2, y, width, height, this);
    }

    private void drawCenteredNumber(Graphics g, String value, int centerX, int y, int scale) {
        int totalWidth = 0;
        for (int i = 0; i < value.length(); i++) {
            Image digit = numbers.get(value.charAt(i));
            if (digit != null) {
                int digitWidth = digit.getWidth(this);
                totalWidth += (digitWidth > 0 ? digitWidth : 8) * scale;
            }
        }

        int x = centerX - totalWidth / 2;
        for (int i = 0; i < value.length(); i++) {
            Image digit = numbers.get(value.charAt(i));
            if (digit == null) continue;
            int sourceWidth = digit.getWidth(this);
            int width = (sourceWidth > 0 ? sourceWidth : 8) * scale;
            g.drawImage(digit, x, y, width, 8 * scale, this);
            x += width;
        }
    }
}
