/**
 * 
 */
package edu.hziee.common.fsm;

/**
 * A state describes a behavioral node of the system in which it is waiting for
 * a trigger to execute a transition. The same stimulus triggers different
 * actions depending on the current state.
 * 
 * I prefer to use singletons for the states,Using singletons makes the design
 * more efficient because they remove the need to allocate and deallocate memory
 * every time a state change is made. Because they are shared between clients,
 * singleton states are unable to make use of their own local data, but if you
 * find that the states you have designed are repeatedly accessing lots of
 * external data, it’s probably worth considering disposing of the singleton
 * design and writing a few lines of code to manage the allocation and
 * deallocation of state memory.
 * 
 * @author archie.wang
 * 
 */
public interface State<T> {

  /**
   * Entry action: which is performed when entering the state. if the current
   * state is an accepting state, the input is accepted; otherwise it is
   * rejected.
   * 
   * @param t
   * @return The next state name, each state explicitly convert to another state
   */
  String enter(T t);

  /**
   * Exit action: which is performed when exiting the state.
   * 
   * @param t
   */
  void exit(T t);

  /**
   * A transition is a set of actions to be executed when an event is received.
   * 
   * @param t
   * @param event
   * @param args
   * @return The next state name, each state explicitly convert to another state
   */
  String execute(T t, String event, Object... args);
}
