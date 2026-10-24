package Tetris;

import commonTools.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class TetrisMainPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    TetrisGame game = new TetrisGame(this);
    int tilesize = 30;

    int gamePanelXoffset = 25;
    int gamePanelYoffset = 25;

    private final Rectangle resetGame = new Rectangle(gamePanelXoffset + (10 * tilesize - 45 * 4) / 2, gamePanelYoffset + 105, 45 * 4, 8 * 4);
    private final Rectangle returnButton = new Rectangle(gamePanelXoffset + (10 * tilesize) + 78, gamePanelYoffset + 430, 39 * 3, 8 * 3);
    HashMap<Integer, Image> tiles = new HashMap<>();
    HashMap<Character, Image> numbers = new HashMap<>();
    Image scoreImg = ImageLoader.loadImage(this.getClass(), "Tetris/score.png");
    Image highscoreImg = ImageLoader.loadImage(this.getClass(), "Tetris/highscore.png");
    Image nextPieceImg = ImageLoader.loadImage(this.getClass(), "Tetris/nextPiece.png");
    Image gameOverImg = ImageLoader.loadImage(this.getClass(), "Tetris/gameOver.png");
    Image restartImg = ImageLoader.loadImage(this.getClass(), "Tetris/restart.png");
    Image returnImg = ImageLoader.loadImage(this.getClass(), "Tetris/return.png");
    Timer repaintTimer;
    Thread gameThread = new Thread(game);
    int[][] renderField;
    private int hoveredElement = 0;

    public TetrisMainPanel(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
        loadImages();
        setPreferredSize(new Dimension(600, 650));
        setBackground(new Color(25, 25, 25));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        gameThread.start();
        renderField = game.getGameField();
        repaintTimer = new Timer(33, e -> repaint());
    }

    public void startGame() {
        repaintTimer.start();
        game.unpause();
    }

    public void stopGame() {
        repaintTimer.stop();
        game.pauseGame();
    }

    public void reset() {
        game.resetGame();
    }

    private void loadImages() {
        tiles.put(0, ImageLoader.loadImage(this.getClass(), "Tetris/background.png"));
        tiles.put(1, ImageLoader.loadImage(this.getClass(), "Tetris/blue.png"));
        tiles.put(2, ImageLoader.loadImage(this.getClass(), "Tetris/cyan.png"));
        tiles.put(3, ImageLoader.loadImage(this.getClass(), "Tetris/green.png"));
        tiles.put(4, ImageLoader.loadImage(this.getClass(), "Tetris/orange.png"));
        tiles.put(5, ImageLoader.loadImage(this.getClass(), "Tetris/purple.png"));
        tiles.put(6, ImageLoader.loadImage(this.getClass(), "Tetris/red.png"));
        tiles.put(7, ImageLoader.loadImage(this.getClass(), "Tetris/yellow.png"));
        numbers.put('0', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/0.png"));
        numbers.put('1', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/1.png"));
        numbers.put('2', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/2.png"));
        numbers.put('3', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/3.png"));
        numbers.put('4', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/4.png"));
        numbers.put('5', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/5.png"));
        numbers.put('6', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/6.png"));
        numbers.put('7', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/7.png"));
        numbers.put('8', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/8.png"));
        numbers.put('9', ImageLoader.loadImage(this.getClass(), "Tetris/numbers/9.png"));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawGameField((Graphics2D) g);
        drawNextPiece(g);
        drawScore(g);
        if (game.isGameOver())
            drawGameOverScreen(g);
        drawReturnButton(g);
    }

    private void drawGameField(Graphics2D g) {
        int boardWidth = renderField[0].length * tilesize;
        int boardHeight = renderField.length * tilesize;

        g.setColor(Color.BLACK);
        g.fillRect(gamePanelXoffset + 6, gamePanelYoffset + 6, boardWidth, boardHeight);

        g.setColor(new Color(40, 40, 40));
        g.fillRect(gamePanelXoffset, gamePanelYoffset, boardWidth, boardHeight);

        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(4));
        g.drawRect(gamePanelXoffset - 2, gamePanelYoffset - 2, boardWidth + 4, boardHeight + 4);

        for (int y = 0; y < renderField.length; y++) {
            for (int x = 0; x < renderField[0].length; x++) {
                if (tiles.get(Math.abs(renderField[y][x])) != null) {
                    g.drawImage(tiles.get(Math.abs(renderField[y][x])), x * tilesize + gamePanelXoffset, y * tilesize + gamePanelYoffset, tilesize, tilesize, null);
                } else {
                    System.out.println("Tile Not Found: " + Math.abs(renderField[y][x]));
                }
            }
        }
    }

    private void drawNextPiece(Graphics g) {
        int uiX = gamePanelXoffset + (renderField[0].length * tilesize) + 30;
        int uiY = gamePanelYoffset;
        int boxWidth = 220;
        int boxHeight = 170;

        drawUIPanel((Graphics2D) g, uiX, uiY, boxWidth, boxHeight);

        int scale = 3;
        int imgWidth = 60 * scale;
        int imgHeight = 8 * scale;

        g.drawImage(nextPieceImg, uiX + (boxWidth - imgWidth) / 2, uiY + 15, imgWidth, imgHeight, null);

        int[][] temp = getPiece();
        int pieceWidth = temp[0].length * tilesize;
        int pieceHeight = temp.length * tilesize;

        int startX = uiX + (boxWidth - pieceWidth) / 2;
        int startY = uiY + 15 + imgHeight + ((boxHeight - 15 - imgHeight - pieceHeight) / 2);

        for (int y = 0; y < temp.length; y++) {
            for (int x = 0; x < temp[y].length; x++) {
                if (temp[y][x] != 0 && tiles.get(game.getNextPieceColor()) != null) {
                    g.drawImage(tiles.get(game.getNextPieceColor()), startX + (x * tilesize), startY + (y * tilesize), tilesize, tilesize, null);
                }
            }
        }
    }

    private void drawScore(Graphics g) {
        int boxWidth = 220;
        int scale = 3;
        int uiX = gamePanelXoffset + (renderField[0].length * tilesize) + 30;
        int uiY = gamePanelYoffset + 180;

        drawUIPanel((Graphics2D) g, uiX, uiY, 220, 220);

        String score = String.valueOf(game.getScore());
        String highscore = String.valueOf(game.getHighscore());

        int highscoreImgWidth = 56 * scale;
        int highscoreImgHeight = 8 * scale;
        int highscoreStartY = uiY + 20;

        g.drawImage(highscoreImg, uiX + (boxWidth - highscoreImgWidth) / 2, highscoreStartY, highscoreImgWidth, highscoreImgHeight, null);

        int highscoreTotalWidth = 0;
        for (int i = 0; i < highscore.length(); i++) {
            Image img = numbers.get(highscore.charAt(i));
            int w = img.getWidth(null);
            highscoreTotalWidth += (w > 0 ? w : 8) * scale;
        }

        drawNumbers(g, highscore, uiX + (boxWidth - highscoreTotalWidth) / 2, highscoreStartY + highscoreImgHeight + 10, scale);

        int scoreImgWidth = 35 * scale;
        int scoreImgHeight = 8 * scale;
        int scoreStartY = highscoreStartY + highscoreImgHeight + 10 + (8 * scale) + 25;

        g.drawImage(scoreImg, uiX + (boxWidth - scoreImgWidth) / 2, scoreStartY, scoreImgWidth, scoreImgHeight, null);

        int scoreTotalWidth = 0;
        for (int i = 0; i < score.length(); i++) {
            Image img = numbers.get(score.charAt(i));
            int w = img.getWidth(null);
            scoreTotalWidth += (w > 0 ? w : 8) * scale;
        }

        int scoreNumX = uiX + (boxWidth - scoreTotalWidth) / 2;
        int scoreNumY = scoreStartY + scoreImgHeight + 10;
        drawNumbers(g, score, scoreNumX, scoreNumY, scale);
    }


    private void drawUIPanel(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(x + 5, y + 5, width, height);
        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, width, height);
    }

    private void drawNumbers(Graphics g, String numStr, int x, int y, int scale) {
        int currentX = x;
        for (int i = 0; i < numStr.length(); i++) {
            Image img = numbers.get(numStr.charAt(i));
            int w = img.getWidth(null);
            int actualWidth = (w > 0 ? w : 8) * scale;
            g.drawImage(img, currentX, y, actualWidth, 8 * scale, null);
            currentX += actualWidth;
        }
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(gamePanelXoffset, gamePanelYoffset, 10 * tilesize, 20 * tilesize);
        g.drawImage(gameOverImg, gamePanelXoffset + (10 * tilesize - 265) / 2, gamePanelYoffset + 50, 265, 40, null);

        if (hoveredElement == 1) {
            g.setColor(new Color(255, 255, 255, 50));
            g.fillRect(resetGame.x - 2, resetGame.y - 2, resetGame.width + 4, resetGame.height + 4);
            g.setColor(Color.WHITE);
            g.drawRect(resetGame.x - 2, resetGame.y - 2, resetGame.width + 4, resetGame.height + 4);
        }
        g.drawImage(restartImg, resetGame.x, resetGame.y, resetGame.width, resetGame.height, null);
    }

    private void drawReturnButton(Graphics g) {
        if (hoveredElement == 2) {
            g.setColor(new Color(255, 255, 255, 50));
            g.fillRect(returnButton.x - 1, returnButton.y - 1, returnButton.width + 2, returnButton.height + 2);
            g.setColor(Color.WHITE);
            g.drawRect(returnButton.x - 1, returnButton.y - 1, returnButton.width + 2, returnButton.height + 2);
        }
        g.drawImage(returnImg, returnButton.x, returnButton.y, returnButton.width, returnButton.height, null);
        g.setColor(new Color(255, 255, 255, 50));
    }

    private int[][] getPiece() {
        int[][] temp;
        switch (game.getNextPieceNumber()) {
            case 1 -> temp = new int[][]{{1, 1}, {1, 1}};
            case 2 -> temp = new int[][]{{1}, {1}, {1}, {1}};
            case 3 -> temp = new int[][]{{1, 1, 0}, {0, 1, 1}};
            case 4 -> temp = new int[][]{{0, 1, 1}, {1, 1, 0}};
            case 5 -> temp = new int[][]{{1, 0}, {1, 0}, {1, 1}};
            case 6 -> temp = new int[][]{{0, 1}, {0, 1}, {1, 1}};
            case 7 -> temp = new int[][]{{0, 1, 0}, {1, 1, 1}};
            default -> temp = new int[][]{{1}};
        }
        return temp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!game.isLockUserInput())
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    game.move(directions.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.move(directions.RIGHT);
                    break;
                case KeyEvent.VK_DOWN:
                    game.moveDown();
                    break;
                case KeyEvent.VK_UP:
                    game.rotate();
            }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (resetGame.contains(e.getPoint())) {
                game.resetGame();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else if (returnButton.contains(e.getPoint())) {
                fireActionPerformed("return");
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
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
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if (resetGame.contains(e.getPoint())) {
            if (game.isGameOver())
                hoveredElement = 1;
        } else if (returnButton.contains(e.getPoint())) {
            hoveredElement = 2;
        } else hoveredElement = 0;

        if (hoveredElement != 0) setCursor(new Cursor(Cursor.HAND_CURSOR));
        else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void fireActionPerformed(String action) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
            }
        }
    }
}