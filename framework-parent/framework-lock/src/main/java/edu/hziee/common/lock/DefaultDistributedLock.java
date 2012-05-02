/**
 * 
 */
package edu.hziee.common.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.SystemUtil;

/**
 * @author Administrator
 * 
 */
public class DefaultDistributedLock implements DistributedLock {

	private static final Logger			logger					= LoggerFactory.getLogger(DefaultDistributedLock.class);

	private String									lockId;
	private String									lockInstance;
	private LockStatus							lockStatus			= LockStatus.LOCK_ACQUIRED;

	private LockUpdater							lockUpdater;
	private LockCallback						lockCallback;

	private ReentrantReadWriteLock	reentrantLock		= new ReentrantReadWriteLock();
	private LockUpdateChecker				lockUpdateChecker;
	private long										updateInterval	= 10L;

	public void start() {
		if (lockInstance == null) {
			lockInstance = SystemUtil.getHostName() + ":" + SystemUtil.getPid();
		}

		lockUpdateChecker = new LockUpdateChecker(this, updateInterval);
		lockUpdateChecker.start();
	}

	public void stop() {
		lockUpdateChecker.stop();
	}

	@Override
	public void acquireLock() {
		logger.info(lockInstance + " acquiring an initial lock on lockId " + lockId);

		LockResult lockResult = lockUpdater.acquireLock(lockId, lockInstance);

		if (lockResult.getException() != null) {
			logger.error(lockInstance + " unable to acquire lock on lockId", lockResult.getException());
			lockCallback.exceptionCaught(lockResult.getException());
			return;
		}

		updateStatus(lockResult.getStatus());
	}

	@Override
	public boolean releaseLock() {
		logger.info(lockInstance + " release a lock on lockId " + lockId);

		LockResult result = lockUpdater.releaseLock(lockId, lockInstance);
		updateStatus(result.getStatus());
		return LockStatus.LOCK_RELEASED == result.getStatus();
	}

	@Override
	public LockStatus getLockStatus() {
		reentrantLock.readLock().lock();
		try {
			return lockStatus;
		} finally {
			reentrantLock.readLock().unlock();
		}
	}

	private void updateStatus(LockStatus newStatus) {
		boolean isUpdated = false;
		reentrantLock.writeLock().lock();
		try {
			if (lockStatus != newStatus) {
				isUpdated = true;
			}
			lockStatus = newStatus;
		} finally {
			reentrantLock.writeLock().unlock();
		}

		if (isUpdated && lockCallback != null) {
			try {
				if (LockStatus.LOCK_ACQUIRED == lockStatus) {
					lockCallback.lockAcquired(lockId);
				} else if (LockStatus.LOCK_RELEASED == lockStatus) {
					lockCallback.lockReleased(lockId);
				} else if (LockStatus.LOCK_BLOCKED == lockStatus) {
					lockCallback.lockBlocked(lockId);
				}
			} catch (RuntimeException e) {
				logger.warn(lockInstance + " caught exception " + e.getMessage() + " for lock resource " + lockId, e);
				throw e;
			}
		}
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

	public String getLockInstance() {
		return lockInstance;
	}

	public void setLockUpdater(LockUpdater lockUpdater) {
		this.lockUpdater = lockUpdater;
	}

	public void setLockCallback(LockCallback lockCallback) {
		this.lockCallback = lockCallback;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		reentrantLock.readLock().lock();
		try {
			builder.append(",instance=").append(lockInstance);
			builder.append(",lockId=").append(lockId);
			builder.append(",status=").append(lockStatus);
		} finally {
			reentrantLock.readLock().unlock();
		}
		return builder.toString();
	}

}
