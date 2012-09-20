/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.List;

import edu.hziee.common.lang.NameValue;

/**
 * @author Administrator
 * 
 */
public interface MetricsTimer {

	long stop(Throwable t, List<NameValue> metricsList);

	long stop(List<NameValue> metricsList);

	long stop(Throwable t);

	long stop();

	CorrelationInfo getCorrelationInfo();

	void setCorrelationInfo(CorrelationInfo correlationInfo);

	void setWorkUnits(int workUnits);

	void setFailStatus(boolean failStatus);

	void setWorkUser(String workUser);

	void addComponent(Object component);

	void addProcessor(Object processor);

	void addRequest(Object request);

	void addMetrics(Object component);

	void addMetrics(String name, String value);
}
