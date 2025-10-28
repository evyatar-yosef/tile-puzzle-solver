package tilepuzzle;

    import java.util.ArrayList;
    import java.util.List;

    public class PuzzleState {
        private Tile[][] board;
        private int emptyRow;
        private int emptyColumn;
        private int cost;
        private PuzzleState parentState; // Add parentState field
        boolean isMarkedOut;
        int fvalue;
        

        public PuzzleState(Tile[][] board, int emptyRow, int emptyColumn, int cost, PuzzleState parentState) {
            this.board = board;
            this.emptyRow = emptyRow;
            this.emptyColumn = emptyColumn;
            this.cost = cost;
            this.parentState = parentState; // Initialize parentState
            isMarkedOut= false;
            int fvalue = 0;

        }

        public Tile[][] getBoard() {
            return board;
        }

        public int getEmptyRow() {
            return emptyRow;
        }

        public int getEmptyColumn() {
            return emptyColumn;
        }

        public int getCost() {
            return cost;
        }
        public void setCost(int tentativeGValue) {
            this.cost = tentativeGValue;
        }

        public boolean isMarkedOut() {
            return this.isMarkedOut;
        }
        public void setMarkedOut(boolean answer) {
            this.isMarkedOut = answer;
        }

        public PuzzleState getParentState() { // Getter for parentState
            return parentState;
        }
        public void setParentState(PuzzleState parentState) {
            this.parentState = parentState;
        }

        // Print the current state of the puzzle board
        public void print() {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    System.out.print(board[i][j] + "\t");
                }
                System.out.println();
            }
        }

        public boolean isGoal() {
            int value = 1;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (i == board.length - 1 && j == board[0].length - 1) {
                        // Skip the last cell in the loop
                        continue;
                    }
                    if (board[i][j].getValue() != value) {
                        return false;
                    }
                    value++;
                }
            }
            // Check the last cell specifically
            return board[board.length - 1][board[0].length - 1].getValue() == 0;
        }

        public List<PuzzleState> getNeighbors() {
            List<PuzzleState> neighbors = new ArrayList<>();
        
            // Possible moves: left, up, right, down
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
            for (int[] dir : directions) {
                int newRow = emptyRow + dir[0];
                int newCol = emptyColumn + dir[1];
        
                if (isValidMove(newRow, newCol)) {
                    PuzzleState neighborState = createNeighborState(newRow, newCol);
                    
                    neighbors.add(neighborState);
                }
            }
        
            return neighbors;
        }
        

            private boolean isValidMove(int row, int col) {
                if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
                    return false; // Coordinates out of bounds
                }
                // Check if the tile at (row, col) is a white tile with no steps left
                Tile tile = board[row][col];
                if (tile.isWhite() && tile.getMovesLeft() <= 0) {
                    return false; // White tile with no steps left
                }
                if (this.parentState != null) {
                    // Get the empty cell position of the parent state
                    int prevEmptyRow = this.parentState.getEmptyRow();
                    int prevEmptyCol = this.parentState.getEmptyColumn();

                    // Check if the current move is the opposite of the previous move
                    if (prevEmptyRow == row && prevEmptyCol == col) {
                        return false; // Skip the move operation
                    }
                }
                return true; // Valid move
            }

        private PuzzleState createNeighborState(int newRow, int newCol) {
            Tile[][] newBoard = copyBoard(board); 
            PuzzleState neighborState = new PuzzleState(newBoard, newRow, newCol, this.cost , this); // Pass this as parentState
            neighborState.move(emptyRow, emptyColumn, newRow, newCol);
            return neighborState;
        }

        private Tile[][] copyBoard(Tile[][] original) {
            Tile[][] copy = new Tile[original.length][original[0].length];
            for (int i = 0; i < original.length; i++) {
                for (int j = 0; j < original[0].length; j++) {
                    copy[i][j] = new Tile(original[i][j].getValue(), original[i][j].isWhite(), original[i][j].getMovesLeft());
                }
            }
            return copy;
        }

        // Swap the empty cell with the specified cell
        public void move(int fromRow, int fromCol, int toRow, int toCol) {
                 
            // Swap the tiles between the specified positions
            Tile temp = board[fromRow][fromCol];
            board[fromRow][fromCol] = board[toRow][toCol];
            board[toRow][toCol] = temp;
        
            // Update the empty cell position
            emptyRow = toRow;
            emptyColumn = toCol;
        
            // Increment cost based on the color of the tile and the previous cost
            if (board[fromRow][fromCol].isWhite()) {
                cost = cost + 1; // White tile cost
            } else {
                cost = cost + 30; // Red tile cost

            }
        
            // Set the parent state of the new state
        
            // Decrement movesLeft for white blocks if applicable
            if (board[toRow][toCol].isWhite()) {
                board[toRow][toCol].setMovesLeft(board[toRow][toCol].getMovesLeft() - 1);
            }
        }
        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PuzzleState:\n");
        sb.append("  Board:\n");
        for (Tile[] row : board) {
            for (Tile tile : row) {
                sb.append(String.format("%3d", tile.getValue()));
            }
            sb.append("\n");
        }
        sb.append("  Empty cell: (" + emptyRow + ", " + emptyColumn + ")\n");
        sb.append("  Cost: " + cost + "\n");
        return sb.toString();
    }

        public int getFValue() {
            return this.fvalue;
        }

        public void setFValue(int value) {
        this.fvalue = value;
        }

        
    }
    

