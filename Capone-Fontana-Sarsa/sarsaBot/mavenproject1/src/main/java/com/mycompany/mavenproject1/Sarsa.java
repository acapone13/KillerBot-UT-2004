package com.mycompany.mavenproject1;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/** 
 *   Implementation of Sarsa algorithm
 */

public class Sarsa {

    // Possible states 
    private int states; //5

    // Previous state
    private int state;

    // Possible actions
    private int actions; //8

    // Previous action
    private int action;

    // Q-values
    private double[][] qvalues;

    // Frequency table
    private double[][] N;

    // Exploration policy (Epsilon)
    private ExplorationPolicy explorationPolicy;

    // Discount factor (Gamma)
    private double discountFactor; // 0.95

    // Learning rate (alpha)
    private double learningRate; // 0.25

    // Reward
    private double reward;
    
    public int count = 0;

    /** 
    *  Amount of possible states
    *  @return States
    */
    public int getStates() {
        return states;
    }

    /** 
    *  Amount of possible actions
    *  @return Actions
    */
    public int getActions() {
        return actions;
    }

    /** 
    *  Exploration policy
    *  @return Exploration policy
    */
    public ExplorationPolicy getExplorationPolicy() {
        return explorationPolicy;
    }

    /**
    *  Exploration policy.
    *  Policy, which is used to select actions.
    *  @param explorationPolicy Exploration policy
    */
    public void setExplorationPolicy(ExplorationPolicy explorationPolicy) {
        this.explorationPolicy = explorationPolicy;
    }

    /**
    *  Get learning rate
    *  @return Learning rate
    */
    public double getLearningRate() {
        return learningRate;
    }

    /**
    *  Learning rate, [0, 1].
    *  The value determines the amount of updates Q-function receives
    *  during learning. The greater the value, the more updates the function receives.
    *  The lower the value, the less updates it receives.
    *  @param learningRate Learning rate
    */
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(0.0, Math.min(1.0, learningRate));
    }

    /**
    *  Get Discount Factor
    *  @return Discount Factor
    */
    public double getDiscountFactor() {
        return discountFactor;
    }

    /**
    *  Discount factor, [0, 1].
    *  Discount factor for the expected summary reward. The value serves as
    *  multiplier for the expected reward. So if the value is set to 1,
    *  then the expected summary reward is not discounted. If the value is getting
    *  smaller, then smaller amount of the expected reward is used for actions'
    *  estimates update.
    *  @param discountFactor Discount Factor
    */
    public void setDiscountFactor(double discountFactor) {
        this.discountFactor = Math.max(0.0, Math.min(1.0, discountFactor));
    }

    /**
    *  Initializes a new instance of the Sarsa class.
    *  @param states Amount of possible states.
    *  @param actions Amount of possible actions.
    *  @param explorationPolicy Exploration policy.
    *  @param randomize Randomize action estimates or not.
    * 
    *  The <b>randomize</b> parameter specifies if initial action estimates should be randomized
    *  with small values or not. Randomization of action values may be useful, when greedy exploration
    *  policies are used. In this case randomization ensures that actions of the same type are not chosen always.
    */
    public Sarsa(int states, int actions, ExplorationPolicy explorationPolicy, boolean randomize) {
        this.states = states;
        this.actions = actions;
        this.explorationPolicy = explorationPolicy;

        // Create Q-Array
        /*qvalues = new double[states][];
        for (int i = 0; i < states; i++){
            qvalues[i] = new double[actions];
        }*/

        this.qvalues = new double[states][actions];
        this.N = new double[states][actions];

        // Randomize
        if (randomize){
            Random r = new Random( );
            for (int i = 0; i < states; i++){
                for (int j = 0; j < actions; j++){
                    this.qvalues[i][j] = r.nextDouble()/10;
                }
            }
        }
    }

    /**
    * Get next action from the specified state.
    * @param state Current state to get an action for.
    * @return Returns the action for the state.
    */
    public int getAction(int state) {
        return explorationPolicy.ChooseAction(qvalues[state]);
    }

    public void setAction(int action){
        this.action = action;
    }

    public void setState(int state){
        this.state = state;
    }

    /**
    * Update Q-function's value for the previous state-action pair.
    * @param previousState Current state.
    * @param previousAction Action, which lead from previous to the next state.
    * @param reward Reward value, received by taking specified action from previous state.
    * @param nextState Next state.
    * @param nextAction Next action.
    */
    public int learn(double nextReward, int nextState){
        count++;
        int nextAction; 
        if (this.state == 3){
            //qvalues[this.state][this.action] = learningRate*nextReward;
            return 4;
        } else {
        //if (this.state != null ){
            nextAction = getAction(nextState);
            //this.N[this.state][this.action] += 1;
            this.qvalues[this.state][this.action] += learningRate*(this.reward + discountFactor*(this.qvalues[nextState][nextAction] - this.qvalues[this.state][this.action]));
        }
        this.state = nextState;
        this.action = nextAction;
        this.reward = nextReward;
        if (count == 100){
            saveQ();
            count = 0;
        }
        return this.action;
    } 
    
    public void saveQ(){
        try {
        BufferedWriter bw = new BufferedWriter(new FileWriter("Qfile"));

        for (int i = 0; i < qvalues.length; i++) {
            for (int j = 0; j < qvalues[i].length - 1; j++) {
                    bw.write(qvalues[i][j] + " ");
            }
            bw.newLine();
        }
        bw.flush();
    } catch (IOException e) {}
    }
    
}

