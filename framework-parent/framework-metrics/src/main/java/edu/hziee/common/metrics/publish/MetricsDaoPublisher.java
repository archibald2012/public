/**
 * 
 */
package edu.hziee.common.metrics.publish;

import edu.hziee.common.metrics.model.Record;
import edu.hziee.common.metrics.publish.dao.DefaultMetricsDao;

/**
 * @author Administrator
 * 
 */
public class MetricsDaoPublisher implements MetricsPublisher {

	private DefaultMetricsDao	metricsDao;

	@Override
	public boolean publish(Record record) {
		int count = metricsDao.saveRecord(record);
		return count > 0;
	}

}
