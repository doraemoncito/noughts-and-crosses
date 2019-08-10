package org.doraemoncito.rlplayer.players;

import org.doraemoncito.rlplayer.CommandParser;
import org.doraemoncito.rlplayer.Player;
import org.doraemoncito.rlplayer.ProxyReferee;
import org.doraemoncito.rlplayer.QLearner;
import org.doraemoncito.rlplayer.events.Event;

/**
 * ObservingPlayer is a noughts and crosses player based on the Q-Learning algorithm that learns from its own moves and
 * from watching the opponent move.
 *
 * @author josehernandez
 */
public class ObservingPlayer extends QLearningPlayer {

    public ObservingPlayer(ProxyReferee referee, String playerName) {

        super(referee, playerName);
        qLearner = new QLearner(referee);
    }

    public static void main(String[] args) {

        ProxyReferee referee;

        try {
            referee = new ProxyReferee();
            CommandParser commandParser = new CommandParser();
            Player player = new ObservingPlayer(referee, ObservingPlayer.class.getSimpleName());

            // initiate a conversation between the referee and the player
            commandParser.commandLoop(referee, player);
        } catch (Throwable e) {
            // Oops, something has gone wrong...
            e.printStackTrace();
        }
    }

    public void playOn(boolean continueGame) {

        qLearner.update(event[eventCount]);

        if (event[eventCount].getEventSource() == Event.SOURCE_OPPONENT) {
            event[eventCount].invert();
            qLearner.update(event[eventCount]);
            event[eventCount].invert();
        }

        if (!continueGame) {
            event[eventCount].swap();
            qLearner.updateTerminalState(event[eventCount]);

            if (event[eventCount].getEventSource() == Event.SOURCE_OPPONENT) {
                event[eventCount].invert();
                qLearner.updateTerminalState(event[eventCount]);
            }

            event = null;
        }

        eventCount++;
    }

}
