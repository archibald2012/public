/**
 * 
 */
package edu.hziee.common.drools;

/**
 * @author Administrator
 * 
 */
public class DroolsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DroolsException(String message) {
		super(message);
	}

	public DroolsException(String message, Throwable cause) {
		super(message, cause);
	}

}
