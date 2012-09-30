/**
 * 
 */
package edu.hziee.common.metrics.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bucket")
public class Bucket  implements Serializable{

	private static final long	serialVersionUID	= 1L;
	
	@XmlAttribute(name = "start", required = true)
	private long	startRange;

	@XmlAttribute(name = "count", required = true)
	private long	count;

	@XmlAttribute(name = "unit_count", required = true)
	private long	unitCount;

	public long getStartRange() {
		return startRange;
	}

	public void setStartRange(long startRange) {
		this.startRange = startRange;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(long unitCount) {
		this.unitCount = unitCount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
