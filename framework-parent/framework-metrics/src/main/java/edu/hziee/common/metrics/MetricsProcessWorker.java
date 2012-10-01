/**
 * 
 */
package edu.hziee.common.metrics;

/**
 * @author Administrator
 * 
 */
public interface MetricsProcessWorker {
	void enqueueMetricsTimer(MetricsTimer timer);

	int getQueueSize();
}
