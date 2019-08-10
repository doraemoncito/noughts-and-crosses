package org.doraemoncito.rlplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * A proxy referee is a stub that manages the interaction between the player and the actual referee thus abstracting
 * away the physical communication handling.  This implementation used IO stream redirection to communicate with the
 * referee.
 *
 * @author josehernandez
 */
public class ProxyReferee {

    private boolean debug = false;
    private BufferedReader m_fromReferee;
    private PrintWriter m_toReferee;

    public ProxyReferee() {

        m_fromReferee = new BufferedReader(new InputStreamReader(System.in));
        m_toReferee = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
    }

    public void sendComment(String comment) {

        if (debug) {
            sendCommand("#" + comment);
        }
    }

    public String readCommand() {

        String line = "";

        try {
            line = m_fromReferee.readLine();
        } catch (IOException e) {
            line = e.getMessage();
        }

        sendComment("received command -> " + line);

        return line;
    }

    public void sendCommand(String command) {

        m_toReferee.println(command);
        m_toReferee.flush();
    }

}
