import java.util.*;

/**
 * Main class to test and benchmark all Rubik's Cube models and solvers
 * Demonstrates all requirements from the project specification
 */
public class RubiksCubeSolver {

  public static void main(String[] args) {
    System.out.println("=== RUBIK'S CUBE SOLVER ===\n");

    // Print JVM memory info
    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory() / (1024 * 1024);
    System.out.println("Max JVM Memory: " + maxMemory + " MB");
    System.out.println("(Use -Xmx512m or -Xmx1g to increase if needed)\n");

    // Test all three models
    testModels();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Benchmark solvers with 8-move scramble
    benchmarkShallowScramble();

    System.out.println("\n" + "=".repeat(60) + "\n");

    // Benchmark IDA* with 13-move scramble
    benchmarkDeepScramble();
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

    // Test IDDFS (most reliable for 8 moves)
    testSolver("IDDFS (Recommended)", cube.clone(), () -> IDDFSSolver.solve(cube.clone(), 10));

    // Test DFS
    testSolver("DFS", cube.clone(), () -> DFSSolver.solve(cube.clone(), 12));

    // Test BFS (with lower depth to avoid memory issues)
    System.out.println("BFS Solver:");
    System.out.println("  Note: BFS requires ~100MB+ memory for 8-move scrambles");
    System.out.println("  Testing with depth limit 8 (may not find solution if longer)...");
    testSolver("BFS (depth limited)", cube.clone(), () -> BFSSolver.solve(cube.clone(), 8));
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

  private static void testSolver(String name, RubiksCube testCube, SolverFunction solver) {
    if (!name.contains("Note:") && !name.contains("BFS")) {
      System.out.println(name + " Solver:");
    }

    long startTime = System.currentTimeMillis();
    List<RubiksCube.Move> solution = null;

    try {
      solution = solver.solve();
    } catch (OutOfMemoryError e) {
      long elapsed = System.currentTimeMillis() - startTime;
      System.out.println("  ✗ Out of memory!");
      System.out.println("  Time before OOM: " + elapsed + "ms");
      System.out.println("  Tip: Run with -Xmx512m or -Xmx1g for more memory");
      System.out.println();
      return;
    }

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
}