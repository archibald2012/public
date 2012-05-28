/**
 * 
 */
package edu.hziee.common.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Administrator
 * 
 */
public class RoundRobin<E> implements Iterator<E> {

	private final Collection<E> dataset;
	private int index = 0;
	private int size = 0;
	private Iterator<E> iterator;

	public RoundRobin(Collection<E> dataSet) {
		this.dataset = Collections.unmodifiableCollection(dataSet);
		this.size = dataset.size();
		this.iterator = dataset.iterator();
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public E next() {
		E next = iterator.next();
		reset();
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void reset() {
		if ((++index % size) == 0) {
			iterator = dataset.iterator();
			index = 0;
		}
	}

}
