/**
 * 
 */
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

/**
 * @author Administrator
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HeapUsage")
public class HeapUsage  implements Serializable{

	private static final long	serialVersionUID	= 1L;
	
	@XmlTransient
	private String	usageId;

	@XmlAttribute(name = "name", required = true)
	private String	name;

	@XmlAttribute(name = "max", required = true)
	private long		max;

	@XmlAttribute(name = "used", required = true)
	private long		used;

	public HeapUsage() {

	}

	public HeapUsage(String name, long max, long used) {
		this.name = MetricsUtil.truncate(name, 64);
		this.max = max;
		this.used = used;
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

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
