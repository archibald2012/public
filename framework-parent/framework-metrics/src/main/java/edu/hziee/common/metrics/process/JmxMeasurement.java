/**
 * 
 */
package edu.hziee.common.metrics.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import edu.hziee.common.metrics.model.Measurement;

/**
 * @author Administrator
 * 
 */
public class JmxMeasurement implements JmxMeasurementMBean {

	private ConcurrentMap<String, MBeanData>	dataMap	= new ConcurrentHashMap<String, MBeanData>();

	@Override
	public List<String> queryMeasurementList() {
		List<String> nameList = new ArrayList<String>();

		Set<Entry<String, MBeanData>> entrySet = dataMap.entrySet();
		for (Entry<String, MBeanData> entry : entrySet) {
			StringBuilder builder = new StringBuilder(entry.getKey());
			MBeanData mbeanData = entry.getValue();
			builder.append("[min=").append(mbeanData.getMinimum());
			builder.append(",avg=").append(mbeanData.getAverage());
			builder.append(",max=").append(mbeanData.getMaximum());
			builder.append(",count=").append(mbeanData.getCount());
			builder.append(",fail=").append(mbeanData.getFailCount()).append("]\n");
			nameList.add(builder.toString());
		}
		return nameList;
	}

	@Override
	public void resetAllCounts() {
		dataMap.clear();
	}

	@Override
	public boolean resetCount(String key) {
		return dataMap.remove(key) == null ? false : true;
	}

	public void addMeasurement(List<Measurement> measurements) {
		for (Measurement measurement : measurements) {
			String key = StringUtils
					.join(new Object[] { measurement.getComponentName(), measurement.getFunctionName() }, ":");
			MBeanData mbeanData = dataMap.get(key);
			if (mbeanData == null) {
				mbeanData = new MBeanData(measurement);
				MBeanData oldData = dataMap.putIfAbsent(key, mbeanData);
				if (oldData != null) {
					oldData.addMeasurement(measurement);
				}
			} else {
				mbeanData.addMeasurement(measurement);
			}
		}
	}
}

class MBeanData {
	private long		minimum;
	private long		maximum;
	private double	average;
	private long		count;
	private long		failCount;

	public MBeanData(Measurement measurement) {
		if (measurement.getDuration() != null) {
			maximum = measurement.getDuration();
			minimum = measurement.getDuration();
			average = measurement.getDuration();
			count = 1;
			if (measurement.getFailStatus() != null && measurement.getFailStatus()) {
				failCount = 1;
			}
		}
	}

	public synchronized void addMeasurement(Measurement measurement) {
		if (measurement.getDuration() != null) {
			if (maximum < measurement.getDuration()) {
				maximum = measurement.getDuration();
			}
			if (minimum > measurement.getDuration()) {
				minimum = measurement.getDuration();
			}

			average = ((average * count) + measurement.getDuration()) / (count + 1);
			count++;
			if (measurement.getFailStatus() != null && measurement.getFailStatus()) {
				failCount++;
			}
		}
	}

	public long getMinimum() {
		return minimum;
	}

	public long getMaximum() {
		return maximum;
	}

	public double getAverage() {
		return average;
	}

	public long getCount() {
		return count;
	}

	public long getFailCount() {
		return failCount;
	}

}
