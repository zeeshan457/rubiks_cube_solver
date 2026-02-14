/**
 * Interface for Rubik's Cube representation
 * Defines core operations that all cube models must support
 */
public interface RubiksCube extends Cloneable {

  /**
   * Possible moves on a Rubik's Cube
   */
  enum Move {
    U, U_PRIME, U2, // Up face
    D, D_PRIME, D2, // Down face
    L, L_PRIME, L2, // Left face
    R, R_PRIME, R2, // Right face
    F, F_PRIME, F2, // Front face
    B, B_PRIME, B2; // Back face

    public static Move[] ALL_MOVES = values();
  }

  /**
   * Apply a move to the cube
   */
  void applyMove(Move move);

  /**
   * Check if the cube is in solved state
   */
  boolean isSolved();

  /**
   * Get a unique hash code for the current state
   */
  int hashCode();

  /**
   * Check equality with another cube state
   */
  boolean equals(Object obj);

  /**
   * Create a deep copy of the cube
   */
  RubiksCube clone();

  /**
   * Get string representation of the cube
   */
  String toString();

  /**
   * Scramble the cube with random moves
   */
  void scramble(int numMoves);

  /**
   * Reset to solved state
   */
  void reset();
}