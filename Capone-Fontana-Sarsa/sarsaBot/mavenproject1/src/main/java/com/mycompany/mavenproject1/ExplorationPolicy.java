package com.mycompany.mavenproject1;

/**
 * The interface describes exploration policies, which are used in Reinforcement
 * Learning to explore state space.
 * 
 * @author a7capone/p7fontan
 */

public interface ExplorationPolicy {
    /**
     * The method chooses an action depending on the provided estimates. the
     * estimates can be any sort of estimate, which values usefulness of the action
     * (expected summary reward, discounted reward, etc).
     * 
     * @param actionEstimates Action estimates.
     * @return Returns selected action.
     */
    public int ChooseAction(double[] actionEstimates);
}
