/**
 * 
 */
package edu.hziee.common.jmx;

import javax.management.MBeanServerConnection;

public interface JMXClosure {
	public void execute(MBeanServerConnection mbsc);
}
