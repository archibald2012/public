/**
 * 
 */
package edu.hziee.common.metrics;

/**
 * @author Administrator
 * 
 */
public interface MetricsEngine {

	boolean isCollectMetrics();

	void setCollectMetrics(boolean collectMetrics);

	boolean isThrowException();

	void setThrowException(boolean throwException);
}
