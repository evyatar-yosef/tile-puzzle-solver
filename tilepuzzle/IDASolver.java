package tilepuzzle;

import java.util.*;

public class IDASolver {
    private static int totalNodesCreated = 0;
    
    public static Map<String, Object> idaStarSolve(PuzzleState start,String printOpenList) {
        Map<String, Object> resultMap = new HashMap<>();

        int threshold = calculateManhattanDistance(start);
        PriorityQueue<PuzzleState> openList = new PriorityQueue<>(Comparator.comparingInt(PuzzleState::getFValue));
        Set<PuzzleState> closedList = new HashSet<>();

        while (true) {
            int minF = Integer.MAX_VALUE;
            openList.clear();
            closedList.clear();

            openList.add(start);
            closedList.add(start); // Mark start state as closed

            while (!openList.isEmpty()) {
                if (printOpenList.equals("with open")) {
                    System.out.println("Open List:");
                    for (PuzzleState state : openList) {
                        System.out.println(state); 
                    }
                }
                PuzzleState current = openList.poll();
                current.setMarkedOut(true);
                for (PuzzleState neighbor : current.getNeighbors()) {
                    totalNodesCreated++; 

                    // Skip closed neighbors to avoid revisiting
                    if (closedList.contains(neighbor)) {
                        continue;
                    }

                    // Calculate f-value for the neighbor (combining move cost and heuristic)
                    int cost = calculateMoveCost(current, neighbor);
                    int distance = calculateManhattanDistance(neighbor);
                    int fValueNeighbor = cost + distance;
                    neighbor.setFValue(fValueNeighbor); // Store f-value for priority queue

                    if (fValueNeighbor > threshold) {
                        minF = Math.min(minF, fValueNeighbor);
                  //      System.out.println("Skipping neighbor due to fValue > threshold");
                        continue;
                    }

                    // Check if the neighbor is the goal state and return the path
                    if (neighbor.isGoal()) {
                        List<PuzzleState> result = getPath(neighbor);
                        resultMap.put("totalNodesCreated", totalNodesCreated);
                        resultMap.put("result", result);
                        return resultMap;
                    }

                    // Add neighbor to both open and closed lists
                    openList.add(neighbor);
                    closedList.add(neighbor);
                }
            }

            // Update threshold for the next iteration (no solution found if minF stays max)
            if (minF == Integer.MAX_VALUE) {
                return null; // No solution found
            } else {
                threshold = minF;
            }
        }
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
}
