import java.util.*;

/**
 * Iterative Deepening Depth-First Search solver
 * Combines benefits of BFS (optimal solution) and DFS (memory efficiency)
 * Good balance for medium scrambles (8-12 moves)
 */
public class IDDFSSolver {

  private static int totalNodesExplored;
  private static long startTime;
  private static final long TIMEOUT_MS = 30000; // 30 second timeout

  public static List<RubiksCube.Move> solve(RubiksCube cube, int maxDepth) {
    totalNodesExplored = 0;
    startTime = System.currentTimeMillis();

    for (int depth = 0; depth <= maxDepth; depth++) {
      System.out.println("IDDFS: Searching at depth " + depth);
      List<RubiksCube.Move> solution = dls(cube, new ArrayList<>(), depth, null);

      if (solution != null) {
        System.out.println("IDDFS: Solution found! Total nodes explored: " + totalNodesExplored);
        return solution;
      }

      // Check timeout
      if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
        System.out.println("IDDFS: Timeout after " + (TIMEOUT_MS / 1000) + " seconds");
        return null;
      }
    }

    System.out.println("IDDFS: No solution found within depth " + maxDepth);
    return null;
  }

  private static List<RubiksCube.Move> dls(RubiksCube cube, List<RubiksCube.Move> moves,
      int depthLeft, RubiksCube.Move lastMove) {
    totalNodesExplored++;

    // Periodic progress update
    if (totalNodesExplored % 50000 == 0) {
      long elapsed = System.currentTimeMillis() - startTime;
      System.out.println("  Progress: " + totalNodesExplored + " nodes, " +
          (elapsed / 1000) + "s elapsed");
    }

    // Check timeout
    if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
      return null;
    }

    if (cube.isSolved()) {
      return new ArrayList<>(moves);
    }

    if (depthLeft == 0) {
      return null;
    }

    for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
      // Strong pruning - essential for performance
      if (shouldPrune(lastMove, move)) {
        continue;
      }

      RubiksCube nextState = cube.clone();
      nextState.applyMove(move);
      moves.add(move);

      List<RubiksCube.Move> result = dls(nextState, moves, depthLeft - 1, move);

      if (result != null) {
        return result;
      }

      moves.remove(moves.size() - 1);
    }

    return null;
  }

  private static boolean shouldPrune(RubiksCube.Move last, RubiksCube.Move current) {
    if (last == null)
      return false;

    String lastFace = last.name().substring(0, 1);
    String currFace = current.name().substring(0, 1);

    // Don't do same face consecutively (handles R, R' and R, R2 cases)
    if (lastFace.equals(currFace)) {
      return true;
    }

    // Canonicalize opposite face order to reduce search space
    // Only allow one ordering: U before D, L before R, F before B
    if (isOppositeFace(lastFace, currFace)) {
      return lastFace.compareTo(currFace) > 0;
    }

    return false;
  }

  private static boolean isOppositeFace(String f1, String f2) {
    return (f1.equals("U") && f2.equals("D")) ||
        (f1.equals("D") && f2.equals("U")) ||
        (f1.equals("L") && f2.equals("R")) ||
        (f1.equals("R") && f2.equals("L")) ||
        (f1.equals("F") && f2.equals("B")) ||
        (f1.equals("B") && f2.equals("F"));
  }
}