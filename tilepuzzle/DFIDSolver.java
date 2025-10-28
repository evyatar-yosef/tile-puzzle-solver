package tilepuzzle;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;

    public class DFIDSolver {
        private static int totalNodesCreated = 0;

        public static Map<String, Object> dfidSolve(PuzzleState start) {
            Map<String, Object> resultMap = new HashMap<>();

            for (int depth = 1; ; depth++) {
                Set<PuzzleState> visited = new HashSet<>();
                List<PuzzleState> result = limitedDFS(start, depth, visited);
        
                if (result != null && !result.isEmpty()) {
                    resultMap.put("totalNodesCreated", totalNodesCreated);
                    resultMap.put("result", result);
                    return resultMap;
                }
            }
        }

        private static List<PuzzleState> limitedDFS(PuzzleState state,  int limit, Set<PuzzleState> hash) {
            if (state.isGoal()) {
                return getPath(state);
            }
            if (limit == 0) {
                return new ArrayList<>(); // Return cutoff if the depth limit is reached
            }

            hash.add(state);    
            boolean isCutoff = false;
            List<PuzzleState> path = new ArrayList<>();
            List<PuzzleState> neighbors =  state.getNeighbors();

            for (PuzzleState neighbor : neighbors) {
                totalNodesCreated++;
                if (hash.contains(neighbor)) {
                    continue;
                }
                List<PuzzleState> result = limitedDFS(neighbor, limit - 1, hash);
                if (result == null) {
                    isCutoff = true;
                } else if (!result.isEmpty()) {
                    path.addAll(result);
                    return path;
                }
            }
            hash.remove(state);

            if (isCutoff) {
                return new ArrayList<>(); // Return cutoff
            } else {
                return null; // No solution found from this state
            }
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
