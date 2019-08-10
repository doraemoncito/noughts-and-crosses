package org.doraemoncito.rlreferee.players;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;

public class RandomPlayer {

    private static final Random random = new Random();

    // IMPORTANT: this main method is required to be able to execute the player as a sub-process
    public static void main(String[] args) throws Exception {

        BufferedReader from_ref = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter to_ref = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

        to_ref.println("iam RandomPlayer");
        to_ref.flush();

        String board = "_________";
        while (true) {
            final String line = from_ref.readLine();
            if (line.startsWith("board ")) {
                board = line.substring("board ".length());
            } else if (line.startsWith("yourturn")) {
                int move;
                do {
                    move = random.nextInt(9);
                } while (board.charAt(move) != '_');

                to_ref.println("play " + move);
                to_ref.flush();
            }
        }
    }

}
