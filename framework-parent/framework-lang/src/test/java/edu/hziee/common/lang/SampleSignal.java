/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SampleBean.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午03:46:48
 *******************************************************************************/
package edu.hziee.common.lang;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SampleSignal.java 14 2012-01-10 11:54:14Z archie $
 */
public class SampleSignal {

	private int intField;

	private byte byteField;

	private String stringField = "";

	private byte[] byteArrayField = new byte[0];

	private short shortField;

	private long longField;

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

	public long getLongField() {
		return longField;
	}

	public void setLongField(long longField) {
		this.longField = longField;
	}

	public byte getByteField() {
		return byteField;
	}

	public void setByteField(byte byteField) {
		this.byteField = byteField;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public byte[] getByteArrayField() {
		return byteArrayField;
	}

	public void setByteArrayField(byte[] byteArrayField) {
		this.byteArrayField = byteArrayField;
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
