/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.List;

import edu.hziee.common.lang.NameValue;
import edu.hziee.common.metrics.model.Measurement;

/**
 * @author Administrator
 * 
 */
public class DefaultMetricsTimer implements MetricsTimer {

	public DefaultMetricsTimer(final MetricsEngine metricsEngine, final String componentName, final String functionName,
			final String correlationId) {

	}

	@Override
	public long stop(Throwable t, List<NameValue> metricsList) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#stop(java.util.List)
	 */
	@Override
	public long stop(List<NameValue> metricsList) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#stop(java.lang.Throwable)
	 */
	@Override
	public long stop(Throwable t) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#stop()
	 */
	@Override
	public long stop() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#getCorrelationInfo()
	 */
	@Override
	public CorrelationInfo getCorrelationInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#setCorrelationInfo(edu.hziee.common.metrics.CorrelationInfo)
	 */
	@Override
	public void setCorrelationInfo(CorrelationInfo correlationInfo) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#setWorkUnits(int)
	 */
	@Override
	public void setWorkUnits(int workUnits) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#setFailStatus(boolean)
	 */
	@Override
	public void setFailStatus(boolean failStatus) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#setWorkUser(java.lang.String)
	 */
	@Override
	public void setWorkUser(String workUser) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#addComponent(java.lang.Object)
	 */
	@Override
	public void addComponent(Object component) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#addProcessor(java.lang.Object)
	 */
	@Override
	public void addProcessor(Object processor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#addRequest(java.lang.Object)
	 */
	@Override
	public void addRequest(Object request) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#addMetrics(java.lang.Object)
	 */
	@Override
	public void addMetrics(Object component) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#addMetrics(java.lang.String, java.lang.String)
	 */
	@Override
	public void addMetrics(String name, String value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#getAllMetricsTimers()
	 */
	@Override
	public List<MetricsTimer> getAllMetricsTimers() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#getMeasurement()
	 */
	@Override
	public Measurement getMeasurement() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsTimer#correlate(java.lang.String, java.lang.String)
	 */
	@Override
	public void correlate(String parentId, String metricsUser) {
		// TODO Auto-generated method stub

	}

}
