package org.doraemoncito.rlplayer.players;

import org.doraemoncito.rlplayer.CommandParser;
import org.doraemoncito.rlplayer.Player;
import org.doraemoncito.rlplayer.ProxyReferee;
import org.doraemoncito.rlplayer.QSymmetricLearner;
import org.doraemoncito.rlplayer.events.Event;

/**
 * SymmetricPlayer is a Q learning player which learns from its own moves using board symmetries to its advantage.
 *
 * @author josehernandez
 */
public class ObservingSymmetricPlayer extends SymmetricPlayer {

    public ObservingSymmetricPlayer(ProxyReferee referee, String playerName) {

        super(referee, playerName);
        qLearner = new QSymmetricLearner(referee);
    }

    public static void main(String[] args) {

        ProxyReferee referee;

        try {
            referee = new ProxyReferee();
            CommandParser commandParser = new CommandParser();
            Player player = new ObservingSymmetricPlayer(referee, ObservingSymmetricPlayer.class.getSimpleName());

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
