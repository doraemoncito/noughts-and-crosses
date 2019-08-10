package org.doraemoncito.rlreferee.players;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;

public class DumbRandomPlayer {

    private static final Random random = new Random();

    // IMPORTANT: this main method is required to be able to execute the player as a sub-process
    public static void main(String[] args) throws Exception {

        BufferedReader from_ref = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter to_ref = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        to_ref.println("iam " + DumbRandomPlayer.class.getSimpleName());
        to_ref.flush();

        while (true) {
            final String line = from_ref.readLine();
            to_ref.println("# Received command: '" + line + "'");
            if (line.startsWith("yourturn")) {
                to_ref.println("play " + random.nextInt(9));
                to_ref.flush();
            }
        }
    }

}
