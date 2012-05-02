/**
 * 
 */
package edu.hziee.common.lock;

/**
 * @author Administrator
 * 
 */
public interface LockUpdater {
	LockResult acquireLock(String resName, String lockInstance);

	LockResult releaseLock(String resName, String lockInstance);
}
