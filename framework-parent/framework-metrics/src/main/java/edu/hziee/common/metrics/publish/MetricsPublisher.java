/**
 * 
 */
package edu.hziee.common.metrics.publish;

import edu.hziee.common.metrics.model.Record;

/**
 * @author Administrator
 * 
 */
public interface MetricsPublisher {

	boolean publish(Record record);
}
