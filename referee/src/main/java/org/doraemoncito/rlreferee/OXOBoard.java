package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.IllegalLocationException;

import java.util.Random;

/**
 * Represents a board.
 */
public class OXOBoard {

    /**
     * Constants for board pieces.
     */
    public static final int
        EMPTY = 0,
        O = 1,
        X = 2;
    /**
     * Random number generator
     */
    private static final Random random = new Random();
    // Set to true to play Tic=Tac-Ooops
    private static final boolean nondeterministic = false;
    private final int[] board = new int[9];

    /**
     * Return the other player.
     */
    public static int otherPlayer(final int player) {

        if (O == player) {
            return X;
        } else if (X == player) {
            return O;
        } else {
            throw new IllegalArgumentException("otherPlayer: invalid piece");
        }
    }

    public int getPiece(int n) {

        return board[n];
    }

    /**
     * Get a piece.  X/Y coordinates start at 0.
     */
    public int getPiece(int x, int y) {

        return getPiece(3 * x + y);
    }

    /**
     * Play piece in given 0-numbered square.
     */
    public void playPiece(final int n, final int piece) throws IllegalLocationException {

        if (n >= 0 && n < 9) {
            if (O == piece || X == piece) {
                if (EMPTY == board[n]) {
                    board[n] = piece;
                    // Randomly remove a piece
                    if (nondeterministic) {
                        board[random.nextInt(9)] = 0;
                    }
                } else {
                    // Square occupied
                    throw new IllegalLocationException(piece);
                }
            } else {
                // Not a nought or a cross!
                throw new IllegalArgumentException("setPiece: invalid piece");
            }
        } else {
            // User playing a different game?
            throw new IllegalLocationException(piece);
        }
    }

    /**
     * Play piece in given 0-numbers x,y location.
     */
    public void playPiece(int x, int y, int piece) throws IllegalLocationException {

        playPiece(3 * x + y, piece);
    }

    /**
     * Check if given player has won.
     */
    public boolean checkForWin(final int player) {

        // Across
        return (player == getPiece(0, 0) && player == getPiece(1, 0) && player == getPiece(2, 0)) ||
            (player == getPiece(0, 1) && player == getPiece(1, 1) && player == getPiece(2, 1)) ||
            (player == getPiece(0, 2) && player == getPiece(1, 2) && player == getPiece(2, 2)) ||
            // Down
            (player == getPiece(0, 0) && player == getPiece(0, 1) && player == getPiece(0, 2)) ||
            (player == getPiece(1, 0) && player == getPiece(1, 1) && player == getPiece(1, 2)) ||
            (player == getPiece(2, 0) && player == getPiece(2, 1) && player == getPiece(2, 2)) ||
            // Diagonal
            (player == getPiece(0, 0) && player == getPiece(1, 1) && player == getPiece(2, 2)) ||
            (player == getPiece(2, 0) && player == getPiece(1, 1) && player == getPiece(0, 2));
    }

    /**
     * Returns true if game is a draw.
     * NB: Never returns true if nondeterministic=true.
     */
    public boolean checkForDraw() {

        for (int i = 0; i < 9; i++)
            if (EMPTY == getPiece(i)) {
                return false;
            }

        return true;
    }

    /**
     * Get a board representation for the given player.  Returned string
     * cpntains f for friendly piece, e for enemy piece and _ for empty.
     */
    public String getBoardString(final int player) {

        if (O == player || X == player) {
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < 9; i++)
                if (EMPTY == getPiece(i)) {
                    buff.append('_');
                } else if (player == getPiece(i)) {
                    buff.append('f');
                } else {
                    buff.append('e');
                }

            return buff.toString();
        } else {
            throw new IllegalArgumentException("getBoardString: invalid piece");
        }
    }

    /**
     * Get a string displaying the board.
     */
    public String getBoardDisplay() {

        StringBuffer buff = new StringBuffer();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                switch (getPiece(x, y)) {
                    case EMPTY:
                        buff.append(' ');
                        break;
                    case O:
                        buff.append('O');
                        break;
                    case X:
                        buff.append('X');
                        break;
                    default:
                        throw new IllegalStateException("getBoardDisplay: invalid board");
                }
                if (y < 2) buff.append('|');
            }
            buff.append('\n');
            if (x < 2) buff.append("-+-+-\n");
        }
        return buff.toString();
    }

}
