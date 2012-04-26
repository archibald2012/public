/**
 * 
 */
package edu.hziee.common.fsm;

/**
 * A finite-state machine (FSM) is conceived as an abstract machine that can be
 * in one of a finite number of states.The machine is in only one state at a
 * time; the state it is in at any given time is called the current state. It
 * can change from one state to another when initiated by a triggering event or
 * condition, this is called a transition. A particular FSM is defined by a list
 * of the possible states it can transition to from each state, and the
 * triggering condition for each transition.
 * 
 * @author ubuntu-admin
 * 
 */
public interface FSM<T> {

  /**
   * Finite-state machine operate on input to either make transitions from one
   * state to another or to cause an output or action to take place.
   * 
   * @param event
   * @return currentStateName
   */
  String acceptEvent(String event, Object... args);

  /**
   * a record of the last state the agent was in
   * 
   * @return
   */
  State<T> getPreviousState();

  /**
   * The machine is in only one state at a time.
   * 
   * @return the current state.
   */
  State<T> getCurrentState();

  /**
   * 
   * @param state
   * @return returns true if the current state’s type is equal to the type of
   *         the class passed as a parameter.
   */
  boolean isInState(State<T> state);

  /**
   * change to a new state
   * 
   * @param newState
   */
  void changeState(State<T> newState);

}
