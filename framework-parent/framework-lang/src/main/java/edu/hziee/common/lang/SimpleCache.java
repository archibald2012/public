
package edu.hziee.common.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: SimpleCache.java 14 2012-01-10 11:54:14Z archie $
 */
public class SimpleCache<K, V> {
	private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();

	public V get(K key, Callable<V> ifAbsent) {
		V value = map.get(key);
		if (value == null) {
			try {
				value = ifAbsent.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			map.putIfAbsent(key, value);
		}
		return value;
	}
}
