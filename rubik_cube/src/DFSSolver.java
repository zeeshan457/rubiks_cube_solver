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

    List<RubiksCube.Move> solution = dfs(cube, new ArrayList<>(), maxDepth, null);

    System.out.println("DFS: Nodes explored: " + nodesExplored);
    return solution;
  }

  private static List<RubiksCube.Move> dfs(RubiksCube cube, List<RubiksCube.Move> moves,
      int depthLeft, RubiksCube.Move lastMove) {
    nodesExplored++;

    if (nodesExplored % 10000 == 0) {
      System.out.println("DFS: Nodes explored: " + nodesExplored + ", depth: " + moves.size());
    }

    if (cube.isSolved()) {
      return new ArrayList<>(moves);
    }

    if (depthLeft == 0) {
      return null;
    }

    // Try all moves with pruning
    for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
      if (shouldPrune(lastMove, move)) {
        continue;
      }

      RubiksCube nextState = cube.clone();
      nextState.applyMove(move);

      int hash = nextState.hashCode();
      if (!visited.contains(hash)) {
        visited.add(hash);
        moves.add(move);

        List<RubiksCube.Move> result = dfs(nextState, moves, depthLeft - 1, move);
        if (result != null) {
          return result;
        }

        moves.remove(moves.size() - 1);
        visited.remove(hash);
      }
    }

    return null;
  }

  private static boolean shouldPrune(RubiksCube.Move last, RubiksCube.Move current) {
    if (last == null)
      return false;

    String lastFace = last.name().substring(0, 1);
    String currFace = current.name().substring(0, 1);

    // Don't repeat same face
    if (lastFace.equals(currFace)) {
      return true;
    }

    // Order opposite faces consistently
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