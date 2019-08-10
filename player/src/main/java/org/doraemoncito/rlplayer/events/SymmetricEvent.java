package org.doraemoncito.rlplayer.events;

/**
 * A symmetric event is one that takes board symmetry into account and always returns the same hash code for board
 * configurations that are symmetrical.
 *
 * @author josehernandez
 */
public final class SymmetricEvent extends Event {

    // Symmetry lookup table
    private final static int[][] symmetryTable =
        {
            {1, 2, 3, 4, 5, 6, 7, 8, 9}, //   0
            {7, 4, 1, 8, 5, 2, 9, 6, 3}, //  90
            {9, 8, 7, 6, 5, 4, 3, 2, 1}, // 180
            {3, 6, 9, 2, 5, 8, 1, 4, 7}, // 270
            {3, 2, 1, 6, 5, 4, 9, 8, 7}, //   0 reflection
            {9, 6, 3, 8, 5, 2, 7, 4, 1}, //  90 reflection
            {7, 8, 9, 4, 5, 6, 1, 2, 3},  // 180 reflection
            {1, 4, 7, 2, 5, 8, 3, 6, 9}  // 270 reflection
        };

    private boolean debug = false;

    public SymmetricEvent(String state, int action, int reward, String nextState) {

        super(state, action, reward, nextState);
    }

    public SymmetricEvent() {

        super();
    }

    private static int getSymmetricIdx(String state) {

        int[] symmetryHash = new int[symmetryTable.length];

        /* First calculate the hash codes of all the symmetries.
         * The hashing function is a simple polynomial amounting to
         * a base 3 conversion.
         */
        for (int i = 0; i < symmetryHash.length; i++) {
            symmetryHash[i] = 0;

            for (int j = 0; j < NUM_ACTIONS; j++) {
                symmetryHash[i] += "_fe".indexOf(state.charAt(symmetryTable[i][j] - 1))
                    * Math.pow(3, j);
            }
        }

        int maxHashIdx = 0;

        /* ...then pick the largest hash value out of all the symmetries so
         * that we only ever update one of the many equivalent positions.
         */
        for (int i = 1; i < symmetryHash.length; i++)
            if (symmetryHash[i] > symmetryHash[maxHashIdx]) {
                maxHashIdx = i;
            }

        return maxHashIdx;
    }

    private static String getSymmetricState(String state) {

        StringBuilder symmetryState = new StringBuilder();
        int i = getSymmetricIdx(state);

        for (int j = 0; j < NUM_ACTIONS; j++) {
            symmetryState.append(state.charAt(symmetryTable[i][j] - 1));
        }

        return symmetryState.toString();
    }

    public boolean isDebug() {

        return debug;
    }

    public void setDebug(boolean debug) {

        this.debug = debug;
    }

    public String getState(int which) {

        return getSymmetricState(state[which]);
    }

    public int getRealAction(int action) {

        int i = getSymmetricIdx(state[THIS_STATE]);
        return (symmetryTable[i][action] - 1);
    }

    /**
     * Returns a hashcode given the board state after taking symmetries into
     * account; that is, many equivalent boards states will return the same
     * hashcode.
     *
     * @param state Board state to transform into a hash code.  The board state is
     *              represented by a string of nine characters where the characters
     *              can be '_', 'f' or 'e'.
     * @return Board state hash code.
     */
    protected int[] calcStateActionHash(String state, int action) {

        int[][] symmetryHash = new int[symmetryTable.length][2];

        /* First calculate the hash codes of all the symmetries.
         * The hashing function is a simple polynomial amounting to a base 3 conversion.
         */
        for (int i = 0; i < symmetryHash.length; i++) {
            symmetryHash[i][0] = 0;
            symmetryHash[i][1] = 0;

            if (debug) {
                System.out.print("[" + i + "] ");
            }

            for (int j = 0; j < NUM_ACTIONS; j++) {
                symmetryHash[i][0] += "_fe".indexOf(state.charAt(symmetryTable[i][j] - 1)) * Math.pow(3, j);

                if (debug) {
                    System.out.print(state.charAt(symmetryTable[i][j] - 1));
                }

                if ((symmetryTable[i][j] - 1) == action) {
                    symmetryHash[i][1] = j;
                }
            }

            if (debug) {
                System.out.println("  --> ACTION: " + symmetryHash[i][1] + "  HASH: " + symmetryHash[i][0]);
            }
        }

        int maxHashIdx = 0;

        /* ...then pick the largest hash value out of all the symmetries so
         * that we only ever update one of the many equivalent positions.
         */
        for (int i = 1; i < symmetryHash.length; i++)
            if (symmetryHash[i][0] > symmetryHash[maxHashIdx][0]) {
                maxHashIdx = i;
            }

        if (debug) {
            System.out.println("  --> MAXHASH: " + maxHashIdx);
        }

        return symmetryHash[maxHashIdx];
    }

}
