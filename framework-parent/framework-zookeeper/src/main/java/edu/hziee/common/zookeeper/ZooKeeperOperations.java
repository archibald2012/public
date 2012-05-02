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
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public interface ZooKeeperOperations {

	/**
	 * Specify the default watcher for the connection (overrides the one specified during construction).
	 * 
	 * @param watcher
	 */
	void register(Watcher watcher);

	/**
	 * Create a node with the given path. The node data will be the given data, and node acl will be the given acl.
	 * <p>
	 * The flags argument specifies whether the created node will be ephemeral or not.
	 * <p>
	 * An ephemeral node will be removed by the ZooKeeper automatically when the session associated with the creation of
	 * the node expires.
	 * <p>
	 * The flags argument can also specify to create a sequential node. The actual path name of a sequential node will be
	 * the given path plus a suffix "i" where i is the current sequential number of the node. The sequence number is
	 * always fixed length of 10 digits, 0 padded. Once such a node is created, the sequential number will be incremented
	 * by one.
	 * <p>
	 * If a node with the same actual path already exists in the ZooKeeper, a KeeperException with error code
	 * KeeperException.NodeExists will be thrown. Note that since a different actual path is used for each invocation of
	 * creating sequential node with the same path argument, the call will never throw "file exists" KeeperException.
	 * <p>
	 * If the parent node does not exist in the ZooKeeper, a KeeperException with error code KeeperException.NoNode will
	 * be thrown.
	 * <p>
	 * An ephemeral node cannot have children. If the parent node of the given path is ephemeral, a KeeperException with
	 * error code KeeperException.NoChildrenForEphemerals will be thrown.
	 * <p>
	 * This operation, if successful, will trigger all the watches left on the node of the given path by exists and
	 * getData API calls, and the watches left on the parent node by getChildren API calls.
	 * <p>
	 * If a node is created successfully, the ZooKeeper server will trigger the watches on the path left by exists calls,
	 * and the watches on the parent of the node by getChildren calls.
	 * <p>
	 * The maximum allowable size of the data array is 1 MB (1,048,576 bytes). Arrays larger than this will cause a
	 * KeeperExecption to be thrown.
	 * 
	 * @param path
	 *          the path for the node
	 * @param data
	 *          the initial data for the node
	 * @param acl
	 *          the acl for the node
	 * @param createMode
	 *          specifying whether the node to be created is ephemeral and/or sequential
	 * @return the actual path of the created node
	 */
	public String create(final String path, byte data[], List<ACL> acl, CreateMode createMode);

	/**
	 * The Asynchronous version of create. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #create(String, byte[], List, CreateMode)
	 */

	public void create(final String path, byte data[], List<ACL> acl, CreateMode createMode, StringCallback cb, Object ctx);

	/**
	 * Delete the node with the given path. The call will succeed if such a node exists, and the given version matches the
	 * node's version (if the given version is -1, it matches any node's versions).
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if the nodes does not exist.
	 * <p>
	 * A KeeperException with error code KeeperException.BadVersion will be thrown if the given version does not match the
	 * node's version.
	 * <p>
	 * A KeeperException with error code KeeperException.NotEmpty will be thrown if the node has children.
	 * <p>
	 * This operation, if successful, will trigger all the watches on the node of the given path left by exists API calls,
	 * and the watches on the parent node left by getChildren API calls.
	 * 
	 * @param path
	 *          the path of the node to be deleted.
	 * @param version
	 *          the expected node version.
	 */
	public void delete(final String path, int version);

	/**
	 * The Asynchronous version of delete. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #delete(String, int)
	 */
	public void delete(final String path, int version, VoidCallback cb, Object ctx);

	/**
	 * Return the stat of the node of the given path. Return null if no such a node exists.
	 * <p>
	 * If the watch is non-null and the call is successful (no exception is thrown), a watch will be left on the node with
	 * the given path. The watch will be triggered by a successful operation that creates/delete the node or sets the data
	 * on the node.
	 * 
	 * @param path
	 *          the node path
	 * @param watcher
	 *          explicit watcher
	 * @return the stat of the node of the given path; return null if no such a node exists.
	 */
	public Stat exists(final String path, Watcher watcher);

	/**
	 * Return the stat of the node of the given path. Return null if no such a node exists.
	 * <p>
	 * If the watch is true and the call is successful (no exception is thrown), a watch will be left on the node with the
	 * given path. The watch will be triggered by a successful operation that creates/delete the node or sets the data on
	 * the node.
	 * 
	 * @param path
	 *          the node path
	 * @param watch
	 *          whether need to watch this node
	 * @return the stat of the node of the given path; return null if no such a node exists.
	 */
	public Stat exists(String path, boolean watch);

	/**
	 * The Asynchronous version of exists. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #exists(String, boolean)
	 */
	public void exists(final String path, Watcher watcher, StatCallback cb, Object ctx);

	/**
	 * The Asynchronous version of exists. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #exists(String, boolean)
	 */
	public void exists(String path, boolean watch, StatCallback cb, Object ctx);

	/**
	 * Return the data and the stat of the node of the given path.
	 * <p>
	 * If the watch is non-null and the call is successful (no exception is thrown), a watch will be left on the node with
	 * the given path. The watch will be triggered by a successful operation that sets data on the node, or deletes the
	 * node.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @param path
	 *          the given path
	 * @param watcher
	 *          explicit watcher
	 * @param stat
	 *          the stat of the node
	 * @return the data of the node
	 */
	public byte[] getData(final String path, Watcher watcher, Stat stat);

	/**
	 * Return the data and the stat of the node of the given path.
	 * <p>
	 * If the watch is true and the call is successful (no exception is thrown), a watch will be left on the node with the
	 * given path. The watch will be triggered by a successful operation that sets data on the node, or deletes the node.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @param path
	 *          the given path
	 * @param watch
	 *          whether need to watch this node
	 * @param stat
	 *          the stat of the node
	 * @return the data of the node
	 */
	public byte[] getData(String path, boolean watch, Stat stat);

	/**
	 * The Asynchronous version of getData. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #getData(String, Watcher, Stat)
	 */
	public void getData(final String path, Watcher watcher, DataCallback cb, Object ctx);

	/**
	 * The Asynchronous version of getData. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #getData(String, boolean, Stat)
	 */
	public void getData(String path, boolean watch, DataCallback cb, Object ctx);

	/**
	 * Set the data for the node of the given path if such a node exists and the given version matches the version of the
	 * node (if the given version is -1, it matches any node's versions). Return the stat of the node.
	 * <p>
	 * This operation, if successful, will trigger all the watches on the node of the given path left by getData calls.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * <p>
	 * A KeeperException with error code KeeperException.BadVersion will be thrown if the given version does not match the
	 * node's version.
	 * <p>
	 * The maximum allowable size of the data array is 1 MB (1,048,576 bytes). Arrays larger than this will cause a
	 * KeeperExecption to be thrown.
	 * 
	 * @param path
	 *          the path of the node
	 * @param data
	 *          the data to set
	 * @param version
	 *          the expected matching version
	 * @return the state of the node
	 */
	public Stat setData(final String path, byte data[], int version);

	/**
	 * The Asynchronous version of setData. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #setData(String, byte[], int)
	 */
	public void setData(final String path, byte data[], int version, StatCallback cb, Object ctx);

	/**
	 * Return the ACL and stat of the node of the given path.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @param path
	 *          the given path for the node
	 * @param stat
	 *          the stat of the node will be copied to this parameter.
	 * @return the ACL array of the given node.
	 */
	public List<ACL> getACL(final String path, Stat stat);

	/**
	 * The Asynchronous version of getACL. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #getACL(String, Stat)
	 */
	public void getACL(final String path, Stat stat, ACLCallback cb, Object ctx);

	/**
	 * Set the ACL for the node of the given path if such a node exists and the given version matches the version of the
	 * node. Return the stat of the node.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * <p>
	 * A KeeperException with error code KeeperException.BadVersion will be thrown if the given version does not match the
	 * node's version.
	 * 
	 * @param path
	 * @param acl
	 * @param version
	 * @return the stat of the node.
	 */
	public Stat setACL(final String path, List<ACL> acl, int version) ;

	/**
	 * The Asynchronous version of setACL. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #setACL(String, List, int)
	 */
	public void setACL(final String path, List<ACL> acl, int version, StatCallback cb, Object ctx);

	/**
	 * Return the list of the children of the node of the given path.
	 * <p>
	 * If the watch is non-null and the call is successful (no exception is thrown), a watch will be left on the node with
	 * the given path. The watch willbe triggered by a successful operation that deletes the node of the given path or
	 * creates/delete a child under the node.
	 * <p>
	 * The list of children returned is not sorted and no guarantee is provided as to its natural or lexical order.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @param path
	 * @param watcher
	 *          explicit watcher
	 * @return an unordered array of children of the node with the given path
	 */
	public List<String> getChildren(final String path, Watcher watcher) ;

	/**
	 * Return the list of the children of the node of the given path.
	 * <p>
	 * If the watch is true and the call is successful (no exception is thrown), a watch will be left on the node with the
	 * given path. The watch willbe triggered by a successful operation that deletes the node of the given path or
	 * creates/delete a child under the node.
	 * <p>
	 * The list of children returned is not sorted and no guarantee is provided as to its natural or lexical order.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @param path
	 * @param watch
	 * @return an unordered array of children of the node with the given path
	 */
	public List<String> getChildren(String path, boolean watch) ;

	/**
	 * The Asynchronous version of getChildren. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #getChildren(String, Watcher)
	 */
	public void getChildren(final String path, Watcher watcher, ChildrenCallback cb, Object ctx);

	/**
	 * The Asynchronous version of getChildren. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @see #getChildren(String, boolean)
	 */
	public void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx);

	/**
	 * For the given znode path return the stat and children list.
	 * <p>
	 * If the watch is non-null and the call is successful (no exception is thrown), a watch will be left on the node with
	 * the given path. The watch willbe triggered by a successful operation that deletes the node of the given path or
	 * creates/delete a child under the node.
	 * <p>
	 * The list of children returned is not sorted and no guarantee is provided as to its natural or lexical order.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @since 3.3.0
	 * 
	 * @param path
	 * @param watcher
	 *          explicit watcher
	 * @param stat
	 *          stat of the znode designated by path
	 * @return an unordered array of children of the node with the given path
	 */
	public List<String> getChildren(final String path, Watcher watcher, Stat stat) ;

	/**
	 * For the given znode path return the stat and children list.
	 * <p>
	 * If the watch is true and the call is successful (no exception is thrown), a watch will be left on the node with the
	 * given path. The watch willbe triggered by a successful operation that deletes the node of the given path or
	 * creates/delete a child under the node.
	 * <p>
	 * The list of children returned is not sorted and no guarantee is provided as to its natural or lexical order.
	 * <p>
	 * A KeeperException with error code KeeperException.NoNode will be thrown if no node with the given path exists.
	 * 
	 * @since 3.3.0
	 * 
	 * @param path
	 * @param watch
	 * @param stat
	 *          stat of the znode designated by path
	 * @return an unordered array of children of the node with the given path
	 */
	public List<String> getChildren(String path, boolean watch, Stat stat) ;

	/**
	 * The Asynchronous version of getChildren. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @since 3.3.0
	 * 
	 * @see #getChildren(String, Watcher, Stat)
	 */
	public void getChildren(final String path, Watcher watcher, Children2Callback cb, Object ctx);

	/**
	 * The Asynchronous version of getChildren. The request doesn't actually until the asynchronous callback is called.
	 * 
	 * @since 3.3.0
	 * 
	 * @see #getChildren(String, boolean, Stat)
	 */
	public void getChildren(String path, boolean watch, Children2Callback cb, Object ctx);

	/**
	 * Asynchronous sync. Flushes channel between process and leader.
	 * 
	 * @param path
	 * @param cb
	 *          a handler for the callback
	 * @param ctx
	 *          context to be provided to the callback
	 * @throws IllegalArgumentException
	 *           if an invalid path is specified
	 */
	public void sync(final String path, VoidCallback cb, Object ctx);

	public States getState();

	boolean isConnected();

	long getSessionId();

	byte[] getSessionPasswd();
}
