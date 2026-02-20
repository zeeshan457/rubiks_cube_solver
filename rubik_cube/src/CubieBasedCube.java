import java.util.Arrays;
import java.util.Random;

/**
 * Model 2: Cubie-based representation
 * Tracks 8 corner cubies and 12 edge cubies with their positions and orientations
 * More efficient for pattern databases and heuristics
 */
public class CubieBasedCube implements RubiksCube {
    
    // Corner cubies: position and orientation (0-2)
    private int[] cornerPos;      // which corner is in position i
    private int[] cornerOrient;   // orientation of corner in position i
    
    // Edge cubies: position and orientation (0-1)
    private int[] edgePos;        // which edge is in position i
    private int[] edgeOrient;     // orientation of edge in position i
    
    public CubieBasedCube() {
        cornerPos = new int[8];
        cornerOrient = new int[8];
        edgePos = new int[12];
        edgeOrient = new int[12];
        reset();
    }
    
    @Override
    public void reset() {
        for (int i = 0; i < 8; i++) {
            cornerPos[i] = i;
            cornerOrient[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            edgePos[i] = i;
            edgeOrient[i] = 0;
        }
    }
    
    @Override
    public void applyMove(Move move) {
        switch (move) {
            case U: applyU(); break;
            case U_PRIME: applyU(); applyU(); applyU(); break;
            case U2: applyU(); applyU(); break;
            case D: applyD(); break;
            case D_PRIME: applyD(); applyD(); applyD(); break;
            case D2: applyD(); applyD(); break;
            case L: applyL(); break;
            case L_PRIME: applyL(); applyL(); applyL(); break;
            case L2: applyL(); applyL(); break;
            case R: applyR(); break;
            case R_PRIME: applyR(); applyR(); applyR(); break;
            case R2: applyR(); applyR(); break;
            case F: applyF(); break;
            case F_PRIME: applyF(); applyF(); applyF(); break;
            case F2: applyF(); applyF(); break;
            case B: applyB(); break;
            case B_PRIME: applyB(); applyB(); applyB(); break;
            case B2: applyB(); applyB(); break;
        }
    }
    
    private void applyU() {
        // Corners: 0->1->2->3->0
        cycleFour(cornerPos, 0, 1, 2, 3);
        // No orientation change for U moves on corners
        
        // Edges: 0->1->2->3->0
        cycleFour(edgePos, 0, 1, 2, 3);
        // No orientation change for U moves on edges
    }
    
    private void applyD() {
        cycleFour(cornerPos, 4, 7, 6, 5);
        cycleFour(edgePos, 4, 5, 6, 7);
    }
    
    private void applyL() {
        cycleFour(cornerPos, 0, 3, 7, 4);
        // Orientation changes for L moves
        cycleFour(cornerOrient, 0, 3, 7, 4);
        cornerOrient[0] = (cornerOrient[0] + 2) % 3;
        cornerOrient[3] = (cornerOrient[3] + 1) % 3;
        cornerOrient[7] = (cornerOrient[7] + 2) % 3;
        cornerOrient[4] = (cornerOrient[4] + 1) % 3;
        
        cycleFour(edgePos, 3, 11, 7, 8);
        cycleFour(edgeOrient, 3, 11, 7, 8);
    }
    
    private void applyR() {
        cycleFour(cornerPos, 1, 5, 6, 2);
        cycleFour(cornerOrient, 1, 5, 6, 2);
        cornerOrient[1] = (cornerOrient[1] + 1) % 3;
        cornerOrient[5] = (cornerOrient[5] + 2) % 3;
        cornerOrient[6] = (cornerOrient[6] + 1) % 3;
        cornerOrient[2] = (cornerOrient[2] + 2) % 3;
        
        cycleFour(edgePos, 1, 9, 5, 10);
        cycleFour(edgeOrient, 1, 9, 5, 10);
    }
    
    private void applyF() {
        cycleFour(cornerPos, 0, 4, 5, 1);
        cycleFour(cornerOrient, 0, 4, 5, 1);
        cornerOrient[0] = (cornerOrient[0] + 1) % 3;
        cornerOrient[4] = (cornerOrient[4] + 2) % 3;
        cornerOrient[5] = (cornerOrient[5] + 1) % 3;
        cornerOrient[1] = (cornerOrient[1] + 2) % 3;
        
        cycleFour(edgePos, 0, 8, 4, 9);
        flipEdges(0, 8, 4, 9);
    }
    
    private void applyB() {
        cycleFour(cornerPos, 2, 6, 7, 3);
        cycleFour(cornerOrient, 2, 6, 7, 3);
        cornerOrient[2] = (cornerOrient[2] + 2) % 3;
        cornerOrient[6] = (cornerOrient[6] + 1) % 3;
        cornerOrient[7] = (cornerOrient[7] + 2) % 3;
        cornerOrient[3] = (cornerOrient[3] + 1) % 3;
        
        cycleFour(edgePos, 2, 10, 6, 11);
        flipEdges(2, 10, 6, 11);
    }
    
    private void cycleFour(int[] array, int a, int b, int c, int d) {
        int temp = array[a];
        array[a] = array[d];
        array[d] = array[c];
        array[c] = array[b];
        array[b] = temp;
    }
    
    private void flipEdges(int... indices) {
        for (int i : indices) {
            edgeOrient[i] = 1 - edgeOrient[i];
        }
    }
    
    @Override
    public boolean isSolved() {
        for (int i = 0; i < 8; i++) {
            if (cornerPos[i] != i || cornerOrient[i] != 0) return false;
        }
        for (int i = 0; i < 12; i++) {
            if (edgePos[i] != i || edgeOrient[i] != 0) return false;
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
    public CubieBasedCube clone() {
        CubieBasedCube copy = new CubieBasedCube();
        copy.cornerPos = Arrays.copyOf(this.cornerPos, 8);
        copy.cornerOrient = Arrays.copyOf(this.cornerOrient, 8);
        copy.edgePos = Arrays.copyOf(this.edgePos, 12);
        copy.edgeOrient = Arrays.copyOf(this.edgeOrient, 12);
        return copy;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(cornerPos) ^ Arrays.hashCode(cornerOrient) ^
               Arrays.hashCode(edgePos) ^ Arrays.hashCode(edgeOrient);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CubieBasedCube)) return false;
        CubieBasedCube other = (CubieBasedCube) obj;
        return Arrays.equals(cornerPos, other.cornerPos) &&
               Arrays.equals(cornerOrient, other.cornerOrient) &&
               Arrays.equals(edgePos, other.edgePos) &&
               Arrays.equals(edgeOrient, other.edgeOrient);
    }
    
    @Override
    public String toString() {
        return "Cubie-Based Cube:\n" +
               "Corners: " + Arrays.toString(cornerPos) + "\n" +
               "Corner Orient: " + Arrays.toString(cornerOrient) + "\n" +
               "Edges: " + Arrays.toString(edgePos) + "\n" +
               "Edge Orient: " + Arrays.toString(edgeOrient);
    }
    
    // Heuristic calculation methods
    public int getCornerPermutationIndex() {
        // Returns permutation index for pattern database
        return calculatePermutationIndex(cornerPos);
    }
    
    public int getEdgePermutationIndex() {
        return calculatePermutationIndex(edgePos);
    }
    
    private int calculatePermutationIndex(int[] perm) {
        int index = 0;
        int n = perm.length;
        for (int i = 0; i < n - 1; i++) {
            int smaller = 0;
            for (int j = i + 1; j < n; j++) {
                if (perm[j] < perm[i]) smaller++;
            }
            index = index * (n - i) + smaller;
        }
        return index;
    }
}