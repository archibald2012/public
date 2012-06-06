/**
 * 
 */
package edu.hziee.common.metrics;

/**
 * @author Administrator
 * 
 */
public class MetricsException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	public MetricsException(String message) {
		super(message);
	}

	public MetricsException(String message, Throwable cause) {
		super(message, cause);
	}
}
