/**
 * 
 */
package edu.hziee.common.lang;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 * 
 */
public class RoundRobin<E> implements Iterator<E> {

	private final List<E>	dataset;
	private AtomicInteger	index	= new AtomicInteger(0);

	public RoundRobin(List<E> dataSet) {
		this.dataset = Collections.unmodifiableList(dataSet);
	}

	@Override
	public boolean hasNext() {
		return dataset.size() > 0;
	}

	@Override
	public E next() {
		return hasNext() ? dataset.get(getIndex()) : null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove operation not supported in <RoundRobin> collection.");
	}

	private int getIndex() {
		int next = index.getAndIncrement();
		if (next < 0) {
			next = 0;
			index.set(next);
		}
		return next % dataset.size();
	}

}
