package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.IllegalLocationException;
import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An Arena object manages a match between two players.
 */
public class Arena {

    private static final int
        DEFAULT_REWARD = 0,
        WIN_REWARD = 10,
        LOSE_REWARD = -WIN_REWARD,
        DRAW_REWARD = WIN_REWARD / 3,
        DISQUALIFY_REWARD = 2 * LOSE_REWARD;
    final static private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static boolean debug = false;
    private final OXOBoard board = new OXOBoard();
    private final ProxyPlayer o, x;
    private final boolean watch, o_human, x_human;
    private boolean gameover = false;
    private int turn = OXOBoard.O;

    public Arena(ProxyPlayer o, ProxyPlayer x, boolean o_human, boolean x_human, boolean watch) {

        this.o = o;
        this.x = x;
        this.x_human = x_human;
        this.o_human = o_human;
        this.watch = watch || o_human || x_human;
        o.newGame(OXOBoard.O, x);
        x.newGame(OXOBoard.X, o);

        if (debug) System.out.println("ARENA: Created " + this);
    }

    /**
     * Play the whole game.
     */
    public void playGame() {

        boolean cont;
        do {
            cont = playTurn();
        } while (cont);
    }

    public int getWinner() {

        if (gameover) {
            return turn;
        } else {
            throw new IllegalStateException("Nobody wins 'til the game is over!");
        }
    }

    /**
     * Play a single turn.
     * Returns true if game should continue.
     */
    public boolean playTurn() {

        if (!gameover) {
            final ProxyPlayer active_player = turn == OXOBoard.O ? o : x;
            final ProxyPlayer other_player = turn == OXOBoard.O ? x : o;
            final boolean active_human = turn == OXOBoard.O ? o_human : x_human;

            try {
                if (watch) {
                    System.out.println(board.getBoardDisplay());
                    if (OXOBoard.O == turn) {
                        System.out.println("O to play");
                    } else {
                        System.out.println("X to play");
                    }
                }

                // Show board to players
                active_player.showBoard(board.getBoardString(turn));
                other_player.showBoard(board.getBoardString(OXOBoard.otherPlayer(turn)));

                int move = -1;
                if (active_human) {
                    // Get move
                    do {
                        System.out.println("Enter a move (0-8)");
                        try {
                            move = Integer.parseInt(in.readLine().trim());
                        } catch (NumberFormatException | NullPointerException | StringIndexOutOfBoundsException e) {
                            System.out.println("Invalid move.");
                        }
                    } while (move < 0 || move > 8);
                    active_player.forceMove(move);
                } else {
                    move = active_player.getMove();
                }

                other_player.showMove(move);
                if (watch) {
                    System.out.println("Played location " + move);
                }
                if (debug) {
                    System.out.println("[ARENA: " + this + "] player '" + active_player + "' played " + move);
                }
                board.playPiece(move, turn);

                // Show board to players
                active_player.showBoard(board.getBoardString(turn));
                other_player.showBoard(board.getBoardString(OXOBoard.otherPlayer(turn)));

                if (board.checkForWin(turn)) {
                    if (watch) System.out.println((OXOBoard.O == turn ? 'O' : 'X') + " has won!");
                    if (debug) {
                        System.out.println("ARENA: " + this + "\t" + (OXOBoard.O == turn ? 'O' : 'X') + " has won!");
                    }

                    active_player.reward(WIN_REWARD);
                    other_player.reward(LOSE_REWARD);
                    active_player.showReward(LOSE_REWARD);
                    other_player.showReward(WIN_REWARD);

                    active_player.gameOver();
                    other_player.gameOver();

                    gameover = true;
                    return false;
                } else if (board.checkForDraw()) {
                    // Only applies for deterministic version
                    turn = OXOBoard.EMPTY;

                    active_player.reward(DRAW_REWARD);
                    other_player.reward(DRAW_REWARD);
                    active_player.showReward(DRAW_REWARD);
                    other_player.showReward(DRAW_REWARD);

                    active_player.gameOver();
                    other_player.gameOver();

                    gameover = true;
                    return false;
                } else {
                    turn = OXOBoard.otherPlayer(turn);
                    active_player.reward(DEFAULT_REWARD);
                    other_player.reward(DEFAULT_REWARD);
                    active_player.showReward(DEFAULT_REWARD);
                    other_player.showReward(DEFAULT_REWARD);

                    active_player.playOn();
                    other_player.playOn();

                    return true;
                }

            } catch (IllegalLocationException e) {
                // Disqualify player
                if (debug) {
                    System.out.println("ARENA: " + this + "\t" + active_player + " disqualified for making illegal move!");
                }
                active_player.reward(DISQUALIFY_REWARD);
                other_player.showReward(DISQUALIFY_REWARD);
                // Make turn show correct winner
                turn = OXOBoard.otherPlayer(turn);
                gameover = true;
                return false;
            } catch (InvalidPlayerResponseException e) {
                // Disqualify player
                if (debug) {
                    System.out.println("ARENA: " + this + "\t" + active_player + " disqualified for sending an invalid response!");
                }
                e.printStackTrace();
                active_player.reward(DISQUALIFY_REWARD);
                other_player.showReward(DISQUALIFY_REWARD);
                // Make turn show correct winner
                turn = OXOBoard.otherPlayer(turn);
                gameover = true;
                return false;
            } catch (IOException e) {
                // Disqualify player
                if (debug) {
                    System.out.println("ARENA: " + this + "\t" + active_player + " disqualified for generating IOException!");
                }
                active_player.reward(DISQUALIFY_REWARD);
                other_player.showReward(DISQUALIFY_REWARD);
                // Make turn show correct winner
                turn = OXOBoard.otherPlayer(turn);
                gameover = true;
                return false;
            }
        } else {
            // Already game over
            throw new IllegalStateException("Can't play game once it is over!");
        }
    }

    public String toString() {

        return o + " vs " + x;
    }

}
