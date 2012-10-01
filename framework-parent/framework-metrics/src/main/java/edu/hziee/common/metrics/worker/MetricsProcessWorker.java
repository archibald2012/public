/**
 * 
 */
package edu.hziee.common.metrics.worker;

import edu.hziee.common.metrics.MetricsTimer;

/**
 * @author Administrator
 * 
 */
public interface MetricsProcessWorker {
	void enqueueMetricsTimer(MetricsTimer metricsTimer);

	int getQueueSize();
}
