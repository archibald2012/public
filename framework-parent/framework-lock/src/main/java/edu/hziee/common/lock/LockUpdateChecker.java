/**
 * 
 */
package edu.hziee.common.lock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * 
 */
public class LockUpdateChecker {

	private DistributedLock						lockManager			= null;
	private long											updateInterval	= 5L;
	private ScheduledExecutorService	exec						= null;

	public LockUpdateChecker(DistributedLock lockManager, long updateInterval) {
		this.lockManager = lockManager;
		this.updateInterval = updateInterval;
		this.exec = Executors.newSingleThreadScheduledExecutor();
	}

	public void start() {
		exec.submit(new Runnable() {

			public void run() {
				doScheduleNextCheck();
			}
		});
	}

	public void stop() {
		exec.shutdownNow();
	}

	private void doScheduleNextCheck() {
		exec.schedule(new Runnable() {

			public void run() {
				doCheck();
			}
		}, updateInterval, TimeUnit.SECONDS);
	}

	private void doCheck() {
		exec.submit(new Runnable() {

			public void run() {
				doScheduleNextCheck();
			}
		});

		lockManager.acquireLock();
	}
}
