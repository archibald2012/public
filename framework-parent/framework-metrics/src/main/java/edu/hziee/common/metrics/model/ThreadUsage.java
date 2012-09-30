package edu.hziee.common.metrics.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.metrics.util.MetricsUtil;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ThreadUsage")
public class ThreadUsage implements Serializable {

	private static final long	serialVersionUID	= 1L;

	@XmlTransient
	private String						usageId;

	@XmlAttribute(name = "name", required = true)
	private String						name;

	@XmlAttribute(name = "state", required = true)
	private String						state;

	@XmlAttribute(name = "cpu", required = true)
	private long							cpuTime;

	@XmlAttribute(name = "user", required = true)
	private long							userTime;

	public ThreadUsage() {

	}

	public ThreadUsage(String name, String state, long cpuTime, long userTime) {
		this.name = MetricsUtil.truncate(name, 64);
		this.state = MetricsUtil.truncate(state, 64);
		this.cpuTime = cpuTime;
		this.userTime = userTime;
	}

	public String getUsageId() {
		return usageId;
	}

	public void setUsageId(String usageId) {
		this.usageId = usageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
