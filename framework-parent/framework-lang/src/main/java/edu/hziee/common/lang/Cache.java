
package edu.hziee.common.lang;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Cache.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Cache<K, V> {

	V get(K key);

	boolean put(K key, V value);

	boolean put(K key, V value, int TTL);

	boolean update(K key, V value);

	boolean remove(K key);

	boolean clear();

	void destroy();

	boolean containsKey(K key);

	boolean flushAll();

	boolean isConnected();
}
