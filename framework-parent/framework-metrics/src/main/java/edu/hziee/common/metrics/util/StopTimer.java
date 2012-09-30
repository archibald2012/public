package edu.hziee.common.metrics.util;

public class StopTimer {

	private static final long NANOS_IN_MILLIS = 1000000L;

	private long startNano;

	public StopTimer() {
		startNano = System.nanoTime();
	}

	public long reset() {
		long originalNano = startNano;
		startNano = System.nanoTime();
		return (startNano - originalNano) / NANOS_IN_MILLIS;
	}

	public long check() {
		return (System.nanoTime() - startNano) / NANOS_IN_MILLIS;
	}
}
