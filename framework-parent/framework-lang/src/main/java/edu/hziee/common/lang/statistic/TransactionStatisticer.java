
package edu.hziee.common.lang.statistic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TransactionStatisticer.java 550 2011-07-08 07:03:23Z
 *          archie.wang $
 */
public class TransactionStatisticer {

	private static class Metric {

		public AtomicInteger handledTransaction = new AtomicInteger(0);
		public AtomicInteger finishedTransaction = new AtomicInteger(0);

		public AtomicInteger handledThroughput = new AtomicInteger(0);
		public AtomicInteger finishedThroughput = new AtomicInteger(0);

		private int lastHandledTransaction = 0;
		private int lastFinishedTransaction = 0;

		public void calculatePerformance(long interval) {
			int handledTransactionNow = handledTransaction.get();
			int finishedTransactionNow = finishedTransaction.get();

			handledThroughput
					.set((int) ((handledTransactionNow - lastHandledTransaction) * 1000 / interval));
			lastHandledTransaction = handledTransactionNow;

			finishedThroughput
					.set((int) ((finishedTransactionNow - lastFinishedTransaction) * 1000 / interval));
			lastFinishedTransaction = finishedTransactionNow;
		}
	}

	private Metric metric = new Metric();

	private Timer timer = new Timer();

	private long caculateInterval = 1000;
	private long lastTimestamp = 0;

	private void calculatePerformance() {
		long now = System.currentTimeMillis();
		long interval = now - lastTimestamp;

		if (interval > 0) {//避免除0
			metric.calculatePerformance(interval);
		}
		lastTimestamp = now;
	}

	public void start() {
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				calculatePerformance();
			}
		}, caculateInterval, caculateInterval);
	}

	public void stop() {
		timer.cancel();
	}

	public long getCaculateInterval() {
		return caculateInterval;
	}

	public void setCaculateInterval(long caculateInterval) {
		this.caculateInterval = caculateInterval * 1000;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}

	public AtomicInteger getHandledTransaction() {
		return metric.handledTransaction;
	}

	public AtomicInteger getFinishedTransaction() {
		return metric.finishedTransaction;
	}

	public AtomicInteger getHandledThroughput() {
		return metric.handledThroughput;
	}

	public AtomicInteger getFinishedThroughput() {
		return metric.finishedThroughput;
	}

	public void incHandledTransactionEnd() {
		getFinishedTransaction().incrementAndGet();
	}

	public void incHandledTransactionStart() {
		getHandledTransaction().incrementAndGet();
	}
}
