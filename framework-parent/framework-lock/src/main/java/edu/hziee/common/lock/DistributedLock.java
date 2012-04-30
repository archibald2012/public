/**
 * 
 */
package edu.hziee.common.lock;

/**
 * @author Administrator
 * 
 */
public interface DistributedLock {

	void acquireLock(LockUpdateCallback updateCallback, Object callbackData);

	void releaseLock();
}
