/**
 * 
 */
package edu.hziee.common.metrics.runtime;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.MetricsPublishWorker;
import edu.hziee.common.metrics.model.CollectorUsage;
import edu.hziee.common.metrics.model.HeapUsage;
import edu.hziee.common.metrics.model.ResourceUsage;
import edu.hziee.common.metrics.model.ThreadUsage;
import edu.hziee.common.metrics.util.SystemUtil;

/**
 * @author Administrator
 * 
 */
public class DefaultRuntimeWorker extends Thread {
	private static final Logger		logger				= LoggerFactory.getLogger(DefaultRuntimeWorker.class);

	private boolean								collectUsage	= false;
	private boolean								detailThread	= false;
	private boolean								detailHeap		= false;
	private long									checkInterval	= 60000;
	private long									startNano			= System.nanoTime();

	private MetricsPublishWorker	metricsPublishWorker;

	public final void run() {
		while (true) {
			checkRuntimeMetrics();
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				logger.warn("Failed to check runtime metrics with error " + e.getMessage(), e);
			}
		}
	}

	private void checkRuntimeMetrics() {
		if (collectUsage) {
			try {
				// add them to the publish worker at once.
				ResourceUsage usage = new ResourceUsage();
				getHeapUsage(usage);
				getThreadUsage(usage);
				usage.setUsageId(SystemUtil.createGuid());
				usage.setCheckTime(new Date());
				metricsPublishWorker.enqueuePublishable(usage);
			} catch (Exception e) {
				logger.warn("Failed to collect runtime ata with error " + e.getMessage(), e);
			}
		}

	}

	private void getHeapUsage(ResourceUsage usage) {
		MemoryMXBean memoryMBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = memoryMBean.getHeapMemoryUsage();
		usage.setHeapMax(heapUsage.getMax());
		usage.setHeapUsed(heapUsage.getUsed());

		MemoryUsage nonHeapUsage = memoryMBean.getNonHeapMemoryUsage();
		usage.setNonHeapMax(nonHeapUsage.getMax());
		usage.setNonHeapUsed(nonHeapUsage.getUsed());

		if (detailHeap) {
			List<MemoryPoolMXBean> memoryPoolList = ManagementFactory.getMemoryPoolMXBeans();
			for (MemoryPoolMXBean memoryPool : memoryPoolList) {
				MemoryUsage memoryUsage = memoryPool.getUsage();
				if (memoryUsage != null) {
					usage.getHeapList().add(new HeapUsage(memoryPool.getName(), memoryUsage.getMax(), memoryUsage.getUsed()));
				}
			}

			List<GarbageCollectorMXBean> collectorList = ManagementFactory.getGarbageCollectorMXBeans();
			for (GarbageCollectorMXBean collector : collectorList) {
				usage.getCollectorList().add(
						new CollectorUsage(collector.getName(), collector.getCollectionCount(), collector.getCollectionTime()));
			}
		}
	}

	private void getThreadUsage(ResourceUsage usage) {

		long cpuTotal = 0;
		long userTotal = 0;
		ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
		long[] threads = threadMBean.getAllThreadIds();
		for (int i = 0; i < threads.length; i++) {
			long time = threadMBean.getThreadCpuTime(threads[i]);
			if (time > 0.0) {
				cpuTotal += time;
			}
			time = threadMBean.getThreadUserTime(threads[i]);
			if (time > 0.0) {
				userTotal += time;
			}
			if (detailThread) {
				ThreadInfo info = threadMBean.getThreadInfo(threads[i]);
				if (info != null) {
					usage.getThreadList().add(
							new ThreadUsage(info.getThreadName(), info.getThreadState().toString(), threadMBean
									.getThreadCpuTime(threads[i]), threadMBean.getThreadUserTime(threads[i])));
				}
			}
		}

		usage.setProcessorCount(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
		usage.setThreadCount(threads.length);
		usage.setUpTime(System.nanoTime() - startNano);
		usage.setCpuTime(cpuTotal);
		usage.setUserTime(userTotal);
	}

	public void setCollectUsage(boolean collectUsage) {
		this.collectUsage = collectUsage;
	}

	public void setDetailThread(boolean detailThread) {
		this.detailThread = detailThread;
	}

	public void setDetailHeap(boolean detailHeap) {
		this.detailHeap = detailHeap;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setMetricsPublishWorker(MetricsPublishWorker metricsPublishWorker) {
		this.metricsPublishWorker = metricsPublishWorker;
	}

}
