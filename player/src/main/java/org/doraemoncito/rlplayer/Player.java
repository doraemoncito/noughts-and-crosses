package org.doraemoncito.rlplayer;

/**
 * @author josehernandez
 * <p>
 * Base class for all Tic-Tac-Toe Q reinforcemement learning agents.
 */
public abstract class Player {

    public static final byte SIDE_O = 0;
    public static final byte SIDE_X = 1;
    protected int side = SIDE_O;
    protected ProxyReferee referee = null;
    private String playerName;
    private int playerId;
    private String opponentName = "unknown";
    private int opponentId = 0;
    private int numPlayers;

    /**
     * Create a new Q-learning reinforcement agent.
     *
     * @param playerName CommandParser's name.
     */
    public Player(ProxyReferee referee, String playerName) {

        this.referee = referee;
        this.playerName = playerName;
    }

    public String getName() {

        return playerName;
    }

    /**
     * Initial welcome sent to player n of m players.
     *
     * @param playerId Number n of m total players.
     * @return Returns the agent name.
     */
    public String hello(int playerId, int numPlayers) {

        this.playerId = playerId;
        this.numPlayers = numPlayers;

        return this.playerName;
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

        this.side = side;
        this.opponentName = opponentName;
        this.opponentId = opponentId;
    }

    /**
     * Shows the player the current board status.
     *
     * @param board current board status  as a 9 character string.
     */
    public abstract void board(String board);

    /**
     * Asks the player to make a move.  This message always comes after a board message.
     *
     * @return The player's chosen move.
     */
    public abstract int yourMove();

    /**
     * Shows the player the move his opponent just made.  This message always comes after a board message.
     *
     * @param action The move the player's opponent just made.
     */
    public abstract void opponentMove(int action);

    /**
     * Forces player to make a move.  This message is sent when a human is
     * moving for a player.  The client is free to ignore the message, or do
     * an update as if the client just requested that move.
     *
     * @param action Move being forced upon the player.
     */
    public abstract void forceMove(int action);

    /**
     * Player is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public abstract void reward(int value);

    /**
     * Player's opponent is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public abstract void opponentReward(int value);

    public abstract void playOn(boolean continueGame);

}
