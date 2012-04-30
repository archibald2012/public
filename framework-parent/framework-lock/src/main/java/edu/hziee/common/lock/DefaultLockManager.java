/**
 * 
 */
package edu.hziee.common.lock;

/**
 * @author Administrator
 * 
 */
public class DefaultLockManager implements LockManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.dlock.LockManager#checkLock()
	 */
	@Override
	public LockResource checkLock() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.dlock.LockManager#getLockStatus()
	 */
	@Override
	public LockStatus getLockStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.dlock.LockManager#acquireLock(java.lang.Object)
	 */
	@Override
	public void acquireLock(Object updateData) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.dlock.LockManager#releaseLock()
	 */
	@Override
	public boolean releaseLock() {
		// TODO Auto-generated method stub
		return false;
	}

}
