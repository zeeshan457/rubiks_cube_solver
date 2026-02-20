import java.util.*;

/**
 * Depth-First Search solver for Rubik's Cube
 * Memory efficient but may find non-optimal solutions
 * Fast for finding any solution
 */
public class DFSSolver {

  private static int nodesExplored;
  private static Set<Integer> visited;

  public static List<RubiksCube.Move> solve(RubiksCube cube, int maxDepth) {
    nodesExplored = 0;
    visited = new HashSet<>();
    visited.add(cube.hashCode());

    List<RubiksCube.Move> solution = dfs(cube, new ArrayList<>(), maxDepth);

    System.out.println("DFS: Nodes explored: " + nodesExplored);
    return solution;
  }

  private static List<RubiksCube.Move> dfs(RubiksCube cube, List<RubiksCube.Move> moves, int depthLeft) {
    nodesExplored++;

    if (cube.isSolved()) {
      return new ArrayList<>(moves);
    }

    if (depthLeft == 0) {
      return null;
    }

    // Try all moves
    for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
      RubiksCube nextState = cube.clone();
      nextState.applyMove(move);

      int hash = nextState.hashCode();
      if (!visited.contains(hash)) {
        visited.add(hash);
        moves.add(move);

        List<RubiksCube.Move> result = dfs(nextState, moves, depthLeft - 1);
        if (result != null) {
          return result;
        }

        moves.remove(moves.size() - 1);
        visited.remove(hash);
      }
    }

    return null;
  }
}