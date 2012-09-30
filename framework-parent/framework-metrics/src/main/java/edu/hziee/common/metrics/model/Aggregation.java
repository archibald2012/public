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
@XmlType(name = "Aggregation", propOrder = { "bucketList" })
public class Aggregation implements Publishable, Serializable {

	private static final long	serialVersionUID	= 1L;
	
	@XmlAttribute(name = "id", required = true)
	private String				id;

	@XmlTransient
	private String				recordId;

	@XmlAttribute(name = "component", required = true)
	private String				componentName;

	@XmlAttribute(name = "function", required = true)
	private String				functionName;

	@XmlAttribute(name = "start", required = true)
	private Date					startTime;

	@XmlAttribute(name = "duration", required = true)
	private long					duration;

	@XmlAttribute(name = "max", required = true)
	private long					maximum;

	@XmlAttribute(name = "min", required = true)
	private long					minimum;

	@XmlAttribute(name = "avg", required = true)
	private double				average;

	@XmlAttribute(name = "unit_max", required = true)
	private long					unitMaximum;

	@XmlAttribute(name = "unit_min", required = true)
	private long					unitMinimum;

	@XmlAttribute(name = "unit_avg", required = true)
	private double				unitAverage;

	@XmlAttribute(name = "count", required = true)
	private long					count;

	@XmlElement(name = "bucket")
	private List<Bucket>	bucketList;

	@XmlTransient
	private String				ranges;

	public Aggregation() {

	}

	public Aggregation(String componentName, String functionName) {
		this.componentName = componentName;
		this.functionName = functionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.model.Publishable#getRecordId()
	 */
	@Override
	public String getRecordId() {
		return recordId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.model.Publishable#setRecordId(java.lang.String)
	 */
	@Override
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getMaximum() {
		return maximum;
	}

	public void setMaximum(long maximum) {
		this.maximum = maximum;
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public long getUnitMaximum() {
		return unitMaximum;
	}

	public void setUnitMaximum(long unitMaximum) {
		this.unitMaximum = unitMaximum;
	}

	public long getUnitMinimum() {
		return unitMinimum;
	}

	public void setUnitMinimum(long unitMinimum) {
		this.unitMinimum = unitMinimum;
	}

	public double getUnitAverage() {
		return unitAverage;
	}

	public void setUnitAverage(double unitAverage) {
		this.unitAverage = unitAverage;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<Bucket> getBucketList() {
		if (bucketList == null) {
			this.bucketList = new ArrayList<Bucket>();
		}
		return bucketList;
	}

	public void setBucketList(List<Bucket> bucketList) {
		this.bucketList = bucketList;
	}

	public String getRanges() {
		return ranges;
	}

	public void setRanges(String ranges) {
		this.ranges = ranges;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
