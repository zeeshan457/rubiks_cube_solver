import java.util.*;

/**
 * Main class to test and benchmark all Rubik's Cube models and solvers
 * Demonstrates all requirements from the project specification
 */
public class RubiksCubeSolver {

  public static void main(String[] args) {
    System.out.println("=== RUBIK'S CUBE SOLVER ===\n");

    // Test all three models
    testModels();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Benchmark solvers with 8-move scramble
    benchmarkShallowScramble();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Benchmark IDA* with 13-move scramble
    benchmarkDeepScramble();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Performance comparison
    performanceComparison();
  }

  private static void testModels() {
    System.out.println("TESTING THREE CUBE MODELS:\n");

    // Test Model 1: Face-based
    System.out.println("1. Face-Based Model:");
    FaceBasedCube faceCube = new FaceBasedCube();
    testModel(faceCube);

    // Test Model 2: Cubie-based
    System.out.println("\n2. Cubie-Based Model:");
    CubieBasedCube cubieCube = new CubieBasedCube();
    testModel(cubieCube);

    // Test Model 3: Bitboard
    System.out.println("\n3. Bitboard Model:");
    BitboardCube bitCube = new BitboardCube();
    testModel(bitCube);
  }

  private static void testModel(RubiksCube cube) {
    System.out.println("  Initial state: " + (cube.isSolved() ? "SOLVED" : "SCRAMBLED"));

    // Apply some moves
    cube.applyMove(RubiksCube.Move.R);
    cube.applyMove(RubiksCube.Move.U);
    cube.applyMove(RubiksCube.Move.R_PRIME);
    cube.applyMove(RubiksCube.Move.U_PRIME);

    System.out.println("  After R U R' U': " + (cube.isSolved() ? "SOLVED" : "SCRAMBLED"));

    // Test cloning
    RubiksCube clone = cube.clone();
    System.out.println("  Clone equals original: " + clone.equals(cube));

    // Reset
    cube.reset();
    System.out.println("  After reset: " + (cube.isSolved() ? "SOLVED" : "SCRAMBLED"));
  }

  private static void benchmarkShallowScramble() {
    System.out.println("SHALLOW SCRAMBLE TEST (8 moves):");
    System.out.println("Goal: Solve in under 3 seconds\n");

    CubieBasedCube cube = new CubieBasedCube();

    // Scramble with exactly 8 moves
    RubiksCube.Move[] scramble = {
        RubiksCube.Move.R, RubiksCube.Move.U, RubiksCube.Move.F,
        RubiksCube.Move.D, RubiksCube.Move.L, RubiksCube.Move.B,
        RubiksCube.Move.R_PRIME, RubiksCube.Move.U2
    };

    for (RubiksCube.Move move : scramble) {
      cube.applyMove(move);
    }

    System.out.println("Scramble sequence: " + Arrays.toString(scramble));
    System.out.println();

    // Test BFS
    testSolver("BFS", cube.clone(), () -> BFSSolver.solve(cube.clone(), 12));

    // Test DFS
    testSolver("DFS", cube.clone(), () -> DFSSolver.solve(cube.clone(), 15));

    // Test IDDFS
    testSolver("IDDFS", cube.clone(), () -> IDDFSSolver.solve(cube.clone(), 12));
  }

  private static void benchmarkDeepScramble() {
    System.out.println("DEEP SCRAMBLE TEST (13 moves):");
    System.out.println("Goal: IDA* solves in under 10 seconds\n");

    CubieBasedCube cube = new CubieBasedCube();

    // Scramble with 13 moves
    RubiksCube.Move[] scramble = {
        RubiksCube.Move.R, RubiksCube.Move.U, RubiksCube.Move.F,
        RubiksCube.Move.D, RubiksCube.Move.L, RubiksCube.Move.B,
        RubiksCube.Move.R2, RubiksCube.Move.U_PRIME, RubiksCube.Move.F2,
        RubiksCube.Move.D_PRIME, RubiksCube.Move.L2, RubiksCube.Move.B_PRIME,
        RubiksCube.Move.R
    };

    for (RubiksCube.Move move : scramble) {
      cube.applyMove(move);
    }

    System.out.println("Scramble sequence (13 moves): " + Arrays.toString(scramble));
    System.out.println();

    // Test Advanced IDA*
    testSolver("Advanced IDA* (Korf's)", cube.clone(),
        () -> AdvancedIDAStarSolver.solve(cube.clone()));
  }

  private static void performanceComparison() {
    System.out.println("PERFORMANCE COMPARISON:\n");

    int[] scrambleLengths = { 5, 7, 8, 10 };

    for (int length : scrambleLengths) {
      System.out.println("Scramble length: " + length + " moves");

      CubieBasedCube cube = new CubieBasedCube();
      cube.scramble(length);

      // Test different solvers
      System.out.println("  BFS: ");
      long start = System.currentTimeMillis();
      List<RubiksCube.Move> bfsSolution = BFSSolver.solve(cube.clone(), 15);
      long bfsTime = System.currentTimeMillis() - start;
      System.out.println("    Time: " + bfsTime + "ms, Solution length: " +
          (bfsSolution != null ? bfsSolution.size() : "N/A"));

      System.out.println("  IDDFS: ");
      start = System.currentTimeMillis();
      List<RubiksCube.Move> iddfsSolution = IDDFSSolver.solve(cube.clone(), 15);
      long iddfsTime = System.currentTimeMillis() - start;
      System.out.println("    Time: " + iddfsTime + "ms, Solution length: " +
          (iddfsSolution != null ? iddfsSolution.size() : "N/A"));

      System.out.println();
    }
  }

  private static void testSolver(String name, RubiksCube testCube, SolverFunction solver) {
    System.out.println(name + " Solver:");

    long startTime = System.currentTimeMillis();
    List<RubiksCube.Move> solution = solver.solve();
    long elapsed = System.currentTimeMillis() - startTime;

    if (solution != null) {
      System.out.println("  ✓ Solution found!");
      System.out.println("  Time: " + elapsed + "ms (" +
          String.format("%.2f", elapsed / 1000.0) + "s)");
      System.out.println("  Solution length: " + solution.size() + " moves");
      System.out.println("  Moves: " + solution);

      // Verify solution
      RubiksCube verify = testCube.clone();
      for (RubiksCube.Move move : solution) {
        verify.applyMove(move);
      }
      System.out.println("  Verification: " + (verify.isSolved() ? "✓ CORRECT" : "✗ FAILED"));

      // Check if meets requirements
      if (elapsed < 3000) {
        System.out.println("  ✓ Meets <3s requirement!");
      } else if (elapsed < 10000) {
        System.out.println("  ✓ Meets <10s requirement!");
      }
    } else {
      System.out.println("  ✗ No solution found");
      System.out.println("  Time: " + elapsed + "ms");
    }

    System.out.println();
  }

  @FunctionalInterface
  interface SolverFunction {
    List<RubiksCube.Move> solve();
  }

  // Additional utility methods
  public static void demonstrateSpaceComplexity() {
    System.out.println("SPACE COMPLEXITY ANALYSIS:\n");

    System.out.println("Model 1 (Face-Based): 6 faces × 3×3 ints = 216 bytes");
    System.out.println("Model 2 (Cubie-Based): 8+12 positions + 8+12 orientations = 80 bytes");
    System.out.println("Model 3 (Bitboard): 3 longs = 24 bytes (most efficient!)");
    System.out.println();

    System.out.println("Algorithm Space Complexity:");
    System.out.println("BFS: O(b^d) - Stores entire level");
    System.out.println("DFS: O(d) - Only stores path");
    System.out.println("IDDFS: O(d) - Combines benefits");
    System.out.println("IDA*: O(d) - Plus pattern database");
  }

  public static void demonstrateTimeComplexity() {
    System.out.println("TIME COMPLEXITY ANALYSIS:\n");

    System.out.println("BFS: O(b^d) where b=18 moves, d=depth");
    System.out.println("  Optimal but exponential in memory");
    System.out.println();

    System.out.println("DFS: O(b^d) but faster in practice");
    System.out.println("  May find non-optimal solution");
    System.out.println();

    System.out.println("IDDFS: O(b^d) with better constants");
    System.out.println("  Optimal and memory-efficient");
    System.out.println();

    System.out.println("IDA*: O(b^d) but heavily pruned by heuristic");
    System.out.println("  Best practical performance for deep searches");
  }
}