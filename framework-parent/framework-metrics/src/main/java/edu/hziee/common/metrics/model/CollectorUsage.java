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
@XmlType(name = "CollectorUsage")
public class CollectorUsage  implements Serializable{

	private static final long	serialVersionUID	= 1L;
	
	@XmlTransient
	private String	usageId;

	@XmlAttribute(name = "name", required = true)
	private String	name;

	@XmlAttribute(name = "count", required = true)
	private long		count;

	@XmlAttribute(name = "time", required = true)
	private long		time;

	public CollectorUsage() {

	}

	public CollectorUsage(String name, long count, long time) {
		this.name = MetricsUtil.truncate(name, 64);
		this.count = count;
		this.time = time;
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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
