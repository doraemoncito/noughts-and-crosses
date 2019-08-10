package org.doraemoncito.rlplayer;

import org.doraemoncito.rlplayer.players.FixedPlayer;
import org.doraemoncito.rlplayer.players.QLearningPlayer;
import org.doraemoncito.rlreferee.RLReferee;
import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ChampionshipTest {

    @Test
    public void testPlayLeague() throws IOException, InvalidPlayerResponseException {

        File playerList = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("playerlist.txt")).getFile());
        final RLReferee rlReferee = new RLReferee(playerList.getAbsolutePath(), 100);
        rlReferee.playLeague();
    }

    @Test
    public void testFixedPlayerAgainstItself() throws IOException, InvalidPlayerResponseException {

        final String fixedPlayerName = FixedPlayer.class.getName();
        final RLReferee rlReferee = new RLReferee(Arrays.asList(fixedPlayerName, fixedPlayerName), 100);
        rlReferee.playNextMatch();
    }

    @Test
    public void testFixedPlayerAgainstQLearningPlayer() throws IOException, InvalidPlayerResponseException {

        final String fixedPlayerName = FixedPlayer.class.getName();
        final String qLearningPlayerName = QLearningPlayer.class.getName();
        final RLReferee rlReferee = new RLReferee(Arrays.asList(fixedPlayerName, qLearningPlayerName), 100);
        rlReferee.playLeague();
    }

}
