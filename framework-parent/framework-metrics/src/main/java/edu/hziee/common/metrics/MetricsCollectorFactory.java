/**
 * 
 */
package edu.hziee.common.metrics;

/**
 * @author Administrator
 * 
 */
public class MetricsCollectorFactory {

	private static MetricsEngine metricsEngine;
	
	private MetricsCollectorFactory() {
	}
	
	public static MetricsCollector getMetricsCollector(final Class<? extends Object> clazz){
		return getMetricsCollector(clazz.getSimpleName());
	}
	
	public static MetricsCollector getMetricsCollector(final String componentName){
		return metricsEngine.createMetricsCollector(componentName);
	}
}
