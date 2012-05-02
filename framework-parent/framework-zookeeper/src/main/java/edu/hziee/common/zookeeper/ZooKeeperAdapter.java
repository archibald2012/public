/**
 * 
 */
package edu.hziee.common.zookeeper;

import java.util.List;

import org.apache.zookeeper.AsyncCallback.ACLCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperAdapter implements ZooKeeperOperations {

	private ZooKeeper	zk;

	/**
	 * @return the zk
	 */
	public ZooKeeper getZk() {
		return zk;
	}

	/**
	 * @param zk
	 *          the zk to set
	 */
	public ZooKeeperOperations setZk(ZooKeeper zk) {
		this.zk = zk;
		return this;
	}

	public boolean isConnected() {
		return (null != zk) && (zk.getState() == ZooKeeper.States.CONNECTED);
	}

	public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) {
		try {
			return zk.create(path, data, acl, createMode);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode, StringCallback cb, Object ctx) {
		zk.create(path, data, acl, createMode, cb, ctx);
	}

	public void delete(String path, int version) {
		try {
			zk.delete(path, version);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void delete(String path, int version, VoidCallback cb, Object ctx) {
		zk.delete(path, version, cb, ctx);
	}

	public Stat exists(String path, Watcher watcher) {
		try {
			return zk.exists(path, watcher);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public Stat exists(String path, boolean watch) {
		try {
			return zk.exists(path, watch);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void exists(String path, Watcher watcher, StatCallback cb, Object ctx) {
		zk.exists(path, watcher, cb, ctx);
	}

	public void exists(String path, boolean watch, StatCallback cb, Object ctx) {
		zk.exists(path, watch, cb, ctx);
	}

	public List<ACL> getACL(String path, Stat stat) {
		try {
			return zk.getACL(path, stat);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void getACL(String path, Stat stat, ACLCallback cb, Object ctx) {
		zk.getACL(path, stat, cb, ctx);
	}

	public List<String> getChildren(String path, Watcher watcher) {
		try {
			return zk.getChildren(path, watcher);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public List<String> getChildren(String path, boolean watch) {
		try {
			return zk.getChildren(path, watch);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void getChildren(String path, Watcher watcher, ChildrenCallback cb, Object ctx) {
		zk.getChildren(path, watcher, cb, ctx);
	}

	public void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx) {
		zk.getChildren(path, watch, cb, ctx);
	}

	public List<String> getChildren(String path, Watcher watcher, Stat stat) {
		try {
			return zk.getChildren(path, watcher, stat);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public List<String> getChildren(String path, boolean watch, Stat stat) {
		try {
			return zk.getChildren(path, watch, stat);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void getChildren(String path, Watcher watcher, Children2Callback cb, Object ctx) {
		zk.getChildren(path, watcher, cb, ctx);
	}

	public void getChildren(String path, boolean watch, Children2Callback cb, Object ctx) {
		zk.getChildren(path, watch, cb, ctx);
	}

	public byte[] getData(String path, Watcher watcher, Stat stat) {
		try {
			return zk.getData(path, watcher, stat);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public byte[] getData(String path, boolean watch, Stat stat) {
		try {
			return zk.getData(path, watch, stat);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void getData(String path, Watcher watcher, DataCallback cb, Object ctx) {
		zk.getData(path, watcher, cb, ctx);
	}

	public void getData(String path, boolean watch, DataCallback cb, Object ctx) {
		zk.getData(path, watch, cb, ctx);
	}

	public void register(Watcher watcher) {
		zk.register(watcher);
	}

	public Stat setACL(String path, List<ACL> acl, int version) {
		try {
			return zk.setACL(path, acl, version);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void setACL(String path, List<ACL> acl, int version, StatCallback cb, Object ctx) {
		zk.setACL(path, acl, version, cb, ctx);
	}

	public Stat setData(String path, byte[] data, int version) {
		try {
			return zk.setData(path, data, version);
		} catch (KeeperException e) {
			throw new ZooKeeperException("", e);
		} catch (InterruptedException e) {
			throw new ZooKeeperException("", e);
		}
	}

	public void setData(String path, byte[] data, int version, StatCallback cb, Object ctx) {
		zk.setData(path, data, version, cb, ctx);
	}

	public void sync(String path, VoidCallback cb, Object ctx) {
		zk.sync(path, cb, ctx);
	}

	public States getState() {
		return zk.getState();
	}

	public long getSessionId() {
		return zk.getSessionId();
	}

	public byte[] getSessionPasswd() {
		return zk.getSessionPasswd();
	}

}
