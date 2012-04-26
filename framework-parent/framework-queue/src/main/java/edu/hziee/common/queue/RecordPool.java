
package edu.hziee.common.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: RecordPool.java 14 2012-01-10 11:54:14Z archie $
 */
public class RecordPool<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(RecordPool.class);

	private BlockingQueue<T> queue;

	private int batchSize = 500;

	public RecordPool(int poolSize) {
		queue = new LinkedBlockingQueue<T>(poolSize);
	}

	public boolean add(T rec) {
		boolean ret = false;
		if (rec != null && !queue.contains(rec)) {
			ret = queue.offer(rec);
			if (!ret) {
				if (logger.isWarnEnabled()) {
					logger.warn("add the object to the queue failed, the queue may be full.");
				}
			}
		}
		return ret;
	}

	public List<T> asList() {
		List<T> recordsCopy = new ArrayList<T>();
		try {
			if (queue.size() > 0) {
				synchronized (queue) {
					int num = queue.size() >= batchSize ? batchSize : queue
							.size();
					queue.drainTo(recordsCopy, num);
				}
			}
		} catch (Exception ex) {
			logger.error(">>>> Excute Get Queue Exception:", ex);
		}
		return recordsCopy;
	}

	public List<T> getWholeRecords() {
		List<T> recordsCopy = new ArrayList<T>();
		synchronized (queue) {
			int num = queue.size();
			if (num != 0) {
				queue.drainTo(recordsCopy, num);
			}
		}
		return recordsCopy;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int remainCapacity() {
		return this.queue.remainingCapacity();
	}

	public int size() {
		return this.queue.size();
	}
}
