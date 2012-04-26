/**
 * 
 */
package edu.hziee.common.fsm.state;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.fsm.State;
import edu.hziee.common.fsm.state.annotation.OnEnter;
import edu.hziee.common.fsm.state.annotation.OnExecute;
import edu.hziee.common.fsm.state.annotation.StateTemplate;

/**
 * @author ubuntu-admin
 * 
 */
public class StateFactoryTestCase {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testCreateStates() {
    State1 stateTemplate = new State1();
    State2 stateTemplate2 = new State2();
    Collection<Object> templates = new ArrayList<Object>();
    templates.add(stateTemplate);
    templates.add(stateTemplate2);

    State[] states = StateFactory.createStates(templates);

    Assert.assertEquals(2, states.length);
    StateAdapter state = (StateAdapter) states[1];
    Assert.assertEquals("State1", state.getName());
    Assert.assertEquals(stateTemplate, state.getTarget());
    Assert.assertNotNull(state.getMethodOfExecute("execute"));
    Assert.assertNull(state.getMethodOfExecute("execute3"));
    Assert.assertNotNull(state.getMethodOfExecute("execute2"));

  }
}

@StateTemplate
class State1 {

  @OnEnter
  public boolean enter() {
    return true;
  }

  @OnExecute
  public void execute(String symbol) {
  }

  @OnExecute
  public void execute2(Long symbol) {
  }

}

@StateTemplate(start = true)
class State2 {

  @OnEnter
  public boolean enter() {
    return true;
  }

  @OnExecute
  public void execute(String symbol) {
  }

  @OnExecute
  public void execute2(Integer symbol) {
  }

}