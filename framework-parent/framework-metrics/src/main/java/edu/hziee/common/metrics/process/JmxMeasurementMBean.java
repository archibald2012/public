/**
 * 
 */
package edu.hziee.common.metrics.process;

import java.util.List;

/**
 * @author Administrator
 * 
 */
public interface JmxMeasurementMBean {

	List<String> queryMeasurementList();

	void resetAllCounts();

	boolean resetCount(String key);
}
