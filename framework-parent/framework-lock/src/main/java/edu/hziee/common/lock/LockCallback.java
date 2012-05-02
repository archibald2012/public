/**
 * 
 */
package edu.hziee.common.lock;

/**
 * @author Administrator
 * 
 */
public interface LockCallback {

	void lockAcquired(String lockedId);
	
	void lockReleased(String lockedId);

	void lockBlocked(String lockedId);

	void exceptionCaught(Throwable e);
}
