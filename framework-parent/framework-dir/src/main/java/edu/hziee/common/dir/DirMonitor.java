
package edu.hziee.common.dir;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DirMonitor.java 14 2012-01-10 11:54:14Z archie $
 */
public class DirMonitor {
	private static final Logger logger = LoggerFactory
			.getLogger(DirMonitor.class);

	private DirChangeListener listener;
	private String dirName;
	private Timer timer = new Timer();
	private long checkInterval = 1000;
	private DirSnapshot lastSnapshot = null;
	private String[] includeSuffixs = null;
	private AtomicBoolean running = new AtomicBoolean(true);

	public void start() {
		running.set(true);
		scheduleNextCheck();
	}

	public void stop() {
		running.set(false);
		timer.cancel();
	}

	/**
	 * @param includeSuffixs
	 *            the includeSuffixs to set
	 */
	public void setIncludeSuffixs(List<String> includeSuffixs) {
		this.includeSuffixs = includeSuffixs.toArray(new String[0]);
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(String dir) {
		this.dirName = dir;
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public String getDir() {
		return this.dirName;
	}

	/**
	 * @param checkTimeout
	 *            the checkTimeout to set
	 */
	public void setCheckTimeout(long checkTimeout) {
		this.checkInterval = checkTimeout;
	}

	public long getCheckTimeout() {
		return checkInterval;
	}

	private void checkDir() {
		boolean modified = false;
		if (null == lastSnapshot) {
			modified = true;
			lastSnapshot = new DirSnapshot(dirName, includeSuffixs);
		} else {
			DirSnapshot newSnapshot = new DirSnapshot(dirName, includeSuffixs);
			if (!lastSnapshot.equals(newSnapshot)) {
				modified = true;
				lastSnapshot = newSnapshot;
			}
		}
		if (modified && running.get()) {
			if (logger.isDebugEnabled()) {
				logger.debug("checkDir: " + dirName + " is changed.");
			}
			if (null != listener) {
				listener.onDirChanged(dirName);
			}
		}
	}

	private void scheduleNextCheck() {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					checkDir();
				} catch (Exception e) {
					logger.error("checkDir: ", e);
				} finally {
					scheduleNextCheck();
				}
			}
		}, checkInterval);
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(DirChangeListener listener) {
		this.listener = listener;
	}

	public boolean isRunning() {
		return running.get();
	}
}
