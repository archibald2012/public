/**
 * 
 */
package edu.hziee.common.jmx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JmxIndicator {

	public abstract String objectName();

	public abstract String operationName();
}
