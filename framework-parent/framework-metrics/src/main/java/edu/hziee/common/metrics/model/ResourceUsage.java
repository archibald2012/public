/**
 * 
 */
package edu.hziee.common.metrics.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceUsage", propOrder = { "threadList", "heapList", "collectorList" })
public class ResourceUsage implements Publishable, Serializable {

	private static final long			serialVersionUID	= 1L;

	@XmlTransient
	private String								recordId;

	@XmlAttribute(name = "usageId", required = true)
	private String								usageId;

	@XmlAttribute(name = "processors", required = true)
	private int										processorCount;

	@XmlAttribute(name = "threads", required = true)
	private int										threadCount;

	@XmlAttribute(name = "checkTime", required = true)
	private Date									checkTime;

	@XmlAttribute(name = "upTime", required = true)
	private long									upTime;

	@XmlAttribute(name = "cpuTime", required = true)
	private long									cpuTime;

	@XmlAttribute(name = "userTime", required = true)
	private long									userTime;

	@XmlAttribute(name = "heapMax", required = true)
	private long									heapMax;

	@XmlAttribute(name = "heapUsed", required = true)
	private long									heapUsed;

	@XmlAttribute(name = "nonHeapMax", required = true)
	private long									nonHeapMax;

	@XmlAttribute(name = "nonHeapUsed", required = true)
	private long									nonHeapUsed;

	@XmlElement(name = "thread")
	private List<ThreadUsage>			threadList;

	@XmlElement(name = "heap")
	private List<HeapUsage>				heapList;

	@XmlElement(name = "collector")
	private List<CollectorUsage>	collectorList;

	public ResourceUsage() {

	}

	@Override
	public String getRecordId() {
		return recordId;
	}

	@Override
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getUsageId() {
		return usageId;
	}

	public void setUsageId(String usageId) {
		this.usageId = usageId;
	}

	public int getProcessorCount() {
		return processorCount;
	}

	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public long getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

	public long getUserTime() {
		return userTime;
	}

	public void setUserTime(long userTime) {
		this.userTime = userTime;
	}

	public long getHeapMax() {
		return heapMax;
	}

	public void setHeapMax(long heapMax) {
		this.heapMax = heapMax;
	}

	public long getHeapUsed() {
		return heapUsed;
	}

	public void setHeapUsed(long heapUsed) {
		this.heapUsed = heapUsed;
	}

	public long getNonHeapMax() {
		return nonHeapMax;
	}

	public void setNonHeapMax(long nonHeapMax) {
		this.nonHeapMax = nonHeapMax;
	}

	public long getNonHeapUsed() {
		return nonHeapUsed;
	}

	public void setNonHeapUsed(long nonHeapUsed) {
		this.nonHeapUsed = nonHeapUsed;
	}

	public List<ThreadUsage> getThreadList() {
		if (threadList == null) {
			threadList = new ArrayList<ThreadUsage>();
		}
		return threadList;
	}

	public void setThreadList(List<ThreadUsage> threadList) {
		this.threadList = threadList;
	}

	public List<HeapUsage> getHeapList() {
		return heapList;
	}

	public void setHeapList(List<HeapUsage> heapList) {
		if (heapList == null) {
			heapList = new ArrayList<HeapUsage>();
		}
		this.heapList = heapList;
	}

	public List<CollectorUsage> getCollectorList() {
		if (collectorList == null) {
			collectorList = new ArrayList<CollectorUsage>();
		}
		return collectorList;
	}

	public void setCollectorList(List<CollectorUsage> collectorList) {
		this.collectorList = collectorList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
