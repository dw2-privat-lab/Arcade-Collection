package twentyfourtyeight;

public class TileAnimation {
    public int startRow, startCol;
    public int endRow, endCol;
    public int value;

    public TileAnimation(int startRow, int startCol, int endRow, int endCol, int value) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.value = value;
    }
}
