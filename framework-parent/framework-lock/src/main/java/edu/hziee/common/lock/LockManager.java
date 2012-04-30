/**
 * 
 */
package edu.hziee.common.lock;

/**
 * @author Administrator
 *
 */
public interface LockManager {

	LockResource checkLock();
	
	LockStatus getLockStatus();
	
	void acquireLock(Object updateData);
	
	boolean releaseLock();
	
}
