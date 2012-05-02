/**
 * 
 */
package edu.hziee.common.lock;

public interface DistributedLock {

	LockStatus getLockStatus();

	void acquireLock();

	boolean releaseLock();

}
