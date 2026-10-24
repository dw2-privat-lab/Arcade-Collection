package PacMan;

import javax.swing.*;
import java.util.ArrayList;

public class Board {
    ArrayList<PacMan_Ghost> ghosts = new ArrayList<>();
    int[][] originalBoard = {
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 2, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 2, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1, -1},
            {-1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {-1, -1, -1, -1, -1, -1, 1, -1, -1, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, -1, -1, 1, -1, -1, -1, -1, -1, -1},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {-1, -1, -1, -1, -1, -1, 1, -1, -1, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, -1, -1, 1, -1, -1, -1, -1, -1, -1},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, -1, 1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 1, -1, 0, 0, 0, 0, 0},
            {-1, -1, -1, -1, -1, -1, 1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 1, -1, -1, -1, -1, -1, -1},
            {-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
            {-1, 2, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, 2, -1},
            {-1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1},
            {-1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1},
            {-1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1, -1},
            {-1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1},
            {-1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1},
            {-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };
    Timer frightenedtimer;
    Timer moveTimer;
    int level=1;
    private Timer modeTimer;
    private int waveStep = 0;
    private final int[] waveDurations = {7, 20, 7, 20, 5, 20, 5, -1};
    private boolean isScatter = false;
    PacMan_player player;
    int[][] board = new int[31][28];

    public Board() {
        resetBoard();
        float stepsize = 0.125f;

        player = new PacMan_player(13.5f, 23.0f, stepsize, this);
        ghosts.add(new PacMan_Ghost(13.5f, 11f, stepsize, 1, this));
        ghosts.add(new PacMan_Ghost(13.5f, 14.0f, stepsize, 2, this));
        ghosts.add(new PacMan_Ghost(12.0f, 14.0f, stepsize, 3, this));
        ghosts.add(new PacMan_Ghost(15f, 14.0f, stepsize, 4, this));

        moveTimer = new Timer(16, e -> {
            player.move();
            for (PacMan_Ghost ghost : ghosts)
                ghost.move();
        });
        moveTimer.start();

        modeTimer = new Timer(waveDurations[waveStep] * 1000, e -> {
            isScatter = !isScatter;
            for (PacMan_Ghost ghost : ghosts) {
                ghost.setScatterMode(isScatter);
            }

            if (waveStep < waveDurations.length - 1) {
                waveStep++;
                if (waveDurations[waveStep] == -1) {
                    modeTimer.stop();
                } else {
                    modeTimer.setDelay(waveDurations[waveStep] * 1000);
                }
            }
        });
        modeTimer.start();
        releaseGhosts();
    }

    public void setTile(int x, int y, int tile) {
        board[y][x] = tile;
    }

    private void resetBoard() {
        for (int i = 0; i < 31; i++) {
            System.arraycopy(originalBoard[i], 0, board[i], 0, 28);
        }
    }
    private void resetGhosts(){
        for(PacMan_Ghost ghost : ghosts){
            ghost.reset();
        }
    }

    public void releaseGhosts() {
        Timer ghostReleaseTimer = new Timer(5000, e -> {
            ArrayList<PacMan_Ghost> temp = new ArrayList<>();
            for(PacMan_Ghost ghost : ghosts){
                if(ghost.isWaiting()){
                    temp.add(ghost);
                }
            }
            if (temp.isEmpty()) {
                ((Timer) e.getSource()).stop();
            }else{
                temp.getFirst().setWaiting(false);
            }
        });
        if(ghostReleaseTimer.isRunning()){
            return;
        }
        ghostReleaseTimer.start();
    }

    public int[][] getBoard() {
        return board;
    }

    public PacMan_player getPlayer() {
        return player;
    }

    public PacMan_Ghost[] getGhosts() {
        return ghosts.toArray(new PacMan_Ghost[0]);
    }

    public float getBlinkyX() {
        for (PacMan_Ghost ghost : ghosts) {
            if (ghost.type == 1) return ghost.x;
        }
        return 0;
    }

    public float getBlinkyY() {
        for (PacMan_Ghost ghost : ghosts) {
            if (ghost.type == 1) return ghost.y;
        }
        return 0;
    }

    public void setFrightened() {

        if(ghostFrightenedTime()>0) {
            for (PacMan_Ghost ghost : ghosts) {
                ghost.setFrightend(true);
            }
            frightenedtimer = new Timer(ghostFrightenedTime(), e -> {
                for (PacMan_Ghost ghost : ghosts) {
                    ghost.setFrightend(false);
                }
                ((Timer) e.getSource()).stop();
            });
            if(frightenedtimer.isRunning())
                frightenedtimer.stop();
            frightenedtimer.start();
        }
    }

    private int ghostFrightenedTime(){
        return switch (level) {
            case 1-> 6000;
            case 2,6-> 5000;
            case 3-> 4000;
            case 4-> 3000;
            case 5,7,8-> 2000;
            case 9 -> 1000;
            default -> 0;
        };
    }
    public void killPlayer() {
        player.lives--;
        resetGhosts();
    }
}
