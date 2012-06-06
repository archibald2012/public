package edu.hziee.common.tcp.bto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: NestedBean.java 14 2012-01-10 11:54:14Z archie $
 */
public class NestedBean {
	@ByteField(index = 0)
	private int intField;

	@ByteField(index = 1)
	private short shortField;

	@ByteField(index = 2)
	private boolean booleanField;

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

	public short getShortField() {
		return shortField;
	}

	public void setShortField(short shortField) {
		this.shortField = shortField;
	}

	public boolean isBooleanField() {
		return booleanField;
	}

	public void setBooleanField(boolean booleanField) {
		this.booleanField = booleanField;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}
}
