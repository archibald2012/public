/**
 * 
 */
package edu.hziee.common.fsm.state;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.fsm.demo.Radio;
import edu.hziee.common.fsm.state.annotation.OnEnter;
import edu.hziee.common.fsm.state.annotation.OnExecute;
import edu.hziee.common.fsm.state.annotation.OnExit;
import edu.hziee.common.fsm.state.annotation.StateTemplate;
import edu.hziee.common.lang.ClassUtil;

/**
 * @author ubuntu-admin
 * 
 */
public class StateAdapterTestCase {

  StateAdapter<Radio> stateAdapter;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    stateAdapter = new StateAdapter<Radio>();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    stateAdapter = null;
  }

  @Test
  public void testEnterMethod() {

    Method[] methods = ClassUtil.getAllMethodOf(InnerState.class);

    Method enterMethod = null;
    for (Method m : methods) {
      if (m.getAnnotation(OnEnter.class) != null) {
        enterMethod = m;
        break;
      }
    }
    stateAdapter.setMethodOfEnter(enterMethod);
    Assert.assertEquals(enterMethod, stateAdapter.getMethodOfEnter());

    stateAdapter.setTarget(new InnerState());
    stateAdapter.enter(new Radio());
  }

  @Test(expected = RuntimeException.class)
  public void testSetEnterMethod_moreThanOne() {
    Method[] methods = ClassUtil.getAllMethodOf(InnerState.class);

    stateAdapter.setMethodOfEnter(methods[0]);
    stateAdapter.setMethodOfEnter(methods[0]);
  }

  @Test
  public void testExecuteMethod() {

    String[] types = stateAdapter.getAcceptedEvents();
    Assert.assertEquals(0, types.length);

    Method[] methods = ClassUtil.getAllMethodOf(InnerState.class);

    Method actionMethod = null;
    for (Method m : methods) {
      if (m.getAnnotation(OnExecute.class) != null) {
        actionMethod = m;
        break;
      }
    }
    stateAdapter.addMethodOfExecute("action", actionMethod);

    Assert.assertEquals(actionMethod, stateAdapter.getMethodOfExecute("action"));

    types = stateAdapter.getAcceptedEvents();
    Assert.assertEquals(1, types.length);
    Assert.assertEquals("action", types[0]);

    stateAdapter.setTarget(new InnerState());
    stateAdapter.execute(new Radio(), "action", "symbol");
  }

  @Test
  public void testExitMethod() {

    Method[] methods = ClassUtil.getAllMethodOf(InnerState.class);

    Method exitMethod = null;
    for (Method m : methods) {
      if (m.getAnnotation(OnExit.class) != null) {
        exitMethod = m;
        break;
      }
    }
    stateAdapter.setMethodOfExit(exitMethod);
    Assert.assertEquals(exitMethod, stateAdapter.getMethodOfExit());

    stateAdapter.setTarget(new InnerState());
    stateAdapter.exit(new Radio());
  }

  @Test(expected = RuntimeException.class)
  public void testSetExitMethod_moreThanOne() {
    Method[] methods = ClassUtil.getAllMethodOf(InnerState.class);

    stateAdapter.setMethodOfExit(methods[0]);
    stateAdapter.setMethodOfExit(methods[0]);
  }

}

@StateTemplate
class InnerState {

  @OnEnter
  public Class<?> enter(Radio radio) {
    return InnerState.class;
  }

  @OnExecute
  public Class<?> action(Radio radio, String symbol) {
    Assert.assertEquals("symbol", symbol);
    return null;
  }

  @OnExit
  public void exit(Radio radio) {
    System.out.println("exit inner state.");
  }

}
