package org.doraemoncito.rlplayer.players;

import org.doraemoncito.rlplayer.CommandParser;
import org.doraemoncito.rlplayer.Player;
import org.doraemoncito.rlplayer.ProxyReferee;
import org.doraemoncito.rlplayer.QLearner;
import org.doraemoncito.rlplayer.QSymmetricLearner;
import org.doraemoncito.rlplayer.events.Event;
import org.doraemoncito.rlplayer.events.SymmetricEvent;

/**
 * SymmetricPlayer is a Q learning player which learns from its own moves using board symmetries to its advantage.
 *
 * @author josehernandez
 */
public class SymmetricPlayer extends Player {

    protected QLearner qLearner;
    protected Event[] event = null;
    int eventCount = 0;
    int side = SIDE_O;
    private int m_whichState = Event.THIS_STATE;

    /**
     * @param playerName
     */
    public SymmetricPlayer(ProxyReferee referee, String playerName) {

        super(referee, playerName);
        qLearner = new QSymmetricLearner(referee);
    }

    public static void main(String[] args) {

        ProxyReferee referee;

        try {
            referee = new ProxyReferee();
            CommandParser commandParser = new CommandParser();
            Player player = new SymmetricPlayer(referee, SymmetricPlayer.class.getSimpleName());

            // initiate a conversation between the referee and the player
            commandParser.commandLoop(referee, player);
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

        this.side = side;
        m_whichState = Event.THIS_STATE;
        eventCount = 0;

        event = new SymmetricEvent[Event.NUM_ACTIONS];
        for (int i = 0; i < Event.NUM_ACTIONS; i++)
            event[i] = new SymmetricEvent();
    }

    /**
     * Shows the player the current board status.
     *
     * @param board current board status expressed as a 9 character string.
     */
    public void board(String board) {
        // Set board status
        event[eventCount].setState(m_whichState, board);

        if (m_whichState == Event.THIS_STATE) {
            m_whichState = Event.NEXT_STATE;
        } else {
            m_whichState = Event.THIS_STATE;
        }
    }

    /**
     * Asks the player to make a move.  This message always comes after a
     * board message.
     *
     * @return The player's chosen move.
     */
    public int yourMove() {

        event[eventCount].setEventSource(Event.SOURCE_PLAYER);
        event[eventCount].setAction(qLearner.selectAction(event[eventCount]));
        referee.sendComment("ACTION " + event[eventCount].getAction());

        return event[eventCount].getAction();
    }

    /**
     * Shows the player the move his opponent just made.  This message
     * always comes after a board message.
     *
     * @param action The move the player's opponent just made.
     */
    public void opponentMove(int action) {

        event[eventCount].setEventSource(Event.SOURCE_OPPONENT);
        event[eventCount].setAction(action);
    }

    @Override
    public void forceMove(int action) {

    }

    /**
     * Player is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public void reward(int value) {

        event[eventCount].setReward(value);
    }

    /**
     * Player's opponent is given a reward for last move made.
     *
     * @param value Reward value.
     */
    public void opponentReward(int value) {

        event[eventCount].setOppReward(value);
    }

    public void playOn(boolean continueGame) {

        qLearner.update(event[eventCount]);

        if (!continueGame) {
            event[eventCount].swap();
            qLearner.updateTerminalState(event[eventCount]);
            event = null;
        }

        eventCount++;
    }

}
