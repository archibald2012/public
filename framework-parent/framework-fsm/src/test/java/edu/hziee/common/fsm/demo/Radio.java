/**
 * 
 */
package edu.hziee.common.fsm.demo;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.fsm.demo.signal.Next;
import edu.hziee.common.fsm.demo.signal.Pause;
import edu.hziee.common.fsm.demo.signal.Play;
import edu.hziee.common.fsm.demo.signal.Stop;
import edu.hziee.common.fsm.state.annotation.OnEnter;
import edu.hziee.common.fsm.state.annotation.OnExecute;
import edu.hziee.common.fsm.state.annotation.OnExit;
import edu.hziee.common.fsm.state.annotation.StateTemplate;

/**
 * @author ubuntu-admin
 * 
 */
public class Radio {

  String        status = "stop";
  AtomicInteger index  = new AtomicInteger(0);

  public String getStatus() {
    return this.status;
  }

  public AtomicInteger getIndex() {
    return this.index;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}

@StateTemplate(start = true)
class StopState {

  @OnEnter
  public void enter(Radio ctx) throws Exception {
    ctx.status = "stop";
    System.out.println(ctx.status);
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Play req) throws Exception {
    System.out.println("stop-->play");
    return PlayState.class;
  }

  @OnExit
  public void exit(Radio ctx) throws Exception {
    ctx.status = "";
    System.out.println(ctx.status);
  }
}

@StateTemplate
class PlayState {

  @OnEnter
  public void enter(Radio ctx) throws Exception {
    ctx.status = "play";
    System.out.println(ctx.status);
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Next req) throws Exception {
    System.out.println("play-->next");
    ctx.index.incrementAndGet();
    return PlayState.class;
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Pause req) throws Exception {
    System.out.println("play-->pause");
    return PauseState.class;
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Stop req) throws Exception {
    System.out.println("play-->stop");
    ctx.index = new AtomicInteger(0);
    return StopState.class;
  }

  @OnExit
  public void exit(Radio ctx) throws Exception {
    ctx.status = "";
    System.out.println(ctx.status);
  }
}

@StateTemplate
class PauseState {

  @OnEnter
  public void enter(Radio ctx) throws Exception {
    ctx.status = "pause";
    System.out.println(ctx.status);
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Play req) throws Exception {
    System.out.println("pause-->play");
    return PlayState.class;
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Next req) throws Exception {
    System.out.println("pause-->next");
    ctx.index.incrementAndGet();
    return PlayState.class;
  }

  @OnExecute
  public Class<?> execute(Radio ctx, Stop req) throws Exception {
    System.out.println("pause-->stop");
    ctx.index = new AtomicInteger(0);
    return StopState.class;
  }

  @OnExit
  public void exit(Radio ctx) throws Exception {
    ctx.status = "";
    System.out.println(ctx.status);
  }
}
