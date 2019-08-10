package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyPlayer {

    public static boolean debug = false;
    public final int player_id, players;
    public final String name;
    private final Process process;
    private final BufferedReader from_player;
    private final PrintWriter to_player;

    public ProxyPlayer(String cmd, int player_id, int players) throws IOException, InvalidPlayerResponseException {

        this.player_id = player_id;
        this.players = players;

        // Create process and IO streams
        List<String> jvmArgs = Collections.singletonList("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005");
        process = exec(cmd, Collections.emptyList(), Collections.emptyList());

        from_player = new BufferedReader(new InputStreamReader(process.getInputStream()));
        to_player = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));

        if (!process.isAlive()) {
            String errorMessage = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                .lines()
                .collect(Collectors.joining("\n"));
            throw new RuntimeException(errorMessage);
        }

        // Say hello to player!
        to_player.println("hello " + player_id + " of " + players);
        to_player.flush();

        name = splitResponse("iam", getResponse());

        System.out.println("Activated player: " + this);
    }

    public Process exec(String className, List<String> jvmArgs, List<String> args) throws IOException {

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);
        return new ProcessBuilder(command).start();
    }

    /**
     * Tell the player it has a new opponent.
     */
    public void newGame(int side, ProxyPlayer o) {

        if (OXOBoard.O == side) {
            tellPlayer("newgame o " + o);
        } else {
            tellPlayer("newgame x " + o);
        }
    }

    /**
     * Get a move from the player.
     */
    public int getMove() throws InvalidPlayerResponseException, IOException {

        tellPlayer("yourturn");

        final String move_string = splitResponse("play", getResponse());
        try {
            final int move = Integer.parseInt(move_string);
            if (debug) System.out.println("PLAYER: " + this + " played location " + move);
            return move;
        } catch (NumberFormatException e) {
            throw new InvalidPlayerResponseException("Expected client reponse to be number, got " + move_string);
        }
    }

    /**
     * Show player the move opponent just made.
     */

    public void showMove(int n) {

        tellPlayer("oppmove " + n);
    }

    /**
     * Force player to make a move.  (Used when a human is playing instead of a
     * player.)
     */
    public void forceMove(int n) {

        tellPlayer("forcemove " + n);
    }

    public void reward(int n) {

        tellPlayer("reward " + n);
    }

    public void showReward(int n) {

        tellPlayer("oppreward " + n);
    }

    /**
     * Show the board to the player.
     */
    public void showBoard(String board) {

        tellPlayer("board " + board);
    }

    /**
     * Send a line to the player.
     */
    public void tellPlayer(String message) {

        if (debug) {
            System.out.println("PLAYER: " + this + " told \"" + message + "\"");
        }
        to_player.println(message);
        to_player.flush();
    }

    public void gameOver() {

        tellPlayer("gameover");
    }

    public void playOn() {

        tellPlayer("playon");
    }

    /**
     * Give a string that represents this player.
     */
    public String toString() {

        return name + " " + player_id;
    }

    private String getResponse() throws IOException, InvalidPlayerResponseException {

        while (true) {
            final String response = from_player.readLine();
            if (null != response) {
                if (response.startsWith("#")) {
                    System.out.println("PLAYER: " + this + " \"comments '" + response + "\"");
                } else {
                    if (debug) System.out.println("PLAYER: " + this + " says \"" + response + "\"");
                    return response;
                }
            } else {
                throw new InvalidPlayerResponseException("Client dead?");
            }
        }
    }

    private String splitResponse(final String prefix, final String response) throws InvalidPlayerResponseException {

        if (debug) {
            System.out.println("[PLAYER: " + this + "] response is \"" + response + "\"");
            System.out.println("[PLAYER: " + this + "] looking for response \"" + prefix + "\"");
        }

        if (response.startsWith(prefix)) {
            return response.substring(prefix.length()).trim();
        } else {
            throw new InvalidPlayerResponseException(prefix, response);
        }
    }

}
