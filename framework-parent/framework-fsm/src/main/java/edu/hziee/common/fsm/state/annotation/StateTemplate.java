/**
 * 
 */
package edu.hziee.common.fsm.state.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Template-based state.
 * 
 * @author ubuntu-admin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StateTemplate {
  public abstract boolean start() default false;
}
