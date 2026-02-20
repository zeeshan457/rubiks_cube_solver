import java.util.Random;

/**
 * Model 3: Bitboard/Compact representation
 * Uses long integers to represent cube state compactly
 * Most space-efficient, useful for pattern databases
 */
public class BitboardCube implements RubiksCube {

  // Pack entire cube state into long integers
  // Each cubie position and orientation encoded in bits
  private long cornerState; // 8 corners * 6 bits each (3 pos + 3 orient) = 48 bits
  private long edgeState1; // First 10 edges * 5 bits each = 50 bits
  private long edgeState2; // Last 2 edges * 5 bits each = 10 bits

  // Lookup tables for move operations (precomputed for speed)
  private static final long[] U_CORNER_TRANSFORM = new long[40320]; // 8! permutations
  private static final long[] U_EDGE_TRANSFORM = new long[479001600]; // 12! too large, use partial

  public BitboardCube() {
    reset();
  }

  @Override
  public void reset() {
    cornerState = 0L;
    edgeState1 = 0L;
    edgeState2 = 0L;

    // Encode solved state
    for (int i = 0; i < 8; i++) {
      setCorner(i, i, 0);
    }
    for (int i = 0; i < 12; i++) {
      setEdge(i, i, 0);
    }
  }

  private void setCorner(int position, int cubie, int orientation) {
    int bitPos = position * 6;
    long mask = ~(0x3FL << bitPos);
    cornerState = (cornerState & mask) | (((long) cubie | ((long) orientation << 3)) << bitPos);
  }

  private int getCornerCubie(int position) {
    return (int) ((cornerState >>> (position * 6)) & 0x7L);
  }

  private int getCornerOrientation(int position) {
    return (int) ((cornerState >>> (position * 6 + 3)) & 0x7L);
  }

  private void setEdge(int position, int cubie, int orientation) {
    int bitPos = position * 5;
    long value = (long) cubie | ((long) orientation << 4);

    if (position < 10) {
      long mask = ~(0x1FL << bitPos);
      edgeState1 = (edgeState1 & mask) | (value << bitPos);
    } else {
      bitPos = (position - 10) * 5;
      long mask = ~(0x1FL << bitPos);
      edgeState2 = (edgeState2 & mask) | (value << bitPos);
    }
  }

  private int getEdgeCubie(int position) {
    if (position < 10) {
      return (int) ((edgeState1 >>> (position * 5)) & 0xFL);
    } else {
      return (int) ((edgeState2 >>> ((position - 10) * 5)) & 0xFL);
    }
  }

  private int getEdgeOrientation(int position) {
    if (position < 10) {
      return (int) ((edgeState1 >>> (position * 5 + 4)) & 0x1L);
    } else {
      return (int) ((edgeState2 >>> ((position - 10) * 5 + 4)) & 0x1L);
    }
  }

  @Override
  public void applyMove(Move move) {
    // Extract current state to arrays
    int[] cPos = new int[8];
    int[] cOrient = new int[8];
    int[] ePos = new int[12];
    int[] eOrient = new int[12];

    for (int i = 0; i < 8; i++) {
      cPos[i] = getCornerCubie(i);
      cOrient[i] = getCornerOrientation(i);
    }
    for (int i = 0; i < 12; i++) {
      ePos[i] = getEdgeCubie(i);
      eOrient[i] = getEdgeOrientation(i);
    }

    // Apply transformation
    applyMoveToArrays(move, cPos, cOrient, ePos, eOrient);

    // Re-encode state
    for (int i = 0; i < 8; i++) {
      setCorner(i, cPos[i], cOrient[i]);
    }
    for (int i = 0; i < 12; i++) {
      setEdge(i, ePos[i], eOrient[i]);
    }
  }

  private void applyMoveToArrays(Move move, int[] cp, int[] co, int[] ep, int[] eo) {
    switch (move) {
      case U:
      case U_PRIME:
      case U2:
        int times = (move == Move.U) ? 1 : (move == Move.U2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 0, 1, 2, 3);
          cycle4(ep, 0, 1, 2, 3);
        }
        break;
      case D:
      case D_PRIME:
      case D2:
        times = (move == Move.D) ? 1 : (move == Move.D2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 4, 7, 6, 5);
          cycle4(ep, 4, 5, 6, 7);
        }
        break;
      case L:
      case L_PRIME:
      case L2:
        times = (move == Move.L) ? 1 : (move == Move.L2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 0, 3, 7, 4);
          co[0] = (co[0] + 2) % 3;
          co[3] = (co[3] + 1) % 3;
          co[7] = (co[7] + 2) % 3;
          co[4] = (co[4] + 1) % 3;
          cycle4(ep, 3, 11, 7, 8);
        }
        break;
      case R:
      case R_PRIME:
      case R2:
        times = (move == Move.R) ? 1 : (move == Move.R2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 1, 5, 6, 2);
          co[1] = (co[1] + 1) % 3;
          co[5] = (co[5] + 2) % 3;
          co[6] = (co[6] + 1) % 3;
          co[2] = (co[2] + 2) % 3;
          cycle4(ep, 1, 9, 5, 10);
        }
        break;
      case F:
      case F_PRIME:
      case F2:
        times = (move == Move.F) ? 1 : (move == Move.F2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 0, 4, 5, 1);
          co[0] = (co[0] + 1) % 3;
          co[4] = (co[4] + 2) % 3;
          co[5] = (co[5] + 1) % 3;
          co[1] = (co[1] + 2) % 3;
          cycle4(ep, 0, 8, 4, 9);
          eo[0] ^= 1;
          eo[8] ^= 1;
          eo[4] ^= 1;
          eo[9] ^= 1;
        }
        break;
      case B:
      case B_PRIME:
      case B2:
        times = (move == Move.B) ? 1 : (move == Move.B2) ? 2 : 3;
        for (int i = 0; i < times; i++) {
          cycle4(cp, 2, 6, 7, 3);
          co[2] = (co[2] + 2) % 3;
          co[6] = (co[6] + 1) % 3;
          co[7] = (co[7] + 2) % 3;
          co[3] = (co[3] + 1) % 3;
          cycle4(ep, 2, 10, 6, 11);
          eo[2] ^= 1;
          eo[10] ^= 1;
          eo[6] ^= 1;
          eo[11] ^= 1;
        }
        break;
    }
  }

  private void cycle4(int[] arr, int a, int b, int c, int d) {
    int temp = arr[a];
    arr[a] = arr[d];
    arr[d] = arr[c];
    arr[c] = arr[b];
    arr[b] = temp;
  }

  @Override
  public boolean isSolved() {
    for (int i = 0; i < 8; i++) {
      if (getCornerCubie(i) != i || getCornerOrientation(i) != 0)
        return false;
    }
    for (int i = 0; i < 12; i++) {
      if (getEdgeCubie(i) != i || getEdgeOrientation(i) != 0)
        return false;
    }
    return true;
  }

  @Override
  public void scramble(int numMoves) {
    Random rand = new Random();
    for (int i = 0; i < numMoves; i++) {
      applyMove(Move.ALL_MOVES[rand.nextInt(Move.ALL_MOVES.length)]);
    }
  }

  @Override
  public BitboardCube clone() {
    BitboardCube copy = new BitboardCube();
    copy.cornerState = this.cornerState;
    copy.edgeState1 = this.edgeState1;
    copy.edgeState2 = this.edgeState2;
    return copy;
  }

  @Override
  public int hashCode() {
    return (int) (cornerState ^ edgeState1 ^ edgeState2);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BitboardCube))
      return false;
    BitboardCube other = (BitboardCube) obj;
    return cornerState == other.cornerState &&
        edgeState1 == other.edgeState1 &&
        edgeState2 == other.edgeState2;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Bitboard Cube:\n");
    sb.append("Corner State: 0x").append(Long.toHexString(cornerState)).append("\n");
    sb.append("Edge State1: 0x").append(Long.toHexString(edgeState1)).append("\n");
    sb.append("Edge State2: 0x").append(Long.toHexString(edgeState2)).append("\n");
    return sb.toString();
  }
}