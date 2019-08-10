package org.doraemoncito.rlreferee.exceptions;

/**
 * Thrown when an illegal board position is played.
 */
public class IllegalLocationException extends Exception {

    public IllegalLocationException(int location) {

        super("tried to play illegal location " + location);
    }

}
