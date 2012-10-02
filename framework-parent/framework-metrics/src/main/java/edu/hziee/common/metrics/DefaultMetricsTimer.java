/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.hziee.common.lang.NameValue;
import edu.hziee.common.metrics.model.Measurement;
import edu.hziee.common.metrics.util.CommonUtil;
import edu.hziee.common.metrics.util.MetricsUtil;
import edu.hziee.common.metrics.util.SystemUtil;

/**
 * @author Administrator
 * 
 */
public class DefaultMetricsTimer implements MetricsTimer {

	private String										uniqueId				= null;

	private final MetricsEngine				metricsEngine;
	private final String							componentName;
	private final String							functionName;
	private final long								startNano;
	private final Date								startTime;
	private Integer										workUnits;
	private Integer										createOrder;
	private Boolean										failStatus;
	private String										workUser;
	private CorrelationInfo						correlationInfo;
	private List<NameValue>						metricsList;

	private MetricsTimer							parentTimer;
	private Measurement								measurement;
	private final List<MetricsTimer>	childTimerList	= new ArrayList<MetricsTimer>();

	public DefaultMetricsTimer(final MetricsEngine metricsEngine, final String componentName, final String functionName,
			final String correlationId) {
		this.metricsEngine = metricsEngine;
		this.componentName = MetricsUtil.truncate(componentName, 64);
		this.functionName = MetricsUtil.truncate(functionName, 64, "function");
		this.startNano = System.nanoTime();
		this.startTime = new Date();
		this.uniqueId = SystemUtil.createGuid();

		if (metricsEngine.isCollectMetrics()) {
			this.parentTimer = metricsEngine.pushTimer(this);
		}
	}

	@Override
	public long stop(Throwable t, List<NameValue> metricsList) {
		long duration = 0;

		try {
			if (startTime != null) {
				duration = CommonUtil.nanoToMillis(System.nanoTime() - startNano);
				if (metricsList != null) {
					this.getMetricsList().addAll(metricsList);
				}

				measurement = new Measurement(duration, getMetricsList(), t);
				measurement.setDuration(duration);
				if (failStatus != null && failStatus) {
					measurement.setFailStatus(failStatus);
				}
				if (workUnits == null) {
					workUnits = 1;
				}
				measurement.setWorkUnits(workUnits);
				measurement.setTimestamp(startTime);

				getMetricsEngine().popTimer(this);

				if (parentTimer != null) {
					parentTimer.getChildTimerList().add(this);
				} else {
					// store the metrics timer to the memory queue
					getMetricsEngine().enqueueMetricsTimer(this);
				}

			}
		} catch (RuntimeException e) {
			if (metricsEngine.isThrowException()) {
				throw e;
			}
		}
		return duration;
	}

	@Override
	public long stop(List<NameValue> metricsList) {
		return stop(null, metricsList);
	}

	@Override
	public long stop(Throwable t) {
		return stop(t, null);
	}

	@Override
	public long stop() {
		return stop(null, null);
	}

	@Override
	public CorrelationInfo getCorrelationInfo() {
		CorrelationInfo correlationInfo = null;
		if (parentTimer != null) {
			correlationInfo = parentTimer.getCorrelationInfo();
		} else {
			if (this.correlationInfo == null) {
				this.correlationInfo = new CorrelationInfo(SystemUtil.createGuid(), uniqueId);
			}
			correlationInfo = this.correlationInfo;
		}
		return correlationInfo;
	}

	@Override
	public void setCorrelationInfo(CorrelationInfo correlationInfo) {
		if (parentTimer != null) {
			parentTimer.setCorrelationInfo(correlationInfo);
		} else {
			this.correlationInfo = correlationInfo;
		}
	}

	@Override
	public void setWorkUnits(int workUnits) {
		this.workUnits = workUnits;
	}

	@Override
	public void setFailStatus(boolean failStatus) {
		this.failStatus = failStatus;
	}

	@Override
	public void setWorkUser(String workUser) {
		this.workUser = workUser;
	}

	@Override
	public void addComponent(Object component) {
		addMetrics(component);
	}

	@Override
	public void addProcessor(Object processor) {
		addMetrics(processor);
	}

	@Override
	public void addRequest(Object request) {
		addMetrics(request);
	}

	@Override
	public void addMetrics(Object requester) {
		if (requester != null) {
			addMetrics("SimpleName", requester.getClass().getSimpleName());
		}
	}

	@Override
	public void addMetrics(String name, String value) {
		getMetricsList().add(new NameValue(name, value));
	}

	public List<NameValue> getMetricsList() {
		if (metricsList == null) {
			metricsList = new ArrayList<NameValue>();
		}
		return metricsList;
	}

	@Override
	public List<MetricsTimer> getAllMetricsTimers() {
		List<MetricsTimer> timerList = new ArrayList<MetricsTimer>();
		timerList.add(this);
		for (MetricsTimer childTimer : childTimerList) {
			timerList.addAll(childTimer.getAllMetricsTimers());
		}
		return timerList;
	}

	@Override
	public Measurement getMeasurement() {
		return measurement;
	}

	@Override
	public void correlate(String parentId, String metricsUser) {
		measurement.setId(uniqueId);
		if (parentId != null) {
			measurement.setParentId(parentId);
		}
		if (correlationInfo != null) {
			measurement.setCorrelationId(correlationInfo.getId());
			measurement.setCorrelationRequester(correlationInfo.getRequester());
		}
		measurement.setComponentName(componentName);
		measurement.setFunctionName(functionName);
		if (workUser == null) {
			workUser = metricsUser;
		}
		measurement.setUser(MetricsUtil.truncate(workUser, 16));

		measurement.setCreateOrder(createOrder);

		// to ensure size
		measurement.setThreadName(MetricsUtil.truncate(measurement.getThreadName(), 64));
		for (NameValue nameValue : measurement.getMetricsList()) {
			nameValue.setName(MetricsUtil.truncate(nameValue.getName(), 64, "name"));
			nameValue.setValue(MetricsUtil.truncate(nameValue.getValue(), 1024));
		}

		// correlate for all child timers
		int createOrder = 0;
		for (MetricsTimer childTimer : childTimerList) {
			childTimer.setCreateOrder(createOrder++);
			childTimer.correlate(measurement.getId(), workUser);
		}
	}

	public final Long getDuration() {
		if (measurement != null && measurement.getDuration() != null) {
			return measurement.getDuration();
		} else {
			return 0L;
		}
	}

	public List<MetricsTimer> getChildTimerList() {
		return childTimerList;
	}

	public final String getId() {
		if (measurement != null) {
			return measurement.getId();
		} else {
			return null;
		}
	}

	public Boolean getFailStatus() {
		Boolean failStatus = this.failStatus;
		if (measurement != null) {
			failStatus = measurement.getFailStatus();
		}
		return failStatus == null ? false : failStatus;
	}

	@Override
	public void setCreateOrder(int createOrder) {
		this.createOrder = createOrder;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Integer getWorkUnits() {
		return workUnits;
	}

	public String getWorkUser() {
		return workUser;
	}

	public MetricsEngine getMetricsEngine() {
		return metricsEngine;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append("[component=").append(componentName);
		builder.append(",function=").append(functionName);
		builder.append("]");

		return builder.toString();
	}

}
