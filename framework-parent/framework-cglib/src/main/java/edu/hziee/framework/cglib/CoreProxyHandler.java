/**
 * 
 */
package edu.hziee.framework.cglib;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.MetricsCollector;
import edu.hziee.common.metrics.MetricsCollectorFactory;
import edu.hziee.common.metrics.MetricsTimer;

/**
 * @author Administrator
 * 
 */
public class CoreProxyHandler {
	private static final Logger											logger						= LoggerFactory.getLogger(CoreProxyHandler.class);

	/**
	 * For holding the metrics collector for the component.
	 */
	private ConcurrentMap<String, MetricsCollector>	collectorMap			= new ConcurrentHashMap<String, MetricsCollector>();

	private static ThreadLocal<MetricsTimer>				localMetricsTimer	= new ThreadLocal<MetricsTimer>();

	private CoreProxyHandler() {
	}

	/**
	 * For returning the internal singleton.
	 * 
	 * @return
	 */
	public static CoreProxyHandler getInstance() {
		return CoreProxyHandlerHolder.instance;
	}

	/**
	 * Start metrics timer with component name and function name.
	 * 
	 * @param component
	 * @param function
	 * @param argument
	 * @return
	 */
	public MetricsTimer startMetricsTimer(String component, String function, Object argument) {
		if (logger.isTraceEnabled()) {
			logger.trace("Start metrics on component " + component + " function " + function);
		}

		MetricsCollector metricsCollector = collectorMap.get(component);
		if (metricsCollector == null) {
			MetricsCollector newCollector = MetricsCollectorFactory.getMetricsCollector(component);
			MetricsCollector oldCollector = collectorMap.putIfAbsent(component, newCollector);
			if (oldCollector != null) {
				metricsCollector = oldCollector;
			} else {
				metricsCollector = newCollector;
			}
		}

		MetricsTimer metricsTimer = metricsCollector.startMetricsTimer(function);
		if (argument != null) {
			metricsTimer.addMetrics(argument);
		}

		localMetricsTimer.set(metricsTimer);

		return metricsTimer;
	}

	public void stopMetricsTimer(MetricsTimer metricsTimer, Object argument, Throwable exception) {
		if (logger.isTraceEnabled()) {
			logger.trace("Stop metrics timer " + metricsTimer + " exception " + exception);
		}

		if (argument != null) {
			metricsTimer.addMetrics(argument);
		}

		metricsTimer.stop(exception);

		localMetricsTimer.remove();
	}

	/**
	 * Return the current metrics timer from the proxy handler. It might be null if there is no current measurement.
	 * 
	 * @return
	 */
	public static MetricsTimer getMetricsTimer() {
		return localMetricsTimer.get();
	}

	private static class CoreProxyHandlerHolder {
		private static final CoreProxyHandler	instance	= new CoreProxyHandler();
	}
}
