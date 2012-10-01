/**
 * 
 */
package edu.hziee.common.metrics.publish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.model.Aggregation;
import edu.hziee.common.metrics.model.Measurement;
import edu.hziee.common.metrics.model.Publishable;
import edu.hziee.common.metrics.model.Record;
import edu.hziee.common.metrics.model.ResourceUsage;
import edu.hziee.common.metrics.util.MetricsUtil;
import edu.hziee.common.metrics.util.StopTimer;
import edu.hziee.common.metrics.worker.MetricsPublishWorker;

/**
 * @author Administrator
 * 
 */
public class DefaultPublishWorker extends Thread implements MetricsPublishWorker {

	private static final Logger								logger							= LoggerFactory.getLogger(DefaultPublishWorker.class);

	private String														domain;
	private String														host;
	private String														applicationName;
	private String														frameworkName;
	private String														version;
	private String														user;
	private String														pid;
	private int																triggerSize					= 100;
	private int																publishSize					= 500;
	private int																timeInterval				= 5000;

	private List<MetricsPublisher>						metricsPublisherList;
	private LinkedBlockingQueue<Publishable>	publishQueue				= new LinkedBlockingQueue<Publishable>(10000);
	private final static Object								publishQueueMonitor	= new Object();

	@Override
	public void enqueuePublishable(Publishable publishable) {
		if (!publishQueue.offer(publishable)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Drop 1 item with publish queue at its capacity " + publishQueue.size());
			}
		}

		// check to see should it wake up waiting thread
		if (publishQueue.size() >= publishSize) {
			synchronized (publishQueueMonitor) {
				// notify the waiting threads to publish
				publishQueueMonitor.notify();
			}
		}
	}

	@Override
	public void enqueuePublishable(Collection<? extends Publishable> publishables) {
		boolean isNotify = false;

		int dropCount = 0;
		for (Publishable publishable : publishables) {
			if (!publishQueue.offer(publishable)) {
				dropCount++;
			}

			if (!isNotify) {
				// check to see should it wake up waiting thread
				if (publishQueue.size() >= publishSize) {
					synchronized (publishQueueMonitor) {
						// notify the waiting threads to publish
						publishQueueMonitor.notify();
						isNotify = true;
					}
				}
			}
		}

		if (dropCount > 0 && logger.isDebugEnabled()) {
			logger.debug("Drop " + dropCount + " item(s) with publish queue at its capacity " + publishQueue.size());
		}

	}

	protected void publishMetrics() {
		List<Publishable> list = getPublishables();

		if (!list.isEmpty()) {
			StopTimer timer = new StopTimer();

			Record record = new Record(domain, host, applicationName, frameworkName, version, user, pid);

			for (Publishable publishable : list) {
				if (publishable instanceof Measurement) {
					record.getMeasurementList().add((Measurement) publishable);
				} else if (publishable instanceof Aggregation) {
					if (record.getAggregationRanges() == null) {
						record.setAggregationRanges(((Aggregation) publishable).getRanges());
					}
					record.getAggregationList().add((Aggregation) publishable);
				} else if (publishable instanceof ResourceUsage) {
					record.getUsageList().add((ResourceUsage) publishable);
				}
			}

			for (MetricsPublisher publisher : metricsPublisherList) {
				publisher.publish(record);
			}

			if (logger.isDebugEnabled()) {
				logger.debug(metricsPublisherList.size() + " publishers process " + list.size() + " publishables in "
						+ timer.check() + " ms.");
			}
		}
	}

	private List<Publishable> getPublishables() {
		List<Publishable> list = new ArrayList<Publishable>();

		if (publishQueue.size() < triggerSize) {
			synchronized (publishQueueMonitor) {
				try {
					publishQueueMonitor.wait(timeInterval);
				} catch (InterruptedException ignore) {
				}
			}
		}

		publishQueue.drainTo(list, publishSize);

		return list;
	}

	public void setDomain(String domain) {
		this.domain = MetricsUtil.truncate(domain, 32);
	}

	public void setHost(String host) {
		this.host = MetricsUtil.truncate(host, 32);
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = MetricsUtil.truncate(applicationName, 32);
	}

	public void setFrameworkName(String frameworkName) {
		this.frameworkName = MetricsUtil.truncate(frameworkName, 32);
	}

	public void setVersion(String version) {
		this.version = MetricsUtil.truncate(version, 16);
	}

	public void setUser(String user) {
		this.user = MetricsUtil.truncate(user, 32);
	}

	public void setPid(String pid) {
		this.pid = MetricsUtil.truncate(pid, 16);
	}

	public void setQueueCapacity(int queueCapacity) {
		publishQueue = new LinkedBlockingQueue<Publishable>(queueCapacity);
	}

	public void setTriggerSize(int triggerSize) {
		this.triggerSize = triggerSize;
	}

	public void setPublishSize(int publishSize) {
		this.publishSize = publishSize;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setPublishQueue(LinkedBlockingQueue<Publishable> publishQueue) {
		this.publishQueue = publishQueue;
	}

}
