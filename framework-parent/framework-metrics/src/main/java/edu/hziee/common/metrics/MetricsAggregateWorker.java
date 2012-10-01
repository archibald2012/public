/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.Collection;
import java.util.Map;

import edu.hziee.common.metrics.aggregate.AggregationEntry;

/**
 * @author Administrator
 * 
 */
public interface MetricsAggregateWorker {

	boolean checkMetricsTimers(Collection<MetricsTimer> meticsTimers);

	Map<String, AggregationEntry> getAggregationMap();
}
