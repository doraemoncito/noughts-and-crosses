package org.doraemoncito.rlplayer.events;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;

public class SymmetricEventTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SymmetricEventTest.class);

    @Test
    public void testSymmetricEvent____eff__ForAction5ReturnsAction3() {

        SymmetricEvent se = new SymmetricEvent();
        se.setDebug(true);
        LOGGER.info("STATE: ____eff__  ACTION: 5");
        final int[] stateActionHash = se.calcStateActionHash("____eff__", 5);
        LOGGER.info("STATE: {}", stateActionHash);
        assertArrayEquals(new int[]{6750, 3}, stateActionHash);
    }

    @Test
    public void testSymmetricEvent_f__e___fForAction1ReturnsAction3() {

        SymmetricEvent se = new SymmetricEvent();
        se.setDebug(true);
        LOGGER.info("STATE: _f__e___f  ACTION: 1");
        final int[] stateActionHash = se.calcStateActionHash("_f__e___f", 1);
        LOGGER.info("STATE: {}", stateActionHash);
        assertArrayEquals(new int[]{6750, 3}, stateActionHash);
    }

}