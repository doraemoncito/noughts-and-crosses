package org.doraemoncito.rlreferee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RunMatch {

    private static final Runtime rt = Runtime.getRuntime();

    public static int playMatch(String player1_cmd, String player2_cmd) throws IOException {

        Process player2 = rt.exec(player2_cmd);
        BufferedReader from_player2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter to_player2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(player2.getOutputStream())));

        return 0;
    }

}
