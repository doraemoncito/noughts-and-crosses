package org.doraemoncito.rlplayer;

import org.doraemoncito.rlplayer.events.Event;
import org.doraemoncito.rlplayer.events.SymmetricEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * A Q-Learning players that uses board symmetries to its advantage.
 *
 * @author josehernandez
 */
public class QSymmetricLearner extends QLearner {

    public QSymmetricLearner(ProxyReferee referee) {

        super(referee);

        m_gamma = (float) 0.65;
        m_k = (float) 0.01;
        m_k_inc = (float) 0.01;
        m_k_lim = (float) 10;
    }

    public void update(Event event) {

        referee.sendComment(" -> UPDATE (" +
            event.getState(Event.THIS_STATE) + ", " +
            event.getAction() + ", " +
            event.getReward() + ") --> " +
            event.getState(Event.NEXT_STATE));

        referee.sendComment(" -> UPDATE (" +
            event.getStateHash(Event.THIS_STATE) + ", " +
            event.getActionHash() + ", " +
            event.getReward() + ") --> " +
            event.getStateHash(Event.NEXT_STATE));

        /* Increament k over a period of time to gradually go from exploration
         * into exploitation.
         */
        if (m_k < m_k_lim) {
            m_k += m_k_inc;
        }

        if (m_visits[event.getStateHash(Event.THIS_STATE)][event.getActionHash()] < Integer.MAX_VALUE) {
            m_visits[event.getStateHash(Event.THIS_STATE)][event.getActionHash()]++;
        }

        referee.sendComment(" -> ACTION  " +
            m_visits[event.getStateHash(Event.THIS_STATE)][event.getActionHash()] +
            "  visits to [" + event.getStateHash(Event.THIS_STATE) + "][" +
            event.getActionHash() + "]");

        /* Calculate alpha using equation 13.11 from Mitchell's book... */
        float alpha = (float) 1 / (1 +
            m_visits[event.getStateHash(Event.THIS_STATE)][event.getActionHash()]);
        float maxReward = (float) 0;

        /* ...then calculate Q^ using 13.10 */
        for (int nextAction = 0; nextAction < Event.NUM_ACTIONS; nextAction++)
            maxReward = Math.max(maxReward,
                m_Q[event.getStateHash(Event.NEXT_STATE)][nextAction]);

        m_Q[event.getStateHash(Event.THIS_STATE)][event.getActionHash()] =
            ((1 - alpha) * m_Q[event.getStateHash(Event.THIS_STATE)][event.getActionHash()]) +
                (alpha * (event.getReward() + m_gamma * maxReward));
    }

    /**
     * Probabilistic action method.
     *
     * @param event board state
     * @return action to take
     */
    public int selectAction(Event event) {

        referee.sendComment(" -> ACTION " +
            event.getState(Event.THIS_STATE) + "  (" +
            event.getStateHash(Event.THIS_STATE) + ")");

        float[] p = new float[Event.NUM_ACTIONS];
        float sumP = 0;

        /* Calculate and sum up each individual k raise to the
         * power of Q^(s,a)).
         */
        for (int aj = 0; aj < Event.NUM_ACTIONS; aj++) {
            if (event.getState(Event.THIS_STATE).charAt(aj) == '_') {
                p[aj] = (float) Math.pow(m_k, m_Q[event.getStateHash(Event.THIS_STATE)][aj]);
                sumP += p[aj];
            } else {
                p[aj] = (float) 0;
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.0000000");

        /* Now calculate the probability of each action and build the
         * intervals by simply adding the probabilities.  (the sum of
         * the probabilities is 1).
         */
        for (int i = 0; i < Event.NUM_ACTIONS; i++) {
            String range = "";

            if (i > 0) {
                p[i] = (p[i] / sumP) + p[i - 1];

                if (p[i] != p[i - 1]) {
                    range = decimalFormat.format(p[i - 1]) + " <-> " +
                        decimalFormat.format(p[i]);
                }
            } else {
                p[i] /= sumP;

                if (p[i] != 0) {
                    range = decimalFormat.format(0) + " <-> " +
                        decimalFormat.format(p[i]);
                }
            }

            referee.sendComment(" -> ACTION P[" + i + "] = " + range);
        }

        int action = 0;
        float rdnMove = random.nextFloat();

        for (int i = 0; i < Event.NUM_ACTIONS; i++)
            if (rdnMove > p[i]) {
                action = i + 1;
            }

        referee.sendComment(" -> ACTION move: " + rdnMove + " --> action: " + action);

        return ((SymmetricEvent) event).getRealAction(action);
    }

    public void retrain() {

    }

    public void saveToFile(String filename) throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter(new File(filename)));

        DecimalFormat decimalFormat = new DecimalFormat("000.0000");

        for (int i = 0; i < Event.NUM_STATES; i++) {
            for (int j = 0; j < Event.NUM_ACTIONS; j++)
                out.write(decimalFormat.format(m_Q[i][j]) + "  ");

            out.newLine();
        }
        out.close();
    }

}
