/**
 * 
 */
package edu.hziee.common.lock.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lock.LockResult;
import edu.hziee.common.lock.LockStatus;
import edu.hziee.common.lock.LockUpdater;
import edu.hziee.common.zookeeper.ZooKeeperAdapter;
import edu.hziee.common.zookeeper.ZooKeeperException;
import edu.hziee.common.zookeeper.ZooKeeperOperations;

/**
 * @author Administrator
 * 
 */
public class ZooKeeperLockUpdater implements LockUpdater {

	private static final Logger	logger			= LoggerFactory.getLogger(ZooKeeperLockUpdater.class);

	private final String				LOCK_PREFIX	= "lock-";
	private String							connectString;
	private int									sessionTimeout;
	private String							root;

	private ZooKeeperOperations	zkOperations;
	private KeeperState					lastState		= null;

	public void start() throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {

			public void process(WatchedEvent event) {
				if (event.getType().equals(EventType.None)) {
					if (lastState == null || !lastState.equals(event.getState())) {
						lastState = event.getState();
						fireZooKeeperState();
					}
				}
			}
		});

		zkOperations = new ZooKeeperAdapter().setZk(zk);

		if (zkOperations.isConnected()) {
			// create root if needed
			if (zkOperations.exists(root, false) == null) {
				if (logger.isInfoEnabled()) {
					logger.info("Root [" + root + "] does not exist, creating");
				}

				try {
					zkOperations.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				} catch (ZooKeeperException e) {
					// other client create this path first
					if (logger.isWarnEnabled()) {
						logger.warn("Root [" + root + "] exists, just skip.");
					}
				}
			}
		}

	}

	private void fireZooKeeperState() {
		// eventBus.fireEvent(eventZooKeeperStateChanged, zk, lastState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.lock.LockUpdater#acquireLock(java.lang.String, java.lang.String)
	 */
	@Override
	public LockResult acquireLock(String resName, String lockInstance) {

		LockResult result = new LockResult();
		result.setLockedId(resName);

		if (!zkOperations.isConnected()) {
			result.setException(new RuntimeException("ZooKeeper not connected."));
			return result;
		}

		String lockPath = root + "/" + resName;

		String lockZNode = null;
		try {
			lockZNode = createLockZNode(lockPath);
			if (logger.isTraceEnabled()) {
				logger.trace("lockZNode created, [" + lockZNode + "]");
			}

			checkLockStatus(lockPath, lockZNode, result);
		} catch (Throwable e) {
			result.setException(e);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.lock.LockUpdater#releaseLock(java.lang.String, java.lang.String)
	 */
	@Override
	public LockResult releaseLock(String resName, String lockInstance) {
		if (logger.isTraceEnabled()) {
			logger.trace("deleting [" + resName + "]");
		}

		LockResult result = new LockResult();
		result.setLockedId(resName);
		try {
			zkOperations.delete(resName, -1);
		} catch (Exception e) {
			// We do not do anything here. The idea is to check that everything goes OK when
			// locking and let unlock always succeed from client's point of view. Ephemeral
			// nodes should be taken care of by ZooKeeper, so ignoring any errors here should
			// not break anything.
			result.setException(e);
		}
		return result;
	}

	private String createLockZNode(String lockPath) throws KeeperException, InterruptedException {
		String lockZNode = null;

		try {
			lockZNode = zkOperations.create(lockPath + "/" + LOCK_PREFIX, new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (ZooKeeperException e) {
			if (logger.isInfoEnabled()) {
				logger.info("lockPath [" + lockPath + "] does not exist, creating");
			}

			try {
				zkOperations.create(lockPath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (ZooKeeperException ex) {
				// other client create this path first
				if (logger.isWarnEnabled()) {
					logger.warn("lockPath [" + lockPath + "] exists, just skip.");
				}
			}

			lockZNode = zkOperations.create(lockPath + "/" + LOCK_PREFIX, new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
		}

		return lockZNode;
	}

	private void checkLockStatus(final String lockPath, final String lockZNode, final LockResult result)
			throws KeeperException, InterruptedException {

		while (true) {
			// check what is our ID (sequence number at the end of file name
			// added by ZK)
			int mySeqNum = Integer.parseInt(lockZNode.substring(lockZNode.lastIndexOf('-') + 1));
			int previousSeqNum = -1;
			String predessor = null;

			// get all children of lock znode and find the one that is just
			// before us, if
			// any. This must be inside loop, as children might get deleted
			// out of order because
			// of client disconnects. We cannot assume that the file that is
			// in front of us this
			// time, is there next time. It might have been deleted even
			// though earlier files
			// are still there.
			List<String> children = zkOperations.getChildren(lockPath, false);
			if (children.isEmpty()) {
				if (logger.isErrorEnabled()) {
					logger.error("No children in [" + lockPath + "] although one was just created. just failed lock progress.");
				}
				result.setException(new RuntimeException("previous created [" + lockZNode + "] not exists."));
				return;
			}

			for (String child : children) {
				if (logger.isTraceEnabled()) {
					logger.trace("child: " + child);
				}

				int otherSeqNum = Integer.parseInt(child.substring(child.lastIndexOf('-') + 1));
				if ((otherSeqNum < mySeqNum) && (otherSeqNum > previousSeqNum)) {
					previousSeqNum = otherSeqNum;
					predessor = child;
				}
			}

			// our sequence number is smallest, we have the lock
			if (-1 == previousSeqNum) {
				if (logger.isTraceEnabled()) {
					logger.trace("No smaller znode sequences, " + lockZNode + " acquired lock");
				}
				result.setStatus(LockStatus.LOCK_ACQUIRED);
				return;
			}

			Watcher watcher = new Watcher() {

				public void process(WatchedEvent event) {
					try {
						checkLockStatus(lockPath, lockZNode, result);
					} catch (KeeperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};

			if (zkOperations.exists(lockPath + "/" + predessor, watcher) == null) {
				if (logger.isTraceEnabled()) {
					logger.trace(predessor + " does not exists, " + lockZNode + " acquired lock");
				}

				result.setStatus(LockStatus.LOCK_ACQUIRED);
				return;
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace(predessor + " is still here, " + lockZNode + " must blocked for wait");
				}
				result.setStatus(LockStatus.LOCK_BLOCKED);
				return;
			}
		}

	}

	/**
	 * @return the connectString
	 */
	public String getConnectString() {
		return connectString;
	}

	/**
	 * @param connectString
	 *          the connectString to set
	 */
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	/**
	 * @return the sessionTimeout
	 */
	public int getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param sessionTimeout
	 *          the sessionTimeout to set
	 */
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}
