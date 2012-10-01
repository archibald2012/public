/**
 * 
 */
package edu.hziee.common.metrics.worker;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.MetricsAggregateWorker;
import edu.hziee.common.metrics.MetricsProcessWorker;
import edu.hziee.common.metrics.MetricsPublishWorker;
import edu.hziee.common.metrics.MetricsTimer;
import edu.hziee.common.metrics.model.Measurement;
import edu.hziee.common.metrics.util.SystemUtil;

/**
 * @author Administrator
 * 
 */
public class DefaultProcessWorker extends Thread implements MetricsProcessWorker {
	private static final Logger								logger						= LoggerFactory.getLogger(DefaultProcessWorker.class);

	private long															checkInterval			= 2000;
	private MetricsPublishWorker							metricsPublishWorker;
	private MetricsAggregateWorker						metricsAggregateWorker;
	private LinkedBlockingQueue<MetricsTimer>	metricsTimerQueue	= new LinkedBlockingQueue<MetricsTimer>(1000);

	private String														defaultUser				= SystemUtil.getUserName();
	private String														mbeanObjectName		= "Application:type=Metrics,name=Measurement";

	private JmxMeasurement										measurementMBean;

	public void run() {

		registerMBeans();

		while (true) {
			try {
				processMetricsTimer();
			} catch (Exception e) {
				logger.warn("Failed to process measurement data", e);
			}
		}
	}

	@Override
	public void enqueueMetricsTimer(MetricsTimer timer) {
		if (!metricsTimerQueue.offer(timer)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Current queue limit " + metricsTimerQueue.size() + " is reached");
			}
		}
	}

	@Override
	public int getQueueSize() {
		return metricsTimerQueue.size();
	}

	protected void processMetricsTimer() {
		MetricsTimer metricsTimer = getMetricsTimer();
		if (metricsTimer != null) {
			// do the correlate, there is no parent ID
			metricsTimer.correlate(null, defaultUser);
			List<MetricsTimer> metricsTimers = metricsTimer.getAllMetricsTimers();
			if (!metricsTimers.isEmpty()) {

				List<Measurement> measurements = getMeasurementList(metricsTimers);
				if (measurementMBean != null) {
					measurementMBean.addMeasurements(measurements);
				}
				boolean valuable = true;
				if (metricsAggregateWorker != null) {
					valuable = metricsAggregateWorker.checkMetricsTimers(metricsTimers);
				}
				if (valuable) {
					// measurements are valuable
					if (metricsPublishWorker != null) {
						// add them to the publish worker at once
						metricsPublishWorker.enqueuePublishable(measurements);
					}
				} else {
					logger.debug(metricsTimers.size() + " timers dropped from further processing");
				}

			}
		}
	}

	public MetricsTimer getMetricsTimer() {
		for (;;) {
			try {
				// it will wait till an item available
				return metricsTimerQueue.poll(checkInterval, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ie) {
				logger.warn("Poll operation on queue interrupted");
			}
		}
	}

	private void registerMBeans() {
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName measurementName = new ObjectName(mbeanObjectName);
			if (mbeanServer.isRegistered(measurementName)) {
				mbeanServer.unregisterMBean(measurementName);
			}
			measurementMBean = new JmxMeasurement();
			mbeanServer.registerMBean(measurementMBean, measurementName);
		} catch (Exception e) {
			String message = "Unable to register MBeans with error " + e.getMessage();
			logger.error(message, e);
		}
	}

	private List<Measurement> getMeasurementList(List<MetricsTimer> metricsTimers) {
		List<Measurement> measurementList = new ArrayList<Measurement>();
		for (MetricsTimer metricsTimer : metricsTimers) {
			measurementList.add(metricsTimer.getMeasurement());
		}
		return measurementList;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setMetricsPublishWorker(MetricsPublishWorker metricsPublishWorker) {
		this.metricsPublishWorker = metricsPublishWorker;
	}

	public void setMetricsAggregateWorker(MetricsAggregateWorker metricsAggregateWorker) {
		this.metricsAggregateWorker = metricsAggregateWorker;
	}

	public void setMbeanObjectName(String mbeanObjectName) {
		this.mbeanObjectName = mbeanObjectName;
	}

	public void setQueueCapacity(int queueCapacity) {
		metricsTimerQueue = new LinkedBlockingQueue<MetricsTimer>(queueCapacity);
	}

}
