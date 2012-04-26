
package edu.hziee.common.lang.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.hziee.common.lang.Holder;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultHolder.java 14 2012-01-10 11:54:14Z archie $
 */
public class DefaultHolder implements Holder {

	private Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();

	/* (non-Javadoc)
	 * @see com.taotaosou.common.transport.cache.Holder#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(Object key, Object value) {
		map.put(key, value);
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.transport.cache.Holder#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.transport.cache.Holder#getAndRemove(java.lang.Object)
	 */
	@Override
	public Object getAndRemove(Object key) {
		Object ret = map.get(key);
		map.remove(key);
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.transport.cache.Holder#remove(java.lang.Object)
	 */
	@Override
	public void remove(Object key) {
		map.remove(key);
	}

}
