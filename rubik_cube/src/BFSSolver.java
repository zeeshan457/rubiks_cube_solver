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

    Node(RubiksCube state, List<RubiksCube.Move> moves) {
      this.state = state;
      this.moves = moves;
    }
  }

  public static List<RubiksCube.Move> solve(RubiksCube cube, int maxDepth) {
    if (cube.isSolved()) {
      return new ArrayList<>();
    }

    Queue<Node> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();

    queue.offer(new Node(cube.clone(), new ArrayList<>()));
    visited.add(cube.hashCode());

    int nodesExplored = 0;

    while (!queue.isEmpty()) {
      Node current = queue.poll();
      nodesExplored++;

      if (current.moves.size() >= maxDepth) {
        continue;
      }

      // Try all possible moves
      for (RubiksCube.Move move : RubiksCube.Move.ALL_MOVES) {
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
          queue.offer(new Node(nextState, newMoves));
        }
      }
    }

    System.out.println("BFS: No solution found within depth " + maxDepth);
    return null;
  }
}