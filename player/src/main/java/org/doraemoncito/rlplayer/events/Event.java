package org.doraemoncito.rlplayer.events;

/**
 * An event is a state-action-reward-state_prime grouping.
 *
 * @author josehernandez
 */
public class Event {

    /*
     * The total number of board states is the number of permutations of 3 elements in groups of 9.  The three elements
     * are a nought, cross or an empty space. 3^9 = 19683
     */
    public static final int NUM_STATES = 19683;
    public static final int NUM_ACTIONS = 9;

    public static final int THIS_STATE = 0;
    public static final int NEXT_STATE = 1;

    public static final int SOURCE_PLAYER = 0;
    public static final int SOURCE_OPPONENT = 1;

    private static final String DEFAULT_STATE = "_________";
    protected String[] state = {DEFAULT_STATE, DEFAULT_STATE};
    protected int[][] stateActionHash = {{0, 0}, {0, 0}};
    protected int action = 0;
    private int reward = 0;
    private int oppReward = 0;

    private int eventSource = SOURCE_PLAYER;

    public Event(String state, int action, int reward, String nextState) {

        setState(THIS_STATE, state);
        setState(NEXT_STATE, state);
        setAction(action);
        setReward(reward);
    }

    public Event() {
    }

    public int getStateHash(int which) {

        return stateActionHash[which][0];
    }

    public int getActionHash() {

        return stateActionHash[THIS_STATE][1];
    }

    public String getState(int which) {

        return state[which];
    }

    /**
     * Stores a board state in the as a string of nine characters where the
     * characters can be '_', 'f' or 'e'.
     *
     * @param state board state to store.
     */
    public void setState(int which, String state) {

        this.state[which] = state;
        this.stateActionHash[which] =
            calcStateActionHash(this.state[which], this.action);
    }

    public int getAction() {

        return action;
    }

    public void setAction(int action) {

        this.action = action;
        this.stateActionHash[THIS_STATE] =
            calcStateActionHash(this.state[THIS_STATE], this.action);
    }

    public int getOppReward() {

        return oppReward;
    }

    public void setOppReward(int oppReward) {

        this.oppReward = oppReward;
    }

    public int getReward() {

        return reward;
    }

    public void setReward(int reward) {

        this.reward = reward;
    }

    /**
     * Inverts the event thus turning an opponents move into a players
     * move so that we can learn from opponent's moves.
     */
    public void invert() {

        setState(Event.THIS_STATE, getState(Event.THIS_STATE).
            replace('f', 't').replace('e', 'f').replace('t', 'e'));

        setState(Event.NEXT_STATE, getState(Event.NEXT_STATE).
            replace('f', 't').replace('e', 'f').replace('t', 'e'));

        int tmpReward = oppReward;
        oppReward = reward;
        reward = tmpReward;
    }


    /**
     * Shifts s' into s.
     */
    public void swap() {

        String tmpState = state[0];
        int[] tmpStateActionHash = stateActionHash[0];

        state[0] = state[1];
        stateActionHash[0] = stateActionHash[1];

        state[1] = tmpState;
        stateActionHash[1] = tmpStateActionHash;
    }

    public int getEventSource() {

        return eventSource;
    }

    public void setEventSource(int owner) {

        eventSource = owner;
    }

    protected int[] calcStateActionHash(String state, int action) {

        int[] hash = {0, action};

        for (int i = 0; i < Event.NUM_ACTIONS; i++)
            hash[0] += "_fe".indexOf(state.charAt(i)) * Math.pow(3, i);

        return hash;
    }

}
