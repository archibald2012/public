/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.Collection;

import edu.hziee.common.metrics.model.Publishable;

/**
 * @author Administrator
 * 
 */
public interface MetricsPublishWorker {
	void enqueuePublishable(Publishable publishable);

	void enqueuePublishable(Collection<? extends Publishable> publishables);
}
