package tilepuzzle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tilepuzzle.Tile;
import tilepuzzle.PuzzleState;
import tilepuzzle.AStarSolver;
import tilepuzzle.DFBnBSolver;
import tilepuzzle.DFIDSolver;
import tilepuzzle.IDASolver;



public class Ex1 {

    private static String algorithmType;
    private static String printTime;
    private static String printOpenList;
    private static int rows;
    private static int columns;
    private static List<Integer> whiteBlocks;
    private static PuzzleState initialPuzzleState;
    private static int totalNodesCreated ;
    @SuppressWarnings("unchecked")


    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
            algorithmType = reader.readLine().trim();
            printTime = reader.readLine().trim();
            printOpenList = reader.readLine().trim();

            // Read board size
            String[] boardSize = reader.readLine().trim().split("x");
            rows = Integer.parseInt(boardSize[0]);
            columns = Integer.parseInt(boardSize[1]);

            // Read white blocks
            String whiteBlocksLine = reader.readLine().trim();
            if (whiteBlocksLine.equals("White")) {
                whiteBlocks = new ArrayList<>();
            } else {
                whiteBlocks = parseWhiteBlocks(whiteBlocksLine);
            }

            // Read initial board state
            Tile[][] initialBoard = new Tile[rows][columns];
            int emptyRow = -1;
            int emptyColumn = -1;

            // Fill the initial board state
            for (int i = 0; i < rows; i++) {
                String[] row = reader.readLine().trim().split(",");
                for (int j = 0; j < columns; j++) {
                    String value = row[j];
                    if (value.equals("_")) {
                        initialBoard[i][j] = new Tile(0, false, 0); // Empty cell
                        emptyRow = i;
                        emptyColumn = j;
                    } else {
                        int intValue = Integer.parseInt(value);
                        boolean isWhite = false;
                        int movesLeft = 0;
                        if (whiteBlocks.contains(intValue)) {
                            int index = whiteBlocks.indexOf(intValue);
                            if (index % 2 == 0) {
                                isWhite = true;
                                if (index + 1 < whiteBlocks.size()) {
                                    movesLeft = whiteBlocks.get(index + 1);
                                }
                            }
                        } else {
                            movesLeft = Integer.MAX_VALUE; // Unlimited moves for red Tiles
                        }
                        initialBoard[i][j] = new Tile(intValue, isWhite, movesLeft);
                    }
                }
            }

            // Ensure all red tiles have movesLeft set to Integer.MAX_VALUE
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (!initialBoard[i][j].isWhite() && initialBoard[i][j].getValue() != 0) {
                        initialBoard[i][j].setMovesLeft(Integer.MAX_VALUE);
                    }
                }
            }

            initialPuzzleState = new PuzzleState(initialBoard, emptyRow, emptyColumn, 0, null);

            // Close the reader
            reader.close();

            // Print the read input (optional)
          //  printInput();


            long startTime = System.nanoTime();
            List<PuzzleState> solution = null;
            totalNodesCreated = 0;
            Map<String, Object> solverResult = null;
            if (algorithmType.equals("DFID")) {
                solverResult = DFIDSolver.dfidSolve(initialPuzzleState);
            } else if (algorithmType.equals("A*")) {
                solverResult = AStarSolver.aStarSolve(initialPuzzleState,printOpenList);
            } else if (algorithmType.equals("IDA*")) {
                solverResult = IDASolver.idaStarSolve(initialPuzzleState,printOpenList);
            } else if (algorithmType.equals("DFBnB")) {
                solverResult = DFBnBSolver.dfbnbSolve(initialPuzzleState,printOpenList);
            } else {
                System.out.println("Unsupported algorithm type: " + algorithmType);
            }
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds

            if (solverResult != null) {
                solution = (List<PuzzleState>) solverResult.get("result");
                totalNodesCreated = (int) solverResult.get("totalNodesCreated");
            }
            
            // Write the solution to the output file
            writeSolutionToFile(solution, executionTime,printTime);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> parseWhiteBlocks(String whiteBlocksLine) {
        List<Integer> result = new ArrayList<>();
        String[] blocks = whiteBlocksLine.replace("White:", "").split(",");
        for (String block : blocks) {
            block = block.replaceAll("[() ]", "");
            if (!block.isEmpty()) {
                result.add(Integer.parseInt(block));
            }
        }
        return result;
    }

    private static void writeSolutionToFile(List<PuzzleState> solution, double executionTime,String printTime) {
        try {
            FileWriter writer = new FileWriter("output.txt");
            if (solution != null) {
                List<String> actions = getActions(solution);
                writer.write(String.join("-", actions) + "\n");
                writer.write("Num: " + totalNodesCreated + "\n");
                writer.write("Cost: " + solution.get(solution.size() - 1).getCost() + "\n");                           
            } else {
                writer.write("no path \n");
                writer.write("Num: " + totalNodesCreated + "\n");
                writer.write("Cost:" + "\n");
            }
            if (printTime.equals("with time"))  {
                String formattedTime = String.format("%.7f seconds\n", executionTime);
                writer.write(formattedTime);
                }        
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getActions(List<PuzzleState> solution) {
        List<String> actions = new ArrayList<>();
        PuzzleState prevState = null;
        for (PuzzleState state : solution) {
            if (prevState != null) {
                String action = getAction(prevState, state);
                actions.add(action);
            }
            prevState = state;
        }
        return actions;
    }
    
    private static String getAction(PuzzleState from, PuzzleState to) {
        int fromEmptyRow = from.getEmptyRow();
        int fromEmptyCol = from.getEmptyColumn();
        int toEmptyRow = to.getEmptyRow();
        int toEmptyCol = to.getEmptyColumn();
        int tileNumber = from.getBoard()[toEmptyRow][toEmptyCol].getValue(); // Get the tile number
    
        // Calculate the movement direction
        String direction;
        if (toEmptyRow < fromEmptyRow) {
            direction = "D";
        } else if (toEmptyRow > fromEmptyRow) {
            direction = "U";
        } else if (toEmptyCol < fromEmptyCol) {
            direction = "R";
        } else {
            direction = "L";
        }
    
        // Construct and return the action string
        return tileNumber + direction;
    }
    
    private static void printInput() {
        System.out.println("Algorithm Type: " + algorithmType);
        System.out.println("Print Time: " + printTime);
        System.out.println("Print Open List: " + printOpenList);
        System.out.println("Board Size: " + rows + "x" + columns);

        // Print White Blocks
        if (!whiteBlocks.isEmpty()) {
            System.out.print("White Blocks: ");
            for (int i = 0; i < whiteBlocks.size(); i += 2) {
                int blockNumber = whiteBlocks.get(i);
                int moveLimit = whiteBlocks.get(i + 1);
                System.out.print("(" + blockNumber + "," + moveLimit + ") ");
            }
            System.out.println();
        }

        
    }
}
