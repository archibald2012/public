/**
 * 
 */
package edu.hziee.common.jmx;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXUtil {
	public static Object invokeOverRmi(String host, int port, String objectName, String methodName, Object[] args,
			String[] signature) throws Exception {
		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;

		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");

			jmxc = JMXConnectorFactory.connect(url, null);

			mbsc = jmxc.getMBeanServerConnection();

			ObjectName on = new ObjectName(objectName);
			return mbsc.invoke(on, methodName, args, signature);
		} finally {
			if (null != jmxc) {
				jmxc.close();
				jmxc = null;
			}
		}

	}

	public static Object invokeOverJmxmp(String host, int port, String objectName, String methodName, Object[] args,
			String[] signature) throws Exception {
		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;

		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:jmxmp://" + host + ":" + port);

			jmxc = JMXConnectorFactory.connect(url, null);

			mbsc = jmxc.getMBeanServerConnection();

			ObjectName on = new ObjectName(objectName);
			return mbsc.invoke(on, methodName, args, signature);
		} finally {
			if (null != jmxc) {
				jmxc.close();
				jmxc = null;
			}
		}

	}

}
