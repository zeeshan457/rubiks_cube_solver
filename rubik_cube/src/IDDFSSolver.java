import java.util.*;

/**
 * Iterative Deepening Depth-First Search solver
 * Combines benefits of BFS (optimal solution) and DFS (memory efficiency)
 * Good balance for medium scrambles (8-12 moves)
 */
public class IDDFSSolver {

  private static int totalNodesExplored;

  public static List<RubiksCube.Move> solve(RubiksCube cube, int maxDepth) {
    totalNodesExplored = 0;

    for (int depth = 0; depth <= maxDepth; depth++) {
      System.out.println("IDDFS: Searching at depth " + depth);
      Set<Integer> visited = new HashSet<>();
      List<RubiksCube.Move> solution = dls(cube, new ArrayList<>(), depth, visited);

      if (solution != null) {
        System.out.println("IDDFS: Solution found! Total nodes explored: " + totalNodesExplored);
        return solution;
      }
    }

    System.out.println("IDDFS: No solution found within depth " + maxDepth);
    return null;
  }

  private static List<RubiksCube.Move> dls(RubiksCube cube, List<RubiksCube.Move> moves,
      int depthLeft, Set<Integer> visited) {
    totalNodesExplored++;

    if (cube.isSolved()) {
      return new ArrayList<>(moves);
    }

    if (depthLeft == 0) {
      return null;
    }

    int currentHash = cube.hashCode();
    visited.add(currentHash);

    for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
      // Prune opposite moves (optimization)
      if (!moves.isEmpty()) {
        RubiksCube.Move lastMove = moves.get(moves.size() - 1);
        if (areOppositeMoves(lastMove, move)) {
          continue;
        }
      }

      RubiksCube nextState = cube.clone();
      nextState.applyMove(move);

      int hash = nextState.hashCode();
      if (!visited.contains(hash)) {
        moves.add(move);
        List<RubiksCube.Move> result = dls(nextState, moves, depthLeft - 1, visited);

        if (result != null) {
          return result;
        }

        moves.remove(moves.size() - 1);
      }
    }

    visited.remove(currentHash);
    return null;
  }

  private static boolean areOppositeMoves(RubiksCube.Move m1, RubiksCube.Move m2) {
    // Check if moves are on the same face
    String face1 = m1.name().substring(0, 1);
    String face2 = m2.name().substring(0, 1);
    return face1.equals(face2);
  }
}