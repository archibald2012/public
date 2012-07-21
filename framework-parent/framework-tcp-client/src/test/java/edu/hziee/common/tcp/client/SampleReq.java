package edu.hziee.common.tcp.client;

import java.util.ArrayList;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipRequest;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SampleReq.java 14 2012-01-10 11:54:14Z archie $
 */
@SignalCode(messageCode = 100001)
public class SampleReq extends AbstractXipSignal implements XipRequest {

	@ByteField(index = 0)
	private int intField;

	@ByteField(index = 1)
	private byte byteField;

	@ByteField(index = 2)
	private String stringField = "";

	@ByteField(index = 3)
	private byte[] byteArrayField = new byte[0];

	@ByteField(index = 4)
	private short shortField;

	@ByteField(index = 5)
	// list字段不能为null，否则会报错
	private ArrayList<NestedBean> listField = new ArrayList<NestedBean>();

	@ByteField(index = 6)
	// 对象类型字段不能为null，否则会报错
	private NestedBean beanField = new NestedBean();

	@ByteField(index = 7)
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

	public ArrayList<NestedBean> getListField() {
		return listField;
	}

	public void setListField(ArrayList<NestedBean> listField) {
		this.listField = listField;
	}

	public NestedBean getBeanField() {
		return beanField;
	}

	public void setBeanField(NestedBean beanField) {
		this.beanField = beanField;
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
