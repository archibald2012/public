/**
 * 
 */
package edu.hziee.common.fsm.state;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.fsm.State;
import edu.hziee.common.fsm.state.annotation.OnEnter;
import edu.hziee.common.fsm.state.annotation.OnExecute;
import edu.hziee.common.fsm.state.annotation.OnExit;
import edu.hziee.common.fsm.state.annotation.StateTemplate;
import edu.hziee.common.lang.ClassUtil;

/**
 * @author ubuntu-admin
 * 
 */
public class StateFactory {

  private static final Logger logger = LoggerFactory.getLogger(StateFactory.class);

  public static State<?>[] createStates(Object state) {
    return createStates(Arrays.asList(new Object[] { state }));
  }

  public static State<?>[] createStates(Collection<Object> states) {
    List<Object> sources = new ArrayList<Object>();

    for (Object state : states) {
      if (isStateTemplate(state.getClass())) {
        sources.add(state);
      }
    }

    boolean startStateDefined = false;

    List<State<?>> ret = new ArrayList<State<?>>();
    for (Object template : sources) {
      if (logger.isDebugEnabled()) {
        logger.debug(template.getClass() + " is in state template. try generate state");
      }

      State<?> state = null;
      try {
        state = processTemplate(template);
      } catch (Exception e) {
        logger.error("processTemplate:", e);
      }

      if (null != state) {
        if (isStartState(template.getClass())) {
          if (startStateDefined) {
            throw new RuntimeException("more than one start state.");
          }
          logger.info("{} is start state.", ClassUtil.getSimpleName(template.getClass()));
          startStateDefined = true;
          ret.add(0, state);
        } else {
          ret.add(state);
        }
      }
    }

    if (!startStateDefined) {
      logger.warn("Non start State defined, just use first State as start state.");
    }

    return ret.toArray(new State[0]);
  }

  @SuppressWarnings("rawtypes")
  private static State<?> processTemplate(Object state) throws Exception {
    StateAdapter<?> stateAdapter = new StateAdapter();

    Class<?> c = state.getClass();
    stateAdapter.setName(c);
    stateAdapter.setTarget(state);

    Method[] methods = ClassUtil.getAllMethodOf(c);

    for (Method m : methods) {
      if (logger.isDebugEnabled()) {
        logger.debug("process method " + m);
      }
      processMethod(stateAdapter, m);
    }

    return stateAdapter;
  }

  private static void processMethod(StateAdapter<?> state, Method m) {
    OnEnter spe = m.getAnnotation(OnEnter.class);
    OnExecute spa = m.getAnnotation(OnExecute.class);
    OnExit spl = m.getAnnotation(OnExit.class);

    if (null != spe) {
      state.setMethodOfEnter(m);
    } else if (null != spl) {
      state.setMethodOfExit(m);
    } else if (null != spa) {
      state.addMethodOfExecute(m.getName(), m);
    }
  }

  private static boolean isStartState(Class<?> c) {
    return (null != c.getAnnotation(StateTemplate.class)) && c.getAnnotation(StateTemplate.class).start();
  }

  private static boolean isStateTemplate(Class<?> c) {
    return null != c.getAnnotation(StateTemplate.class);
  }

}
