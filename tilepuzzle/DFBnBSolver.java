package tilepuzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class DFBnBSolver {
    private static int totalNodesCreated = 0;

    public static Map<String, Object> dfbnbSolve(PuzzleState start, String printOpenList) {
        Map<String, Object> resultMap = new HashMap<>();
        List<PuzzleState> result ;
        Stack<PuzzleState> openList = new Stack<>();
        Set<PuzzleState> closedList = new HashSet<>();

        openList.push(start);
        closedList.add(start); // Mark initial state as visited

        int maxValue = (start.getBoard().length - 1) * (start.getBoard()[0].length - 1); // Assuming maximum value based on grid size
        int initialBound;
        if (maxValue <= 12) {
            initialBound = calculateFactorial(maxValue);
        } else {
            initialBound = maxValue;
        }

        while (!openList.isEmpty()) {
            if (printOpenList.equals("with open")) {
                System.out.println("Open List:");
                for (PuzzleState state : openList) {
                    System.out.println(state); 
                }
            }
            PuzzleState current = openList.pop();
            if (current.isMarkedOut()) {
                continue;
            }

            current.setMarkedOut(true); // Mark as "out"

            List<PuzzleState> neighbors = current.getNeighbors();
            Collections.sort(neighbors, Comparator.comparingInt(neighbor -> DFBnBSolver.getFValue(current, neighbor)));

            for (PuzzleState neighbor : neighbors) {
                totalNodesCreated++;

                int manhattanDistance = calculateManhattanDistance(neighbor);
                int moveCost = calculateMoveCost(current, neighbor);
                int fValue = manhattanDistance + moveCost;
                neighbor.setFValue(fValue); // Assuming setFValue exists in PuzzleState
                            if (fValue >= initialBound) {
                    continue;
                }

                if (closedList.contains(neighbor) && neighbor.isMarkedOut()) {
                    continue;
                }

                closedList.add(neighbor); // Mark visited in this branch

                if (neighbor.isGoal()) {
                    result = getPath(neighbor);
                    resultMap.put("totalNodesCreated", totalNodesCreated);
                    resultMap.put("result", result);
                    return resultMap;
                }

                openList.push(neighbor);
            }

            // Update threshold with minimum f-value from explored neighbors
            int minFValue = Integer.MAX_VALUE;
            for (PuzzleState neighbor : neighbors) {
                minFValue = Math.min(minFValue,getFValue(current,neighbor));
            }
            initialBound = Math.min(initialBound, minFValue);
        }

        return null; // No solution found
    }


 private static int calculateManhattanDistance(PuzzleState state) {
        int distance = 0;
        int numRows = state.getBoard().length;
        int numCols = state.getBoard()[0].length;
    
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int value = state.getBoard()[i][j].getValue();
                if (value != 0) { // Ignore the empty tile
                    int goalRow = (value - 1) / numCols;
                    int goalCol = (value - 1) % numCols;
                    distance += Math.abs(goalRow - i) + Math.abs(goalCol - j);
                }
            }
        }
        return distance;
    }
    

    private static int calculateMoveCost(PuzzleState currentState, PuzzleState neighbor) {
        int fromRow = currentState.getEmptyRow();
        int fromCol = currentState.getEmptyColumn();

        Tile movedTile = currentState.getBoard()[fromRow][fromCol];
        return movedTile.isWhite() ? 1 : 30;
    }

    private static List<PuzzleState> getPath(PuzzleState state) {
        List<PuzzleState> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state);
            state = state.getParentState(); // Move to the parent state
        }
        return path;
    }


    public static int calculateFactorial(int n) {
  if (n < 0) {
    throw new IllegalArgumentException("n must be non-negative");
  }

  if (n == 0) {
    return 1;
  }

  int factorial = 1;
  for (int i = 1; i <= n; i++) {
    factorial *= i;
  }

  return factorial;
}

public static int getFValue(PuzzleState currentState, PuzzleState neighbor){
    int cost = calculateMoveCost(currentState, neighbor);
    int distance = calculateManhattanDistance(neighbor);
    int fValueNeighbor = cost + distance;
    return fValueNeighbor;
}

    
}

