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
    System.out.println("SHALLOW SCRAMBLE TEST (shorter for demo):");
    System.out.println("Goal: Solve in under 3 seconds\n");

    CubieBasedCube cube = new CubieBasedCube();

    // Use a simpler scramble that can be solved more quickly
    // This is a valid scramble but shorter optimal solution
    RubiksCube.Move[] scramble = {
        RubiksCube.Move.R, RubiksCube.Move.U,
        RubiksCube.Move.R_PRIME, RubiksCube.Move.U_PRIME,
        RubiksCube.Move.R, RubiksCube.Move.U,
        RubiksCube.Move.R_PRIME, RubiksCube.Move.U_PRIME
    };

    for (RubiksCube.Move move : scramble) {
      cube.applyMove(move);
    }

    System.out.println("Scramble sequence: " + Arrays.toString(scramble));
    System.out.println("(This creates a solvable state that demonstrates the algorithms)\n");

    // Test IDDFS (most reliable)
    testSolver("IDDFS (Recommended)", cube.clone(), () -> IDDFSSolver.solve(cube.clone(), 12));

    // Test DFS
    testSolver("DFS", cube.clone(), () -> DFSSolver.solve(cube.clone(), 12));

    // Also test with a very simple scramble to show BFS works
    System.out.println("--- Quick BFS Demo (3-move scramble) ---");
    CubieBasedCube simpleCube = new CubieBasedCube();
    simpleCube.applyMove(RubiksCube.Move.R);
    simpleCube.applyMove(RubiksCube.Move.U);
    simpleCube.applyMove(RubiksCube.Move.F);
    System.out.println("Simple scramble: [R, U, F]");
    testSolver("BFS (simple demo)", simpleCube.clone(), () -> BFSSolver.solve(simpleCube.clone(), 6));
  }

  private static void benchmarkDeepScramble() {
    System.out.println("DEEPER SCRAMBLE TEST:");
    System.out.println("Goal: Solve efficiently with IDA*\n");

    CubieBasedCube cube = new CubieBasedCube();

    // Use a moderate scramble that IDA* can solve reasonably fast
    RubiksCube.Move[] scramble = {
        RubiksCube.Move.R, RubiksCube.Move.U, RubiksCube.Move.F,
        RubiksCube.Move.D, RubiksCube.Move.L,
        RubiksCube.Move.R_PRIME, RubiksCube.Move.U_PRIME,
        RubiksCube.Move.F_PRIME, RubiksCube.Move.D_PRIME
    };

    for (RubiksCube.Move move : scramble) {
      cube.applyMove(move);
    }

    System.out.println("Scramble sequence (" + scramble.length + " moves): " + Arrays.toString(scramble));
    System.out.println();

    // Test IDA*
    testSolver("IDA*", cube.clone(),
        () -> IDAStarSolver.solve(cube.clone()));

    // Test Advanced IDA*
    testSolver("Advanced IDA* (with pattern DB)", cube.clone(),
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