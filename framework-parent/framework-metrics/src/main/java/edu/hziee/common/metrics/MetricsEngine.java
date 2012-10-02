/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.Stack;

/**
 * @author Administrator
 * 
 */
public interface MetricsEngine {

	boolean isCollectMetrics();

	void setCollectMetrics(boolean collectMetrics);

	boolean isThrowException();

	void setThrowException(boolean throwException);

	MetricsCollector createMetricsCollector(String componentName);

	CorrelationInfo getCorrelationInfo();

	boolean isFilter(String componentName, String functionName);

	boolean updateFilter(String componentName, String functionName, boolean isFilter);

	Stack<MetricsTimer> getTimerStack();

	MetricsTimer pushTimer(MetricsTimer timer);

	MetricsTimer popTimer(MetricsTimer timer);

	void enqueueMetricsTimer(MetricsTimer metricsTimer);
}
