package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;
import org.doraemoncito.rlreferee.players.DumbRandomPlayer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ProxyPlayerTest {

    @Test
    public void testPlayerProxy() throws IOException, InvalidPlayerResponseException {

        final String dumbPlayerName = DumbRandomPlayer.class.getName();
        ProxyPlayer test_player = new ProxyPlayer(dumbPlayerName, 1, 1);
        OXOBoard test_board = new OXOBoard();
        test_player.showBoard(test_board.getBoardString(OXOBoard.O));
        final int move = test_player.getMove();
        assertTrue("Player returned a valid move", (move >= 0) && (move <= 9));
    }

}