
package edu.hziee.common.dbroute.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DBRoute.java 4 2012-01-10 11:51:54Z archie $
 */
public class DBRoute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6594251491917354115L;

	private String xid = null;
	private Map<String, String> items = new HashMap<String, String>();

	private DBRoutingStrategy routingStrategy = DBRoutingStrategy.BY_XID;

	public DBRoute() {
	}

	public DBRoute(String xid) {
		setXid(xid);
	}

	public static DBRoute getRoute(String xid) {
		DBRoute back = new DBRoute();
		back.setXid(xid);
		return back;
	}

	public static DBRoute getRouteByItems(Map<String, String> items) {
		DBRoute back = new DBRoute();
		back.setItems(items);
		return back;
	}

	public static DBRoute getRouteByItem(String key, String value) {
		DBRoute back = new DBRoute();
		back.putItem(key, value);
		return back;
	}

	/**
	 * @return the xid
	 */
	public String getXid() {
		return xid;
	}

	/**
	 * @param xid
	 *            the xid to set
	 */
	public void setXid(String xid) {
		this.xid = xid;
		routingStrategy = DBRoutingStrategy.BY_XID;
	}

	public Map<String, String> getItems() {
		return items;
	}

	public void setItems(Map<String, String> items) {
		this.items = items;
		routingStrategy = DBRoutingStrategy.BY_ITEM;
	}

	public void putItem(String itemName, String itemValue) {
		this.items.put(itemName, itemValue);
		routingStrategy = DBRoutingStrategy.BY_ITEM;
	}

	public DBRoutingStrategy getRoutingStrategy() {
		return routingStrategy;
	}

	public void setRoutingStrategy(DBRoutingStrategy routingStrategy) {
		this.routingStrategy = routingStrategy;
	}

	public String toString() {
		String str = "";

		if ((routingStrategy == DBRoutingStrategy.BY_XID) && (xid != null)) {
			str += String.format("[{}:{}]", routingStrategy, xid);
			return str;
		}

		if ((routingStrategy == DBRoutingStrategy.BY_ITEM)
				&& (!items.isEmpty())) {
			str += String.format("[{}]", routingStrategy);
			for (Map.Entry<String, String> item : items.entrySet()) {
				str += String.format("[{}:{}]", item.getKey(), item.getValue());
			}
			return str;
		}

		return null;
	}
}
