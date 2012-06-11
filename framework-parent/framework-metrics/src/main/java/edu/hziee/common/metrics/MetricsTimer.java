/**
 * 
 */
package edu.hziee.common.metrics;

/**
 * @author Administrator
 * 
 */
public interface MetricsTimer {

	void addMetrics(Object argument);

	long stop(Throwable t);

	long stop();
}
