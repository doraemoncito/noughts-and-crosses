package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;
import org.doraemoncito.rlreferee.players.RandomPlayer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ArenaTest {

    @Test(timeout = 2000L)
    public void testArena() throws IOException, InvalidPlayerResponseException {

        final String randomPlayerName = RandomPlayer.class.getName();
        Arena arena = new Arena(
            new ProxyPlayer(randomPlayerName, 0, 2),
            new ProxyPlayer(randomPlayerName, 1, 2),
            false,
            false,
            true);
        arena.playGame();
        final int winner = arena.getWinner();
    }

}