package com.mycompany.mavenproject1;

import java.util.Random;



public class EpsilonGreedyExploration implements ExplorationPolicy {
    private double epsilon;
    
    private Random r = new Random();

    /**
     * Initializes a new instance of the EpsilonGreedyExploration class.
     * @param epsilon Epsilon value (exploration rate).
     */
    public EpsilonGreedyExploration(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Get Episilon value.
     * @return Return Epsilon value.
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * The value determines the amount of exploration driven by the policy.
     * If the value is high, then the policy drives more to exploration - choosing random
     * action, which excludes the best one. If the value is low, then the policy is more
     * greedy - choosing the beat so far action.
     * 
     * @param epsilon Epsilon value (exploration rate), [0, 1].
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = Math.max( 0.0, Math.min( 1.0, epsilon ) );
    }
    
    /**
     * The method chooses an action depending on the provided estimates. The
     * estimates can be any sort of estimate, which values usefulness of the action
     * (expected summary reward, discounted reward, etc).
     * 
     * @param actionEstimates Action Estimates.
     * @return Return Selected actions.
     */

    @Override
    public int ChooseAction(double[] actionEstimates){
        int actionsCount = actionEstimates.length;

        // find the best action (greedy)
        double maxReward = actionEstimates[0];
        int greedyAction = 0;

        for ( int i = 1; i < actionsCount; i++ )
        {
            if ( actionEstimates[i] > maxReward )
            {
                maxReward = actionEstimates[i];
                greedyAction = i;
            }
        }

        // try to do exploration
        if ( r.nextDouble( ) < epsilon )
        {
            int randomAction = r.nextInt( actionsCount - 1 );

            if ( randomAction >= greedyAction )
                randomAction++;

            return randomAction;
        }

        return greedyAction;
    }
    
}