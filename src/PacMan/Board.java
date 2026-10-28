package PacMan;

import commonTools.SoundPlayer;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {
    private static final Path SAVE_FILE = Path.of("highscores.json");
    private final int[] waveDurations = {7, 20, 7, 20, 5, 20, 5, -1};
    public int score = 0;
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
    int level = 1;
    PacMan_player player;
    int[][] board = new int[31][28];
    private javax.sound.sampled.Clip activeLoopSound;
    private LoopingSound activeLoopSoundType = LoopingSound.NONE;
    private boolean frightenedModeActive;
    private Timer modeTimer;
    private int waveStep = 0;
    private boolean isScatter = false;
    private boolean running = false;
    private int highscore = 0;
    private boolean playDeathAnimaton = false;
    private boolean deathInProgress = false;
    private Timer ghostReleaseTimer;
    private Timer startDelayTimer;
    private Timer deathDelayTimer;
    private Timer deathFinishTimer;
    private Timer secondDeathSoundTimer;
    private boolean started = false;
    private boolean playerMoving = false;

    public Board() {
        resetBoard();
        float stepsize = 0.125f;
        player = new PacMan_player(13.5f, 23.0f, stepsize, this);
        ghosts.add(new PacMan_Ghost(13.5f, 11f, stepsize, 1, this));
        ghosts.add(new PacMan_Ghost(13.5f, 14.0f, stepsize, 2, this));
        ghosts.add(new PacMan_Ghost(11.75f, 14.0f, stepsize, 3, this));
        ghosts.add(new PacMan_Ghost(15.25f, 14.0f, stepsize, 4, this));
        loadHighscores();
    }

    public void setTile(int x, int y, int tile) {
        board[y][x] = tile;
    }

    private void loadHighscores() {
        if (!Files.exists(SAVE_FILE)) return;

        try {
            String content = Files.readString(SAVE_FILE);
            Matcher categoryMatcher = Pattern.compile("\"PACMAN\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL).matcher(content);
            if (!categoryMatcher.find()) return;

            String data = categoryMatcher.group(1);
            Matcher currentFormat = Pattern.compile("\"highscore\"\\s*:\\s*(\\d+)").matcher(data);
            if (currentFormat.find()) {
                highscore = Integer.parseInt(currentFormat.group(1));
                return;
            }

            Matcher legacyFormat = Pattern.compile("\"(\\d+)\"\\s*:\\s*(\\d+)").matcher(data);
            while (legacyFormat.find()) {
                highscore = Math.max(highscore, Integer.parseInt(legacyFormat.group(2)));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Could not load Pac-Man high score from " + SAVE_FILE + ": " + e.getMessage());
        }
    }

    public void saveHighscore() {
        try {
            String content = Files.exists(SAVE_FILE) ? Files.readString(SAVE_FILE) : "{}";
            String trimmed = content.trim();
            if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
                throw new IOException("High-score file is not a JSON object");
            }

            String entry = "\"PACMAN\": {\n    \"highscore\": " + highscore + "\n  }";
            Pattern pattern = Pattern.compile("\"PACMAN\"\\s*:\\s*\\{[^{}]*}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                content = matcher.replaceFirst(Matcher.quoteReplacement(entry));
            } else {
                int closingBrace = content.lastIndexOf('}');
                int openingBrace = content.indexOf('{');
                String existingEntries = content.substring(openingBrace + 1, closingBrace).trim();
                String separator = existingEntries.isEmpty() || existingEntries.endsWith(",") ? "" : ",";
                content = content.substring(0, closingBrace) + separator + "\n  " + entry + "\n" + content.substring(closingBrace);
            }

            Files.writeString(SAVE_FILE, content);
        } catch (IOException e) {
            System.err.println("Could not save Pac-Man high score to " + SAVE_FILE + ": " + e.getMessage());
        }
    }

    public void ghostEaten() {
        updateLoopingSounds();
    }

    private void updateLoopingSounds() {
        boolean anyEyes = false;
        for (PacMan_Ghost ghost : ghosts) {
            anyEyes |= ghost.isDead();
        }

        LoopingSound desired;
        if (!started || !running || deathInProgress) {
            desired = LoopingSound.NONE;
        } else if (anyEyes) {
            desired = LoopingSound.EYES;
        } else if (frightenedModeActive) {
            desired = LoopingSound.FRIGHTENED;
        } else {
            desired = LoopingSound.SIREN;
        }
        setLoopingSound(desired);
    }

    private void setLoopingSound(LoopingSound sound) {
        if (activeLoopSoundType == sound && activeLoopSound != null && activeLoopSound.isRunning()) return;

        SoundPlayer.stopSound(activeLoopSound);
        activeLoopSound = null;
        activeLoopSoundType = sound;

        String path = switch (sound) {
            case SIREN -> "resources/Sounds/pacman_siren.wav";
            case FRIGHTENED -> "resources/Sounds/pacman_fright.wav";
            case EYES -> "resources/Sounds/pacman_eyes.wav";
            case NONE -> null;
        };
        if (path != null) activeLoopSound = SoundPlayer.playLoopingSound(path);
    }

    private void stopLoopingSounds() {
        SoundPlayer.stopSound(activeLoopSound);
        activeLoopSound = null;
        activeLoopSoundType = LoopingSound.NONE;
    }

    public int getScore() {
        return score;
    }

    public int getHighscore() {
        return highscore;
    }

    private void resetBoard() {
        for (int i = 0; i < 31; i++) {
            System.arraycopy(originalBoard[i], 0, board[i], 0, 28);
        }
        if (player != null)
            player.remaininDots = 244;
    }

    public void addScore(int score) {
        this.score += score;
        if (this.score > highscore) {
            highscore = this.score;
            saveHighscore();
        }
    }

    private void resetGhosts() {
        for (PacMan_Ghost ghost : ghosts) {
            ghost.reset();
        }
    }

    public void releaseGhosts() {
        if (ghostReleaseTimer == null) {
            ghostReleaseTimer = new Timer(4210, e -> {
                for (PacMan_Ghost ghost : ghosts) {
                    if (ghost.isWaiting()) {
                        ghost.setWaiting(false);
                        return;
                    }
                }
                ((Timer) e.getSource()).stop();
            });
        }
        if (!ghostReleaseTimer.isRunning()) ghostReleaseTimer.start();
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
        int duration = ghostFrightenedTime();
        if (duration <= 0) return;

        frightenedModeActive = true;
        for (PacMan_Ghost ghost : ghosts) {
            ghost.setFrightend(true);
        }

        if (frightenedtimer == null) {
            frightenedtimer = new Timer(duration, e -> {
                frightenedModeActive = false;
                for (PacMan_Ghost ghost : ghosts) {
                    ghost.setFrightend(false);
                }
                updateLoopingSounds();
                ((Timer) e.getSource()).stop();
            });
            frightenedtimer.setRepeats(false);
        }
        frightenedtimer.setInitialDelay(duration);
        frightenedtimer.setDelay(duration);
        frightenedtimer.restart();

        updateLoopingSounds();
    }

    private int ghostFrightenedTime() {
        return switch (level) {
            case 1 -> 6000;
            case 2, 6 -> 5000;
            case 3 -> 4000;
            case 4 -> 3000;
            case 5, 7, 8 -> 2000;
            case 9 -> 1000;
            default -> 0;
        };
    }

    public void killPlayer() {
        if (!started || deathInProgress) return;
        deathInProgress = true;
        frightenedModeActive = false;
        if (frightenedtimer != null) frightenedtimer.stop();
        running = false;
        playDeathAnimaton = true;
        if (moveTimer != null) moveTimer.stop();
        if (modeTimer != null) modeTimer.stop();
        if (ghostReleaseTimer != null) ghostReleaseTimer.stop();
        stopLoopingSounds();
        SoundPlayer.playSound("resources/Sounds/pacman_death_0.wav");

        deathDelayTimer = new Timer(1380, e -> {
            SoundPlayer.playSound("resources/Sounds/pacman_death_1.wav");
            deathFinishTimer = new Timer(200, event -> finishDeath());
            secondDeathSoundTimer = new Timer(200, event -> {
                SoundPlayer.playSound("resources/Sounds/pacman_death_1.wav");
                deathFinishTimer.start();
            });
            secondDeathSoundTimer.setRepeats(false);
            secondDeathSoundTimer.start();
            deathFinishTimer.setRepeats(false);
            ((Timer) e.getSource()).stop();
        });
        deathDelayTimer.setRepeats(false);
        deathDelayTimer.start();
    }

    private void finishDeath() {
        if (!started) return;
        player.lives--;
        if (player.lives == 0) {
            score = 0;
            level = 1;
            player.resetForNewGame();
            resetBoard();
        }
        player.reset();
        resetGhosts();
        resetModeSchedule();
        playDeathAnimaton = false;
        deathInProgress = false;
        if (moveTimer != null) moveTimer.start();
        start();
    }

    public boolean playDeathAnimaton() {
        return playDeathAnimaton;
    }

    private void start() {
        if (!started) return;
        running = false;
        SoundPlayer.playSound("resources/Sounds/pacman_start.wav");
        if (startDelayTimer != null) startDelayTimer.stop();
        if (deathDelayTimer != null) deathDelayTimer.stop();
        if (deathFinishTimer != null) deathFinishTimer.stop();
        if (secondDeathSoundTimer != null) secondDeathSoundTimer.stop();
        startDelayTimer = new Timer(4210, e -> {
            if (started) {
                running = true;
                restartModeTimer();
                releaseGhosts();
                updateLoopingSounds();
            }
            ((Timer) e.getSource()).stop();
        });
        startDelayTimer.setRepeats(false);
        startDelayTimer.start();
    }

    public void startGame() {
        if (started) return;
        resetForNewGame();
        started = true;
        if (moveTimer == null) {
            moveTimer = new Timer(16, e -> {
                if (running) {
                    float temp = player.x + player.y;
                    player.move();
                    playerMoving = (temp != player.x + player.y);
                    for (PacMan_Ghost ghost : ghosts) ghost.move();
                    updateLoopingSounds();
                }
            });
        }
        if (modeTimer == null) {
            modeTimer = new Timer(waveDurations[waveStep] * 1000, e -> {
                isScatter = !isScatter;
                for (PacMan_Ghost ghost : ghosts) ghost.setScatterMode(isScatter);
                if (waveStep < waveDurations.length - 1) {
                    waveStep++;
                    if (waveDurations[waveStep] == -1) modeTimer.stop();
                    else modeTimer.setDelay(waveDurations[waveStep] * 1000);
                }
            });
        }
        moveTimer.start();
        start();
    }

    private void resetForNewGame() {
        running = false;
        score = 0;
        level = 1;
        deathInProgress = false;
        playDeathAnimaton = false;
        playerMoving = false;
        frightenedModeActive = false;

        if (ghostReleaseTimer != null) ghostReleaseTimer.stop();
        if (frightenedtimer != null) frightenedtimer.stop();
        if (modeTimer != null) modeTimer.stop();
        stopLoopingSounds();

        resetBoard();
        player.resetForNewGame();
        resetGhosts();
        resetModeSchedule();
    }

    private void resetModeSchedule() {
        waveStep = 0;
        isScatter = true;
        for (PacMan_Ghost ghost : ghosts) ghost.setScatterMode(true);
    }

    public void stopGame() {
        started = false;
        deathInProgress = false;
        frightenedModeActive = false;
        playDeathAnimaton = false;
        running = false;
        if (moveTimer != null) moveTimer.stop();
        if (modeTimer != null) modeTimer.stop();
        if (ghostReleaseTimer != null) ghostReleaseTimer.stop();
        if (startDelayTimer != null) startDelayTimer.stop();
        if (deathDelayTimer != null) deathDelayTimer.stop();
        if (deathFinishTimer != null) deathFinishTimer.stop();
        if (secondDeathSoundTimer != null) secondDeathSoundTimer.stop();
        if (frightenedtimer != null) frightenedtimer.stop();
        stopLoopingSounds();
        for (PacMan_Ghost ghost : ghosts) ghost.setFrightend(false);
    }

    private void restartModeTimer() {
        if (modeTimer == null || waveDurations[waveStep] == -1) return;
        int duration = waveDurations[waveStep] * 1000;
        modeTimer.setInitialDelay(duration);
        modeTimer.setDelay(duration);
        modeTimer.restart();
    }

    public void wonLevel() {
        if (!started || !running) return;
        running = false;
        level++;
        if (frightenedtimer != null) frightenedtimer.stop();
        frightenedModeActive = false;
        for (PacMan_Ghost ghost : ghosts) ghost.setFrightend(false);
        stopLoopingSounds();

        resetBoard();
        resetGhosts();
        player.reset();
        if (ghostReleaseTimer != null) ghostReleaseTimer.stop();
        releaseGhosts();

        Timer delay = new Timer(4000, e -> {
            if (started) {
                running = true;
                updateLoopingSounds();
            }
            ((Timer) e.getSource()).stop();
        });
        delay.setRepeats(false);
        delay.start();
    }

    public boolean isPlayerMoving() {
        return playerMoving;
    }

    private enum LoopingSound {NONE, SIREN, FRIGHTENED, EYES}
}