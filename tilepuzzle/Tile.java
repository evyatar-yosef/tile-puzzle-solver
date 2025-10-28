package tilepuzzle;

public class Tile {
    private int value;
    private boolean isWhite;
    private int movesLeft;
    
    public Tile(int value, boolean isWhite, int movesLeft) {
        this.value = value;
        this.isWhite = isWhite;
        this.movesLeft = movesLeft;
    }

   
    // Getter methods for other attributes
    public int getValue() {
        return value;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

   
    @Override
    public String toString() {
        return "(" + value + ", " + (isWhite ? "White" : "Red") + ", " + movesLeft + ")";
    }

    public int getCost() {
        if(isWhite == true) return 1;
        else return 30;
    }
}
