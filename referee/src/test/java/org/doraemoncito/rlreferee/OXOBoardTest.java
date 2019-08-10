package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.IllegalLocationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class OXOBoardTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OXOBoardTest.class);

    @Test
    public void testBoard() throws IllegalLocationException {

        String boardString;
        OXOBoard board = new OXOBoard();

        boardString = board.getBoardString(OXOBoard.O);
        assertEquals("_________", boardString);
        LOGGER.info("[O] {}", boardString);

        boardString = board.getBoardString(OXOBoard.X);
        assertEquals("_________", boardString);
        LOGGER.info("[X] {}", boardString);

        LOGGER.info("#1 {}", board.getBoardDisplay());

        board.playPiece(1, 1, OXOBoard.O);

        boardString = board.getBoardString(OXOBoard.O);
        assertEquals("____f____", boardString);
        LOGGER.info("[O] {}", boardString);

        boardString = board.getBoardString(OXOBoard.X);
        assertEquals("____e____", boardString);
        LOGGER.info("[X] {}", boardString);

        LOGGER.info("#2 {}", board.getBoardDisplay());

        board.playPiece(0, 0, OXOBoard.X);

        boardString = board.getBoardString(OXOBoard.O);
        assertEquals("e___f____", boardString);
        LOGGER.info("[O] {}", boardString);

        boardString = board.getBoardString(OXOBoard.X);
        assertEquals("f___e____", boardString);
        LOGGER.info("[X] {}", boardString);

        LOGGER.info("#3 {}", board.getBoardDisplay());
    }

}
