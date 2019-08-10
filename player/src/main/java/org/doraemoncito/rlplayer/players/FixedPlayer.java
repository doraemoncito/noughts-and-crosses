package org.doraemoncito.rlplayer.players;

import org.doraemoncito.rlplayer.CommandParser;
import org.doraemoncito.rlplayer.Player;
import org.doraemoncito.rlplayer.ProxyReferee;
import org.doraemoncito.rlplayer.events.Event;

/**
 * A player that chooses the first available position starting from the top left in the usual text book reading order.
 *
 * @author josehernandez
 */
public class FixedPlayer extends Player {

    String board = "_________";

    public FixedPlayer(ProxyReferee referee, String playerName) {

        super(referee, playerName);
    }

    public static void main(String[] args) {

        ProxyReferee referee;

        try {
            referee = new ProxyReferee();
            Player player = new FixedPlayer(referee, FixedPlayer.class.getSimpleName());

            // initiate a conversation between the referee and the player
            new CommandParser().commandLoop(referee, player);
        } catch (Throwable e) {
            // Oops, something has gone wrong...
            e.printStackTrace();
        }
    }

    /**
     * Tell player that we are about to start a new game, playing side 'side', against
     * player 'opponentId' who is called 'opponentName'.
     *
     * @param side         The side this agent is playing; can be SIDE_O or SIDE_X.  The player playing
     *                     SIDE_O gets to go first.
     * @param opponentName The opponent's name.
     * @param opponentId   The opponent's playerId.
     */
    public void newGame(int side, String opponentName, int opponentId) {

        super.newGame(side, opponentName, opponentId);
    }

    /**
     * Shows the player the current board status.
     *
     * @param board current board status  as a 9 character string.
     */
    public void board(String board) {

        this.board = board;
    }

    /**
     * Asks the player to make a move.  This message always comes after a board message.
     *
     * @return The player's chosen move.
     */
    @Override
    public int yourMove() {

        for (int i = 0; i < Event.NUM_ACTIONS; i++)
            if (this.board.charAt(i) == '_') {
                return i;
            }

        return 0;
    }

    /**
     * Shows the player the move his opponent just made.  This message
     * always comes after a board message.
     *
     * @param action The move the player's opponent just made.
     */
    public void opponentMove(int action) {

    }

    /**
     * Forces player to make a move.  This message is sent when a human is
     * moving for a player.  The client is free to ignore the message, or do
     * an update as if the client just requested that move.
     *
     * @param action Move being forced upon the player.
     */
    public void forceMove(int action) {

    }

    /**
     * Player is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public void reward(int value) {

    }

    /**
     * Player's opponent is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public void opponentReward(int value) {

    }

    public void playOn(boolean continueGame) {

    }

}
