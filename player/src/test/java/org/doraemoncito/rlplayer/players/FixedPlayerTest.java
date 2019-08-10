package org.doraemoncito.rlplayer.players;

import org.doraemoncito.rlplayer.CommandParser;
import org.doraemoncito.rlplayer.ProxyReferee;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FixedPlayerTest {

    public static final int EXIT_CODE_SUCCESS = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedPlayerTest.class);

    @Test
    public void testFixedPlayerSelectsTheFirstAvailableSequentialPosition() {

        final FixedPlayer player = new FixedPlayer(null, FixedPlayer.class.getSimpleName());
        String board = "_________";

        for (int i = 0; i < 9; i++) {
            player.board(board);
            int move = player.yourMove();
            assertEquals(i, move);
            final StringBuilder builder = new StringBuilder(board);
            builder.setCharAt(i, 'e');
            board = builder.toString();
        }
    }

    @Test(timeout = 1000L)
    public void testRefereePlayerCommunicationsByReplayingMatch() throws IOException {

        final ProxyReferee referee = Mockito.mock(ProxyReferee.class);

        when(referee.readCommand()).thenReturn(
            "newgame x FixedPlayer 0",
            "board _________",
            "oppmove 0",
            "board e________",
            "reward 0",
            "oppreward 0",
            "playon",
            "board e________",
            "yourturn",
            "reward -20",
            "quit");

        final FixedPlayer player = new FixedPlayer(referee, FixedPlayer.class.getSimpleName());

        final int exitCode = new CommandParser().commandLoop(referee, player);
        assertEquals(EXIT_CODE_SUCCESS, exitCode);
    }

}