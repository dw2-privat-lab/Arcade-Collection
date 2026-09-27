package minesweeper;

public class Settings {
    public static int tilesize = 34;
    public static int Xoffset = 0;
    public static int Yoffset = 30;

    public static int getArraywidth(difficulty diff) {
        return switch (diff) {
            case EASY -> 9;
            case MEDIUM -> 16;
            case HARD -> 30;
        };
    }

    public static int getArrayheight(difficulty diff) {
        return switch (diff) {
            case EASY -> 9;
            case MEDIUM, HARD -> 16;
        };
    }

    public static int getNumberOfMines(difficulty diff) {
        return switch (diff) {
            case EASY -> 10;
            case MEDIUM -> 40;
            case HARD -> 99;
        };
    }
}
