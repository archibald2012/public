/**
 * 
 */
package edu.hziee.common.metrics;

import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.worker.MetricsProcessWorker;
import edu.hziee.common.metrics.worker.MetricsRuntimeWorker;

/**
 * @author Administrator
 * 
 */
public class DefaultMetricsEngine implements MetricsEngine {
	private static final Logger											logger					= LoggerFactory.getLogger(DefaultMetricsEngine.class);

	private static final String											KEY_JOIN				= "-";

	private AtomicBoolean														collectMetrics	= new AtomicBoolean(false);

	private AtomicBoolean														throwException	= new AtomicBoolean(false);

	private MetricsProcessWorker										metricsProcessWorker;

	private MetricsRuntimeWorker										metricsRuntimeWorker;

	private final ThreadLocal<Stack<MetricsTimer>>	localTimerStack	= new ThreadLocal<Stack<MetricsTimer>>();

	private ConcurrentMap<String, Boolean>					filterMap				= new ConcurrentHashMap<String, Boolean>();

	private boolean																	filterAll				= false;

	private ReentrantLock														filterMapLock		= new ReentrantLock();

	@Override
	public boolean isCollectMetrics() {
		return collectMetrics.get();
	}

	@Override
	public void setCollectMetrics(boolean collectMetrics) {
		this.collectMetrics.set(collectMetrics);
	}

	@Override
	public boolean isThrowException() {
		return throwException.get();
	}

	@Override
	public void setThrowException(boolean throwException) {
		this.throwException.set(throwException);
	}

	@Override
	public MetricsCollector createMetricsCollector(String componentName) {
		return new DefaultMetricsCollector(this, componentName);
	}

	@Override
	public Stack<MetricsTimer> getTimerStack() {
		Stack<MetricsTimer> timerStack = localTimerStack.get();
		if (timerStack == null) {
			timerStack = new Stack<MetricsTimer>();
			localTimerStack.set(timerStack);
		}
		return timerStack;
	}

	@Override
	public CorrelationInfo getCorrelationInfo() {
		CorrelationInfo correlationInfo = null;

		Stack<MetricsTimer> timerStack = getTimerStack();
		if (!timerStack.isEmpty()) {
			MetricsTimer metricsTimer = getTimerStack().peek();
			correlationInfo = metricsTimer.getCorrelationInfo();
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Correlation infomation is unavailable");
			}
		}
		return correlationInfo;
	}

	@Override
	public MetricsTimer pushTimer(MetricsTimer timer) {
		MetricsTimer parent = null;

		Stack<MetricsTimer> timerStack = getTimerStack();
		if (!timerStack.isEmpty()) {
			parent = timerStack.peek();
		}
		timerStack.push(timer);

		return parent;
	}

	public MetricsTimer popTimer(MetricsTimer timer) {
		Stack<MetricsTimer> timerStack = getTimerStack();

		while (!timerStack.isEmpty()) {
			MetricsTimer temp = timerStack.pop();
			if (temp.equals(timer)) {
				return timer;
			} else {
				if (isThrowException()) {
					throw new MetricsException("Invalid stack timer " + temp);
				} else {
					logger.warn("Invalid stack timer " + temp);
				}
			}
		}

		if (isThrowException()) {
			throw new MetricsException("Unable to find timer " + timer);
		} else {
			logger.warn("Unable to find timer " + timer);
		}

		return null;
	}

	public void enqueueMetricsTimer(MetricsTimer metricsTimer) {
		if (metricsProcessWorker != null) {
			metricsProcessWorker.enqueueMetricsTimer(metricsTimer);
		}
	}

	@Override
	public boolean isFilter(String componentName, String functionName) {
		boolean isFitler = false;

		filterMapLock.lock();
		try {
			if (filterAll) {
				isFitler = true;
			} else {
				String key = StringUtils.join(new Object[] { componentName, functionName }, KEY_JOIN);
				Boolean result = filterMap.get(key);
				if (result != null) {
					isFitler = result;
				} else {
					key = StringUtils.join(new Object[] { componentName, "" }, KEY_JOIN);
					result = filterMap.get(key);
					if (result != null) {
						isFitler = result;
					}
				}
			}
		} finally {
			filterMapLock.unlock();
		}
		return isFitler;
	}

	@Override
	public boolean updateFilter(String componentName, String functionName, boolean isFilter) {
		boolean oldValue = false;

		filterMapLock.lock();
		try {
			if (StringUtils.isBlank(componentName) && StringUtils.isBlank(functionName)) {
				oldValue = filterAll;
				filterAll = isFilter;
			} else if (StringUtils.isNotBlank(componentName) && StringUtils.isNotBlank(functionName)) {
				String key = StringUtils.join(new Object[] { componentName, functionName }, KEY_JOIN);
				Boolean result = filterMap.put(key, new Boolean(isFilter));
				if (result != null) {
					oldValue = result;
				}
			} else if (StringUtils.isNotBlank(componentName)) {
				String key = StringUtils.join(new Object[] { componentName, "" }, KEY_JOIN);
				Boolean result = filterMap.get(key);
				if (result != null) {
					oldValue = result;
				}
				Set<String> keySet = filterMap.keySet();
				for (String entry : keySet) {
					if (entry.startsWith(key)) {
						filterMap.remove(entry);
					}
				}
				filterMap.put(key, new Boolean(isFilter));
			} else {
				logger.warn("Invalid component " + componentName + " function " + functionName + " isFilter " + isFilter);
			}
		} finally {
			filterMapLock.unlock();
		}
		return oldValue;
	}

	public void setMetricsProcessWorker(MetricsProcessWorker metricsProcessWorker) {
		this.metricsProcessWorker = metricsProcessWorker;
	}

	public void setMetricsRuntimeWorker(MetricsRuntimeWorker metricsRuntimeWorker) {
		this.metricsRuntimeWorker = metricsRuntimeWorker;
	}

	public MetricsRuntimeWorker getMetricsRuntimeWorker() {
		return metricsRuntimeWorker;
	}

}
