package tilepuzzle;

import java.util.*;

public class AStarSolver {
    private static Set<PuzzleState> allVertices = new HashSet<>(); // Set to keep track of all created vertices
    private static int totalNodesCreated = 0;

    // A* search algorithm to solve the puzzle
    public static Map<String, Object> aStarSolve(PuzzleState start, String printOpenList) {
        totalNodesCreated = 0;
        allVertices.clear(); // Clear the set before starting a new search
        Map<String, Object> resultMap = new HashMap<>(); // Result map to store the total nodes created and the solution path
        Map<PuzzleState, Integer> heuristicValues = new HashMap<>(); // Store heuristic values for each state
        Map<PuzzleState, Integer> gValues = new HashMap<>(); // Store g values for each state
        Set<PuzzleState> closedList = new HashSet<>(); // Closed list to store explored states

        // Priority queue to store states based on f value (f = g + h)
        PriorityQueue<PuzzleState> openSet = new PriorityQueue<>(Comparator.comparingInt(state -> gValues.getOrDefault(state, Integer.MAX_VALUE) + heuristicValues.getOrDefault(state, Integer.MAX_VALUE)));

        openSet.add(start);
        gValues.put(start, 0);
        heuristicValues.put(start, calculateManhattanDistance(start));

        // Main loop for A* algorithm
        while (!openSet.isEmpty()) {
            // Print the open list if requested
            if (printOpenList.equals("with open")) {
                System.out.println("Open List:");
                for (PuzzleState state : openSet) {
                    System.out.println(state);
                }
            }

            // Poll the state with the lowest f value from the open set
            PuzzleState currentState = openSet.poll();
            totalNodesCreated++;

            // Check if the current state is the goal state
            if (currentState.isGoal()) {
                // If goal state is found, return the solution path
                List<PuzzleState> result = getPath(currentState);
                resultMap.put("totalNodesCreated", totalNodesCreated);
                resultMap.put("result", result);
                return resultMap;
            }

            // Add current state to closed list
            closedList.add(currentState);

            // Generate neighbors and update costs
            for (PuzzleState neighbor : currentState.getNeighbors()) {
                // Check if neighbor is in closed list
                if (!closedList.contains(neighbor)) {
                    int tentativeGValue = gValues.getOrDefault(currentState, Integer.MAX_VALUE) + calculateMoveCost(currentState, neighbor);

                    // Update neighbor's g value if it improves the current value
                    if (tentativeGValue < gValues.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        gValues.put(neighbor, tentativeGValue);
                        heuristicValues.put(neighbor, calculateManhattanDistance(neighbor));
                        openSet.add(neighbor);
                        allVertices.add(neighbor); // Add neighbor to the set of all vertices
                    }
                }
            }
        }

        return null; // No solution found
    }

    // Calculate Manhattan distance heuristic for a given state
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

    // Calculate move cost between two states
    private static int calculateMoveCost(PuzzleState currentState, PuzzleState neighbor) {
        int fromRow = currentState.getEmptyRow();
        int fromCol = currentState.getEmptyColumn();
        Tile movedTile = currentState.getBoard()[fromRow][fromCol];
        return movedTile.isWhite() ? 1 : 30;
    }

    // Generate the solution path from the goal state to the start state
    private static List<PuzzleState> getPath(PuzzleState state) {
        List<PuzzleState> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state);
            state = state.getParentState(); // Move to the parent state
        }
        return path;
    }
}
