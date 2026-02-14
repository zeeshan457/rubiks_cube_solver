import java.util.Arrays;
import java.util.Random;

/**
 * Model 1: Face-based representation
 * Each face is represented as a 3x3 2D array
 * Intuitive but requires complex rotation logic
 */
public class FaceBasedCube implements RubiksCube {

  // Six faces represented as 3x3 arrays
  // 0=White(U), 1=Yellow(D), 2=Red(F), 3=Orange(B), 4=Green(R), 5=Blue(L)
  private int[][] up, down, front, back, right, left;

  public FaceBasedCube() {
    reset();
  }

  @Override
  public void reset() {
    up = createFace(0);
    down = createFace(1);
    front = createFace(2);
    back = createFace(3);
    right = createFace(4);
    left = createFace(5);
  }

  private int[][] createFace(int color) {
    int[][] face = new int[3][3];
    for (int i = 0; i < 3; i++) {
      Arrays.fill(face[i], color);
    }
    return face;
  }

  @Override
  public void applyMove(Move move) {
    switch (move) {
      case U:
        rotateU();
        break;
      case U_PRIME:
        rotateU();
        rotateU();
        rotateU();
        break;
      case U2:
        rotateU();
        rotateU();
        break;
      case D:
        rotateD();
        break;
      case D_PRIME:
        rotateD();
        rotateD();
        rotateD();
        break;
      case D2:
        rotateD();
        rotateD();
        break;
      case L:
        rotateL();
        break;
      case L_PRIME:
        rotateL();
        rotateL();
        rotateL();
        break;
      case L2:
        rotateL();
        rotateL();
        break;
      case R:
        rotateR();
        break;
      case R_PRIME:
        rotateR();
        rotateR();
        rotateR();
        break;
      case R2:
        rotateR();
        rotateR();
        break;
      case F:
        rotateF();
        break;
      case F_PRIME:
        rotateF();
        rotateF();
        rotateF();
        break;
      case F2:
        rotateF();
        rotateF();
        break;
      case B:
        rotateB();
        break;
      case B_PRIME:
        rotateB();
        rotateB();
        rotateB();
        break;
      case B2:
        rotateB();
        rotateB();
        break;
    }
  }

  private void rotateFaceClockwise(int[][] face) {
    int[][] temp = new int[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        temp[j][2 - i] = face[i][j];
      }
    }
    for (int i = 0; i < 3; i++) {
      System.arraycopy(temp[i], 0, face[i], 0, 3);
    }
  }

  private void rotateU() {
    rotateFaceClockwise(up);
    int[] temp = Arrays.copyOf(front[0], 3);
    front[0] = right[0];
    right[0] = back[0];
    back[0] = left[0];
    left[0] = temp;
  }

  private void rotateD() {
    rotateFaceClockwise(down);
    int[] temp = Arrays.copyOf(front[2], 3);
    front[2] = left[2];
    left[2] = back[2];
    back[2] = right[2];
    right[2] = temp;
  }

  private void rotateL() {
    rotateFaceClockwise(left);
    int[] temp = new int[3];
    for (int i = 0; i < 3; i++) {
      temp[i] = front[i][0];
      front[i][0] = up[i][0];
      up[i][0] = back[2 - i][2];
      back[2 - i][2] = down[i][0];
      down[i][0] = temp[i];
    }
  }

  private void rotateR() {
    rotateFaceClockwise(right);
    int[] temp = new int[3];
    for (int i = 0; i < 3; i++) {
      temp[i] = front[i][2];
      front[i][2] = down[i][2];
      down[i][2] = back[2 - i][0];
      back[2 - i][0] = up[i][2];
      up[i][2] = temp[i];
    }
  }

  private void rotateF() {
    rotateFaceClockwise(front);
    int[] temp = new int[3];
    for (int i = 0; i < 3; i++) {
      temp[i] = up[2][i];
      up[2][i] = left[2 - i][2];
      left[2 - i][2] = down[0][2 - i];
      down[0][2 - i] = right[i][0];
      right[i][0] = temp[i];
    }
  }

  private void rotateB() {
    rotateFaceClockwise(back);
    int[] temp = new int[3];
    for (int i = 0; i < 3; i++) {
      temp[i] = up[0][i];
      up[0][i] = right[i][2];
      right[i][2] = down[2][2 - i];
      down[2][2 - i] = left[2 - i][0];
      left[2 - i][0] = temp[i];
    }
  }

  @Override
  public boolean isSolved() {
    return isFaceUniform(up) && isFaceUniform(down) &&
        isFaceUniform(front) && isFaceUniform(back) &&
        isFaceUniform(right) && isFaceUniform(left);
  }

  private boolean isFaceUniform(int[][] face) {
    int color = face[0][0];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (face[i][j] != color)
          return false;
      }
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
  public FaceBasedCube clone() {
    FaceBasedCube copy = new FaceBasedCube();
    copy.up = deepCopy(this.up);
    copy.down = deepCopy(this.down);
    copy.front = deepCopy(this.front);
    copy.back = deepCopy(this.back);
    copy.right = deepCopy(this.right);
    copy.left = deepCopy(this.left);
    return copy;
  }

  private int[][] deepCopy(int[][] array) {
    int[][] copy = new int[3][3];
    for (int i = 0; i < 3; i++) {
      System.arraycopy(array[i], 0, copy[i], 0, 3);
    }
    return copy;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(up) ^ Arrays.deepHashCode(down) ^
        Arrays.deepHashCode(front) ^ Arrays.deepHashCode(back) ^
        Arrays.deepHashCode(right) ^ Arrays.deepHashCode(left);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FaceBasedCube))
      return false;
    FaceBasedCube other = (FaceBasedCube) obj;
    return Arrays.deepEquals(up, other.up) && Arrays.deepEquals(down, other.down) &&
        Arrays.deepEquals(front, other.front) && Arrays.deepEquals(back, other.back) &&
        Arrays.deepEquals(right, other.right) && Arrays.deepEquals(left, other.left);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Face-Based Cube State:\n");
    sb.append("UP: ").append(faceToString(up)).append("\n");
    sb.append("DOWN: ").append(faceToString(down)).append("\n");
    sb.append("FRONT: ").append(faceToString(front)).append("\n");
    sb.append("BACK: ").append(faceToString(back)).append("\n");
    sb.append("RIGHT: ").append(faceToString(right)).append("\n");
    sb.append("LEFT: ").append(faceToString(left)).append("\n");
    return sb.toString();
  }

  private String faceToString(int[][] face) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      sb.append(Arrays.toString(face[i]));
    }
    return sb.toString();
  }
}