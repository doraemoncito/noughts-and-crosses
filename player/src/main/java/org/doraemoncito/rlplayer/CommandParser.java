package org.doraemoncito.rlplayer;

import java.io.IOException;

/**
 * The reinforcement learning environment.  Interaction with the referee is managed here.
 *
 * @author josehernandez
 */
public class CommandParser {

    public int commandLoop(ProxyReferee referee, Player player) throws IOException {

        while (true) {
            final String line = referee.readCommand();

            if (line.startsWith("hello")) {
                String[] parameters = line.split("\\s");
                String userName = player.hello(Integer.parseInt(parameters[1]), Integer.parseInt(parameters[3]));
                // Send greeting to referee
                referee.sendCommand("iam " + userName);
            } else if (line.startsWith("newgame")) {
                String[] parameters = line.split("\\s");
                player.newGame((parameters[1].equals("o")) ? Player.SIDE_O : Player.SIDE_X, parameters[2], Integer.parseInt(parameters[3]));
            } else if (line.startsWith("board")) {
                player.board(line.substring("board ".length()));
            } else if (line.startsWith("oppmove")) {
                player.opponentMove(Integer.parseInt(line.substring("oppmove ".length())));
            } else if (line.startsWith("yourturn")) {
                referee.sendCommand("play " + player.yourMove());
            } else if (line.startsWith("oppreward")) {
                player.opponentReward(Integer.parseInt(line.substring("oppreward ".length())));
            } else if (line.startsWith("reward")) {
                player.reward(Integer.parseInt(line.substring("reward ".length())));
            } else if (line.startsWith("playon")) {
                player.playOn(true);
            } else if (line.startsWith("gameover")) {
                player.playOn(false);
            } else if (line.startsWith("quit")) {
                return 0;
            }
        }
    }

}
