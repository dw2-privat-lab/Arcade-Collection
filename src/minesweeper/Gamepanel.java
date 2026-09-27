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
    int[][] field;
    int[][] fieldmask;
    int mouseX;
    int mouseY;
    boolean restart = false;
    int totaltiles;
    int openedTiles;
    int minesFlagged;
    boolean won = false;
    int Time = 0;

    Map<Integer ,Image> tiles=new HashMap<>();

    Timer timer;

    JLabel timelabel = new JLabel("Time: "+Time);

    difficulty diff = difficulty.EASY;

    Gamepanel() {
        for (int i = -5; i <= 8; i++) {
            if(i==-2)
                continue;
            tiles.put(i, new ImageIcon(Objects.requireNonNull(getClass().getResource("/minesweeper/Tile" + i + ".png"))).getImage());
        }

        setFocusable(true);
        field = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        fieldmask = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        totaltiles = Settings.getArraywidth(diff) * Settings.getArrayheight(diff);
        minesFlagged = Settings.getNumberOfMines(diff);
        setPreferredSize(new Dimension(Settings.getArraywidth(diff) * Settings.tilesize + 2 * Settings.Xoffset, Settings.getArrayheight(diff) * Settings.tilesize + Settings.Yoffset));
        restart = true;

        addMouseListener(this);
        timer = new Timer(1000,this);
    }

    public void addTimerlabel(){
        add(timelabel);
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

        setPreferredSize(new Dimension(
                Settings.getArraywidth(diff) * Settings.tilesize + 2 * Settings.Xoffset,
                Settings.getArrayheight(diff) * Settings.tilesize + Settings.Yoffset
        ));
        revalidate();
        repaint();
    }

    public void reset(int firstX, int firstY) {
        field = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];
        fieldmask = new int[Settings.getArrayheight(diff)][Settings.getArraywidth(diff)];

        totaltiles = Settings.getArraywidth(diff) * Settings.getArrayheight(diff);
        minesFlagged = Settings.getNumberOfMines(diff);
        openedTiles = 0;
        won = false;
        restart = false;

        Time = 0;
        timer.start();

        // The generation loop now handles creating and validating the board
        randomizeMines(firstX, firstY);

        // Reset the visible field mask once a valid board is generated
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                fieldmask[i][j] = 0;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        paintGamefield(g2d);
        paintGameOverlay(g2d);
    }


    private void paintGamefield(Graphics2D g) {
        setBackground(new Color(94, 203, 226));

        for (int col = 0; col < field.length; col++) {
            for (int row = 0; row < field[col].length; row++) {
                int imageInt;

                if (fieldmask[col][row] != 0 && fieldmask[col][row] != 2) {
                    if (field[col][row] == -2) {
                        imageInt = 0;
                    } else {
                        imageInt =field[col][row];
                    }
                } else {
                    if (fieldmask[col][row] == 2) {
                        imageInt = -4;
                    } else {
                        imageInt = -5;
                    }
                }
                g.drawImage(tiles.get(imageInt), row * Settings.tilesize + Settings.Xoffset, col * Settings.tilesize + Settings.Yoffset, Settings.tilesize, Settings.tilesize, null);
            }
        }
    }

    private void paintGameOverlay(Graphics2D g) {

        g.drawString("Mines: " + minesFlagged, 0, 10);
        //g.drawString("Time: " + Time, getWidth()-g.getFontMetrics().stringWidth("Time:"+Time)-10,10 );
        timelabel.setText("Time: " + Time);
        if (won) {
            timer.stop();
            ImageIcon win = new ImageIcon(Objects.requireNonNull(getClass().getResource("/minesweeper/YOU_WIN.png")));
            int imgwidth = win.getImage().getWidth(null);
            int imgheight = win.getImage().getHeight(null);
            double scale = (double) getWidth() / 2 / imgwidth;
            g.drawImage(win.getImage(), getWidth() / 4, 30, getWidth() / 2, (int) (imgheight * scale), null);
        }
    }



    private void randomizeMines(int firstX, int firstY) {
        boolean validBoard = false;

        while (!validBoard) {
            // Wipe the backend field for a new generation attempt
            for (int i = 0; i < field[0].length; i++) {
                for (int j = 0; j < field.length; j++) {
                    field[j][i] = 0;
                }
            }

            // Drop random mines
            for (int i = Settings.getNumberOfMines(diff); i > 0; i--) {
                int randomX, randomY;
                do {
                    randomX = (int) (Math.random() * field[0].length);
                    randomY = (int) (Math.random() * field.length);
                } while (field[randomY][randomX] == -1 || isProtectedArea(randomX, randomY, firstX, firstY));

                field[randomY][randomX] = -1;
                updateFields(randomY, randomX);
            }

            // Test if the generated board can be solved deterministically
            validBoard = testSolvable(firstX, firstY);
        }
    }

    private boolean isProtectedArea(int testX, int testY, int firstX, int firstY) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (firstX + i == testX && firstY + j == testY) return true;
            }
        }
        return false;
    }

    private void updateFields(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (row + i >= 0 && row + i < field.length && col + j >= 0 && col + j < field[0].length) {
                    if (field[row + i][col + j] != -1) {
                        field[row + i][col + j]++;
                    }
                }
            }
        }
    }

    private boolean testSolvable(int startX, int startY) {
        int[][] simMask = new int[field.length][field[0].length];
        int[] simOpened = {0};

        // Simulate the player's first click safely
        simReveal(startY, startX, simMask, simOpened);

        boolean progress;
        do {
            progress = false;
            for (int r = 0; r < field.length; r++) {
                for (int c = 0; c < field[0].length; c++) {
                    if (simMask[r][c] == 1 && field[r][c] > 0) {
                        int targetMines = field[r][c];
                        int flags = 0;
                        int hidden = 0;

                        // Count neighbor states
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                int nr = r + i, nc = c + j;
                                if (nr >= 0 && nr < field.length && nc >= 0 && nc < field[0].length) {
                                    if (simMask[nr][nc] == 2) flags++;
                                    if (simMask[nr][nc] == 0) hidden++;
                                }
                            }
                        }

                        // All-Safe Rule
                        if (flags == targetMines && hidden > 0) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    int nr = r + i, nc = c + j;
                                    if (nr >= 0 && nr < field.length && nc >= 0 && nc < field[0].length && simMask[nr][nc] == 0) {
                                        simReveal(nr, nc, simMask, simOpened);
                                        progress = true;
                                    }
                                }
                            }
                        }

                        // All-Mines Rule
                        if (hidden > 0 && (hidden + flags) == targetMines) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    int nr = r + i, nc = c + j;
                                    if (nr >= 0 && nr < field.length && nc >= 0 && nc < field[0].length && simMask[nr][nc] == 0) {
                                        simMask[nr][nc] = 2; // Plant a flag
                                        progress = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (progress);

        // Returns true if every safe tile was found
        return simOpened[0] == (totaltiles - Settings.getNumberOfMines(diff));
    }

    private void simReveal(int r, int c, int[][] simMask, int[] simOpened) {
        if (simMask[r][c] != 0) return;

        simMask[r][c] = 1;
        simOpened[0]++;

        if (field[r][c] == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = r + i, nc = c + j;
                    if (nr >= 0 && nr < field.length && nc >= 0 && nc < field[0].length) {
                        simReveal(nr, nc, simMask, simOpened);
                    }
                }
            }
        }
    }

    private void revealField(int row, int col) {
        try {
            if (fieldmask[col][row] != 1) {
                openedTiles++;
                fieldmask[col][row] = 1;
                if (field[col][row] == -1) {
                    field[col][row] = -3;
                    revealAllTiles();
                    timer.stop();
                    restart = true;
                }
                if (field[col][row] == 0) {
                    field[col][row] = -2;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            revealField(row + i, col + j);
                        }
                    }
                }
            }
            if (openedTiles >= totaltiles - Settings.getNumberOfMines(diff) && field[col][row] != -3) {
                won = true;
                restart = true;
            }
        } catch (ArrayIndexOutOfBoundsException _) {
        }
    }

    private void switchFlag(int row, int col) {
        try {
            if (fieldmask[col][row] == 0) {
                fieldmask[col][row] = 2;
                minesFlagged--;
            } else if (fieldmask[col][row] == 2) {
                fieldmask[col][row] = 0;
                minesFlagged++;
            }
        } catch (ArrayIndexOutOfBoundsException _) {
        }
    }

    private void revealAllTiles() {
        for (int i = 0; i < field[0].length; i++) {
            for (int j = 0; j < field.length; j++) {
                fieldmask[j][i] = 1;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();
        mouseX = (int) point.getX();
        mouseY = (int) point.getY();

        int clickX = (mouseX - Settings.Xoffset) / Settings.tilesize;
        int clickY = (mouseY - Settings.Yoffset) / Settings.tilesize;

        if (mouseY < 30) {
            return;
        }

        if (clickY >= 0) {
            switch (e.getButton()) {
                case 1:
                    if (restart) {
                        reset(clickX, clickY);
                    }
                    if (fieldmask[clickY][clickX] != 2) {
                        revealField(clickX, clickY);
                    }
                    break;
                case 3:
                    switchFlag(clickX, clickY);
                    break;
            }
        }
        repaint();
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