package minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Gamepanel extends JPanel implements MouseListener, ActionListener {
    private static final Color backgroundColor = new Color(20, 23, 26);
    private static final Color boardColor = new Color(35, 39, 42);
    private static final Color frameColor = new Color(77, 83, 86);
    private static final Color textColor = new Color(225, 228, 226);
    private static final Font uiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private final Map<Integer, Image> tiles = new HashMap<>();
    int[][] field;
    int[][] fieldmask;
    boolean restart = true;
    int totaltiles;
    int openedTiles;
    int minesFlagged;
    boolean won;
    int Time;
    difficulty diff = difficulty.EASY;
    private final Timer timer;

    Gamepanel() {
        for (int i = -5; i <= 8; i++) {
            if (i == -2) continue;
            tiles.put(i, new ImageIcon(Objects.requireNonNull(getClass().getResource("/minesweeper/Tile" + i + ".png"))).getImage());
        }
        setFocusable(true);
        setBackground(backgroundColor);
        addMouseListener(this);
        timer = new Timer(1000, this);
        prepareNewBoard();
    }

    public void prepareNewBoard() {
        field = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        fieldmask = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        totaltiles = Settings.getArraywidth(diff) * Settings.getArrayheight(diff);
        minesFlagged = Settings.getNumberOfMines(diff);
        openedTiles = 0;
        won = false;
        restart = true;
        Time = 0;
        timer.stop();
        setPreferredSize(new Dimension(Settings.getArraywidth(diff) * Settings.tilesize + Settings.Xoffset * 2, Settings.getArrayheight(diff) * Settings.tilesize + Settings.Yoffset + 52));
        revalidate();
        repaint();
    }

    private String formatTime() {
        return String.format("TIME %03d", Math.min(Time, 999));
    }

    public void reset(int firstX, int firstY) {
        if (firstX < 0 || firstY < 0 || firstX >= Settings.getArraywidth(diff) || firstY >= Settings.getArrayheight(diff))
            return;
        field = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        fieldmask = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        totaltiles = Settings.getArraywidth(diff) * Settings.getArrayheight(diff);
        minesFlagged = Settings.getNumberOfMines(diff);
        openedTiles = 0;
        won = false;
        restart = false;
        Time = 0;
        timer.start();
        randomizeMines(firstX, firstY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        paintStatusBar(g);
        paintGamefield(g);
        if (won) paintWinOverlay(g);
        g.dispose();
    }

    private void paintStatusBar(Graphics2D g) {
        int width = getWidth() - Settings.Xoffset * 2;
        g.setColor(boardColor);
        g.fillRect(Settings.Xoffset, 8, width, 34);
        g.setFont(uiFont);
        g.setColor(textColor);
        g.drawString("MINES", Settings.Xoffset + 10, 30);
        String mineCount = String.format("%03d", minesFlagged);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.drawString(mineCount, Settings.Xoffset + 62, 30);
        int timeWidth = g.getFontMetrics().stringWidth(formatTime());
        g.drawString(formatTime(), getWidth() - Settings.Xoffset - timeWidth - 10, 30);
    }

    private void paintGamefield(Graphics2D g) {
        int boardX = Settings.Xoffset;
        int boardY = 48;
        int boardWidth = Settings.getArraywidth(diff) * Settings.tilesize;
        int boardHeight = Settings.getArrayheight(diff) * Settings.tilesize;
        g.setColor(frameColor);
        g.fillRect(boardX - 1, boardY - 1, boardWidth + 2, boardHeight + 2);
        g.setColor(boardColor);
        g.fillRect(boardX, boardY, boardWidth, boardHeight);

        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                int tileImage;
                if (fieldmask[row][col] != 0 && fieldmask[row][col] != 2) {
                    tileImage = field[row][col] == -2 ? 0 : field[row][col];
                } else {
                    tileImage = fieldmask[row][col] == 2 ? -4 : -5;
                }
                int x = boardX + col * Settings.tilesize;
                int y = boardY + row * Settings.tilesize;
                g.drawImage(tiles.get(tileImage), x, y, Settings.tilesize, Settings.tilesize, this);
            }
        }
    }

    private void paintWinOverlay(Graphics2D g) {
        int x = Settings.Xoffset;
        int y = 48;
        int width = Settings.getArraywidth(diff) * Settings.tilesize;
        int height = Settings.getArrayheight(diff) * Settings.tilesize;
        g.setColor(new Color(8, 11, 13, 185));
        g.fillRect(x, y, width, height);

        Image win = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/minesweeper/YOU_WIN.png"))).getImage();
        int imageWidth = Math.min(width - 32, getWidth() - Settings.Xoffset * 4);
        int imageHeight = imageWidth * win.getHeight(this) / Math.max(1, win.getWidth(this));
        int imageX = x + (width - imageWidth) / 2;
        int imageY = y + (height - imageHeight) / 2;
        g.drawImage(win, imageX, imageY, imageWidth, imageHeight, this);
    }

    private void randomizeMines(int firstX, int firstY) {
        boolean validBoard = false;
        while (!validBoard) {
            for (int[] row : field) java.util.Arrays.fill(row, 0);
            for (int i = Settings.getNumberOfMines(diff); i > 0; i--) {
                int randomX;
                int randomY;
                do {
                    randomX = (int) (Math.random() * field[0].length);
                    randomY = (int) (Math.random() * field.length);
                } while (field[randomY][randomX] == -1 || isProtectedArea(randomX, randomY, firstX, firstY));
                field[randomY][randomX] = -1;
                updateFields(randomY, randomX);
            }
            validBoard = testSolvable(firstX, firstY);
        }
    }

    private boolean isProtectedArea(int testX, int testY, int firstX, int firstY) {
        return Math.abs(firstX - testX) <= 1 && Math.abs(firstY - testY) <= 1;
    }

    private void updateFields(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < field.length && newCol >= 0 && newCol < field[0].length
                        && field[newRow][newCol] != -1) field[newRow][newCol]++;
            }
        }
    }

    private boolean testSolvable(int startX, int startY) {
        int[][] simMask = new int[field.length][field[0].length];
        int[] simOpened = {0};
        simReveal(startY, startX, simMask, simOpened);
        boolean progress;
        do {
            progress = false;
            for (int row = 0; row < field.length; row++) {
                for (int col = 0; col < field[row].length; col++) {
                    if (simMask[row][col] != 1 || field[row][col] <= 0) continue;
                    int targetMines = field[row][col];
                    int flags = 0;
                    int hidden = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int r = row + i;
                            int c = col + j;
                            if (r >= 0 && r < field.length && c >= 0 && c < field[0].length) {
                                if (simMask[r][c] == 2) flags++;
                                if (simMask[r][c] == 0) hidden++;
                            }
                        }
                    }
                    if (flags == targetMines && hidden > 0) {
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                int r = row + i;
                                int c = col + j;
                                if (r >= 0 && r < field.length && c >= 0 && c < field[0].length && simMask[r][c] == 0) {
                                    simReveal(r, c, simMask, simOpened);
                                    progress = true;
                                }
                            }
                        }
                    }
                    if (hidden > 0 && hidden + flags == targetMines) {
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                int r = row + i;
                                int c = col + j;
                                if (r >= 0 && r < field.length && c >= 0 && c < field[0].length && simMask[r][c] == 0) {
                                    simMask[r][c] = 2;
                                    progress = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (progress);
        return simOpened[0] == totaltiles - Settings.getNumberOfMines(diff);
    }

    private void simReveal(int row, int col, int[][] simMask, int[] simOpened) {
        if (simMask[row][col] != 0) return;
        simMask[row][col] = 1;
        simOpened[0]++;
        if (field[row][col] == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int r = row + i;
                    int c = col + j;
                    if (r >= 0 && r < field.length && c >= 0 && c < field[0].length)
                        simReveal(r, c, simMask, simOpened);
                }
            }
        }
    }

    private void revealField(int col, int row) {
        if (row < 0 || col < 0 || row >= field.length || col >= field[0].length || fieldmask[row][col] != 0) return;
        openedTiles++;
        fieldmask[row][col] = 1;
        if (field[row][col] == -1) {
            field[row][col] = -3;
            revealAllTiles();
            timer.stop();
            restart = true;
            return;
        }
        if (field[row][col] == 0) {
            field[row][col] = -2;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) revealField(col + j, row + i);
            }
        }
        if (openedTiles >= totaltiles - Settings.getNumberOfMines(diff)) {
            won = true;
            restart = true;
            timer.stop();
        }
    }

    private void switchFlag(int col, int row) {
        if (row < 0 || col < 0 || row >= field.length || col >= field[0].length || fieldmask[row][col] == 1) return;
        if (fieldmask[row][col] == 0) {
            fieldmask[row][col] = 2;
            minesFlagged--;
        } else {
            fieldmask[row][col] = 0;
            minesFlagged++;
        }
    }

    private void revealAllTiles() {
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) fieldmask[row][col] = 1;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();
        int boardX = Settings.Xoffset;
        int boardY = 48;
        if (point.y < boardY || point.x < boardX) return;
        int col = (point.x - boardX) / Settings.tilesize;
        int row = (point.y - boardY) / Settings.tilesize;
        if (row < 0 || col < 0 || row >= field.length || col >= field[0].length) return;
        if (restart && SwingUtilities.isLeftMouseButton(e)) reset(col, row);
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (fieldmask[row][col] != 2) revealField(col, row);
        } else if (SwingUtilities.isRightMouseButton(e) && !restart && !won) {
            switchFlag(col, row);
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Time++;
        repaint();
    }
}
