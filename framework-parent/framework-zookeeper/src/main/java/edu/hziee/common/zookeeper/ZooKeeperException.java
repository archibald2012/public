/**
 * 
 */
package edu.hziee.common.zookeeper;

/**
 * @author Administrator
 * 
 */
public class ZooKeeperException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZooKeeperException(String message) {
		super(message);
	}

	public ZooKeeperException(String message, Throwable cause) {
		super(message, cause);
	}

}
