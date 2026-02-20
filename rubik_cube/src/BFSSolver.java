import java.util.*;

/**
 * Breadth-First Search solver for Rubik's Cube
 * Guarantees optimal solution but memory-intensive
 * Best for shallow scrambles (< 8 moves)
 */
public class BFSSolver {

  private static class Node {
    RubiksCube state;
    List<RubiksCube.Move> moves;
    RubiksCube.Move lastMove;

    Node(RubiksCube state, List<RubiksCube.Move> moves, RubiksCube.Move lastMove) {
      this.state = state;
      this.moves = moves;
      this.lastMove = lastMove;
    }
  }

  public static List<RubiksCube.Move> solve(RubiksCube cube, int maxDepth) {
    if (cube.isSolved()) {
      return new ArrayList<>();
    }

    Queue<Node> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();

    queue.offer(new Node(cube.clone(), new ArrayList<>(), null));
    visited.add(cube.hashCode());

    int nodesExplored = 0;
    int currentDepth = 0;

    while (!queue.isEmpty()) {
      Node current = queue.poll();
      nodesExplored++;

      if (current.moves.size() > currentDepth) {
        currentDepth = current.moves.size();
        System.out.println("BFS: Exploring depth " + currentDepth +
            ", queue size: " + queue.size() +
            ", visited: " + visited.size());
      }

      if (current.moves.size() >= maxDepth) {
        continue;
      }

      // Try all possible moves with pruning
      for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
        // Prune redundant moves
        if (shouldPrune(current.lastMove, move)) {
          continue;
        }

        RubiksCube nextState = current.state.clone();
        nextState.applyMove(move);

        if (nextState.isSolved()) {
          List<RubiksCube.Move> solution = new ArrayList<>(current.moves);
          solution.add(move);
          System.out.println("BFS: Solution found! Nodes explored: " + nodesExplored);
          return solution;
        }

        int hash = nextState.hashCode();
        if (!visited.contains(hash)) {
          visited.add(hash);
          List<RubiksCube.Move> newMoves = new ArrayList<>(current.moves);
          newMoves.add(move);
          queue.offer(new Node(nextState, newMoves, move));
        }
      }
    }

    System.out.println("BFS: No solution found within depth " + maxDepth);
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

    // Order opposite faces consistently to avoid redundancy
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