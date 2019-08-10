package org.doraemoncito.rlreferee;

import org.doraemoncito.rlreferee.exceptions.InvalidPlayerResponseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class RLReferee {

    final static private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final int DEFAULT_ROUNDS = 10;
    private static boolean watch_matches = false;
    private final ProxyPlayer[] players;
    private final boolean[] humans;
    private final int[] wins, losses, draws;
    private final int rounds;
    private int league, round, o, x = 1;

    public RLReferee(String playerList, int rounds) throws IOException, InvalidPlayerResponseException {

        this(Files.readAllLines(new File(playerList).toPath(), Charset.defaultCharset()), rounds);
    }

    public RLReferee(List<String> playerCommands, int rounds) throws IOException, InvalidPlayerResponseException {

        players = new ProxyPlayer[playerCommands.size()];
        humans = new boolean[playerCommands.size()];

        for (int i = 0; i < players.length; i++) {
            players[i] = new ProxyPlayer(playerCommands.get(i), i, players.length);
            humans[i] = false;
        }

        this.rounds = rounds;

        wins = new int[players.length];
        losses = new int[players.length];
        draws = new int[players.length];
    }

    private static int getNumber(final int defaultNumber, final String n) {

        try {
            return Integer.parseInt(n.substring(n.indexOf(' ')).trim());
        } catch (NumberFormatException | NullPointerException | StringIndexOutOfBoundsException e) {
            return defaultNumber;
        }
    }

    public static void main(String[] args) throws Exception {

        if (1 == args.length) {
            RLReferee ref = new RLReferee(args[0], DEFAULT_ROUNDS);
            ref.shell();
        } else if (2 == args.length) {
            RLReferee ref = new RLReferee(args[0], Integer.parseInt(args[1].trim()));
            ref.shell();
        } else {
            System.err.println("\nUsage:\tRLReferee <playerlistfile> [rounds]");
        }
    }

    public void playNextMatch() {

        Arena arena = new Arena(players[o], players[x], humans[o], humans[x], watch_matches);
        if (watch_matches) {
            System.out.println("Match:\t" + arena);
        }
        arena.playGame();
        final int winner = arena.getWinner();
        if (OXOBoard.O == winner) {
            if (watch_matches) {
                System.out.println("Winner:\t" + players[o]);
            }
            wins[o]++;
            losses[x]++;
        } else if (OXOBoard.X == winner) {
            if (watch_matches) {
                System.out.println("Winner:\t" + players[x]);
            }
            wins[x]++;
            losses[o]++;
        } else if (OXOBoard.EMPTY == winner) {
            draws[o]++;
            draws[x]++;
        }

        advanceCounters();
    }

    public void playRound() {

        final int cur_round = round;
        while (cur_round == round) {
            playNextMatch();
        }
    }

    public void playLeague() {

        final int cur_league = league;
        while (cur_league == league) {
            playNextMatch();
        }

        shellShowScores();

        for (int i = 0; i < wins.length; i++) {
            draws[i] = wins[i] = 0;
        }
    }

    public void shell() throws IOException {

        System.out.println("\nWelcome to the RLReferee Shell");
        System.out.println("A league consists of " + rounds + " rounds\n");

        boolean done = false;

        do {
            System.out.print("\n> ");
            System.out.flush();

            final String input = in.readLine().toLowerCase();

            if (input.equals("help")) {
                shellHelp();
            } else if (input.equals("quit")) {
                done = shellQuit();
            } else if (input.equals("watch")) {
                shellWatch();
            } else if (input.equals("debug arena")) {
                shellDebugArena();
            } else if (input.equals("debug player")) {
                shellDebugPlayer();
            } else if (input.equals("scores")) {
                shellShowScores();
            } else if (input.startsWith("match")) {
                shellMatch(getNumber(1, input));
            } else if (input.startsWith("round")) {
                shellRound(getNumber(1, input));
            } else if (input.startsWith("league")) {
                shellLeague(getNumber(1, input));
            } else if (input.startsWith("human")) {
                shellHuman(getNumber(-1, input));
            } else {
                shellError();
            }
        } while (!done);
    }

    private void shellHelp() {

        System.out.println("\nRLReferee shell help\n");
        System.out.println("debug arena\tToggle debugging of Arena module.");
        System.out.println("debug player\tToggle debugging of Player module.");
        System.out.println("help\tPrint this message.");
        System.out.println("human n\tToggle human control on player n");
        System.out.println("human\tShow which players are human controlled");
        System.out.println("league n\tPlay to the end of the next (n) league(s).");
        System.out.println("match n\tPlay the next (n) match(es).");
        System.out.println("quit\tExit the program");
        System.out.println("round n\tPlay to the end of the next (n) round(s).");
        System.out.println("scores\nShow scores");
        System.out.println("watch\tToggle watching of matches\n");
        System.out.println("\nn\tIndicates command can optionally be followed by a count.");
    }

    private void shellError() {

        System.out.println("Unknown command\nUse 'help' to get help.");
    }

    private void shellMatch(int n) {

        System.out.println("Playing " + n + " match(es)");
        for (int i = 0; i < n; i++) {
            playNextMatch();
        }
    }

    private void shellRound(int n) {

        System.out.println("Playing " + n + " round(s)");
        for (int i = 0; i < n; i++) {
            playRound();
        }
    }

    private void shellLeague(int n) {

        System.out.println("Playing " + n + " league(s)");
        for (int i = 0; i < n; i++) {
            playLeague();
        }
    }

    private void shellHuman(int n) {

        if (-1 == n) {
            System.out.println("Players controlled by humans?");
            for (int i = 0; i < players.length; i++)
                System.out.println(players[i] + "\t" + humans[i]);
        } else {
            try {
                humans[n] = !humans[n];
                if (humans[n]) {
                    System.out.println("Player " + players[n] + " now human controlled.");
                } else {
                    System.out.println("Player " + players[n] + " now not human controlled.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid player! (Remember numbers go from 0)");
            }
        }
    }

    private boolean shellQuit() throws IOException {

        System.out.println("Really quit? (y/N)");
        final String input = in.readLine().toLowerCase();
        return input.length() > 0 && 'y' == input.charAt(0);
    }

    private void shellWatch() {

        watch_matches = !watch_matches;
        if (watch_matches) {
            System.out.println("Watching enabled");
        } else {
            System.out.println("Watching disabled");
        }
    }

    private void shellDebugArena() {

        Arena.debug = !Arena.debug;
        if (Arena.debug) {
            System.out.println("Arena debugging enabled");
        } else {
            System.out.println("Arena debugging disabled");
        }
    }

    private void shellDebugPlayer() {

        ProxyPlayer.debug = !ProxyPlayer.debug;
        if (ProxyPlayer.debug) {
            System.out.println("Player debugging enabled");
        } else {
            System.out.println("Player debugging disabled");
        }
    }

    private void shellShowScores() {

        System.out.println("Results for league " + league);
        System.out.println(String.format("%-30s%-10s%-10s%-10s%-10s%-10s", "Player Name", "Won", "Lost", "Drawn", "Points", "% Wins"));

        LeagueEntry[] table = new LeagueEntry[players.length];
        for (int i = 0; i < players.length; i++)
            table[i] = new LeagueEntry(players[i].toString(), wins[i], losses[i], draws[i]);

        Arrays.sort(table);

        for (int i = players.length - 1; i >= 0; i--)
            System.out.println(table[i]);

        System.out.println();
    }

    /**
     * Advance the counters ready for the next match.
     */
    private void advanceCounters() {
        // There's probably a smarter way of doing this.
        x++;

        if (players.length == x) {
            x = 0;
            o++;
        }

        if (players.length == o) {
            o = 0;
            round++;
        }

        if (rounds == round) {
            round = 0;
            league++;
        }

        if (o == x) {
            advanceCounters();
        }
    }

    private static class LeagueEntry implements Comparable<LeagueEntry> {

        private final String name;
        private final int wins, losses, draws;

        public LeagueEntry(String name, int wins, int losses, int draws) {

            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.draws = draws;
        }

        @Override
        public int compareTo(LeagueEntry other) {

            if (wins < other.wins) {
                return -1;
            } else if (wins > other.wins) {
                return 1;
            } else {
                return Integer.compare(draws, other.draws);
            }
        }

        public String toString() {

            return String.format("%-30s%-10s%-10s%-10s%-10s%.0f%%", name, wins, losses, draws, (wins * 3 + draws), (100f * wins) / (wins + losses + draws));
        }

    }

}
