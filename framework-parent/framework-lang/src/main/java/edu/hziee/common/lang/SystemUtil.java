/**
 * 
 */
package edu.hziee.common.lang;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class SystemUtil {

	private static final Logger	logger	= LoggerFactory.getLogger(SystemUtil.class);

	private SystemUtil() {
	}

	private static String					ipAddress;
	private static ReentrantLock	ipAddressLock	= new ReentrantLock();

	public static String getIpAddress() {
		if (ipAddress == null) {
			ipAddressLock.lock();
			try {
				if (ipAddress == null) {
					try {
						// TODO set default ip address
						InetAddress address = InetAddress.getLocalHost();
						ipAddress = address.getHostAddress();
					} catch (UnknownHostException e) {
						logger.warn("Unable to get hostAddress with error " + e.getMessage(), e);
					}
				}
			} finally {
				ipAddressLock.unlock();
			}
		}
		return ipAddress;
	}

	private static String					pid;
	private static ReentrantLock	pidLock	= new ReentrantLock();

	public static String getPid() {
		if (pid == null) {
			pidLock.lock();
			try {
				if (pid == null) {
					try {
						// TODO set default pid
						RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
						if (runtime != null) {
							String name = runtime.getName();
							int index = name.indexOf('@');
							if (index > 0) {
								pid = name.substring(0, index);
							}
						}
					} catch (Exception e) {
						logger.warn("Unable to get runtimeName with error " + e.getMessage(), e);
					}
				}
			} finally {
				pidLock.unlock();
			}
		}
		return pid;
	}

	private static String					hostName;
	private static ReentrantLock	hostNameLock	= new ReentrantLock();

	public static String getHostName() {
		if (hostName == null) {
			hostNameLock.lock();
			try {
				if (hostName == null) {
					try {
						InetAddress address = InetAddress.getLocalHost();
						hostName = address.getHostName();
					} catch (Exception e) {
						logger.warn("Unable to resolve hostname with error " + e.getMessage(), e);
						// TODO set default host name
					}
				}
			} finally {
				hostNameLock.unlock();
			}
		}
		return hostName;
	}

	private static String					userName;
	private static ReentrantLock	userNameLock	= new ReentrantLock();

	public static String getUserName() {
		if (userName == null) {
			userNameLock.lock();
			try {
				if (userName == null) {
					// TODO
				}
			} finally {
				userNameLock.unlock();
			}
		}
		return userName;
	}
}
