/**
 * 
 */
package edu.hziee.common.fsm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A deterministic finite state machine or acceptor deterministic finite state
 * machine is a quintuple (Σ,S,s0,δ,F), where:
 * <ul>
 * <li>Σ is the input alphabet (a finite, non-empty set of symbols).</li>
 * <li>S is a finite, non-empty set of states.</li>
 * <li>s0 is an initial state, an element of S.</li>
 * <li>δ is the state-transition function:</li>
 * <li>F is the set of final states, a (possibly empty) subset of S.</li>
 * </ul>
 * 
 * @author ubuntu-admin
 * 
 */
public class DefaultFSM<T> implements FSM<T> {

  private static final Logger   logger = LoggerFactory.getLogger(DefaultFSM.class);

  // a pointer to the agent that owns this instance
  private T                     t;

  private State<T>              previousState;
  private State<T>              currentState;

  private Map<String, State<T>> states = new HashMap<String, State<T>>();

  public DefaultFSM(T t, State<T>[] states) {
    this.t = t;

    this.states.clear();
    for (State<T> state : states) {
      this.states.put(FSMUtil.getStateNameOfInstance(state), state);
    }

    currentState = states[0];
    previousState = currentState;
    String nextStateName = currentState.enter(t);
    changeState(nextStateName);
  }

  @Override
  public State<T> getPreviousState() {
    return previousState;
  }

  @Override
  public State<T> getCurrentState() {
    return this.currentState;
  }

  @Override
  public boolean isInState(State<T> state) {
    return this.currentState == state;
  }

  /**
   * call this to update fsm.
   * 
   * @param event
   * @return currentStateName
   */
  @Override
  public String acceptEvent(String event, Object... args) {

    // call current state execute method
    String nextStateName = currentState.execute(t, event, args);

    changeState(nextStateName);

    return FSMUtil.getStateNameOfInstance(currentState);
  }

  /**
   * change to a new state
   * 
   * @param nextStateName
   */
  public void changeState(String newStateName) {
    if (newStateName == null) {
      return;
    }

    String currentStateName = FSMUtil.getStateNameOfInstance(currentState);
    // state no change
    if (currentStateName.equals(newStateName)) {
      return;
    }

    State<T> newState = getStateOf(newStateName);
    if (null == newState) {
      logger.error("Internal Error: Can not found valid state for [" + newStateName + "] , just ignore");
      return;
    }

    if (currentState != newState) {
      changeState(newState);
    }
  }

  /**
   * change to a new state
   * 
   * @param newState
   */
  public void changeState(State<T> newState) {
    if (newState != null && currentState != newState) {

      // keep a record of the previous state
      previousState = currentState;

      // call the exit method of the existing state
      currentState.exit(t);

      // change state to the new state
      currentState = newState;

      // call the entry method of the new state
      String nextStateName = currentState.enter(t);

      changeState(nextStateName);
    }

  }

  /**
   * revert to the previous state
   */
  public void revertToPreviousState() {
    changeState(previousState);
  }

  private State<T> getStateOf(String stateName) {
    return this.states.get(stateName);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
