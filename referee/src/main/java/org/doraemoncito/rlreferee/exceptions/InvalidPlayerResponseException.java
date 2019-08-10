package org.doraemoncito.rlreferee.exceptions;

public class InvalidPlayerResponseException extends Exception {

    public InvalidPlayerResponseException(String prefix, String response) {

        super("Expected client response starting with \"" + prefix + "\" but got \"" + response + "\"");
    }

    public InvalidPlayerResponseException(String msg) {

        super(msg);
    }

}
