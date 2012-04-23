/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    XipHeader.java
 * Creator:     wangqi
 * Create-Date: 2011-4-28 上午09:54:08
 *******************************************************************************/
package edu.hziee.common.serialization.protocol.xip;

import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: XipHeader.java 23 2012-01-10 14:42:11Z archie $
 */

//Xip header
//0               1               2               3                
//0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7  
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|  basic ver(1) |                    length(4)                  |
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//                |                                               |
//|                       transaction(16)                         |
//|                                                               |
//|                                                               |
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//                |   type(1)     |         reserved(2)           |
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|                           code(4)                             |
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|application data field(...)                                    |
//|                                                               |
//|                                                               |
//|                                                               |
//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//字段名称    字段大小    取�1�7�范囄1�7    功能说明
//基础协议版本
//(basic ver)   1(byte) [1,255] 标识基础协议的版本号，不会频繁更新，仄1�7�开始版朄1�7
//协议包长庄1�7
//(length)      4   [32,16777215]   整个协议包长度，朄1�7小�1�7�为协议头大射1�7(32)，高位字节序
//事务标识
//(transaction) 16  GUID      参照GUID的生成算法，由请求或者�1�7�知的发送�1�7�保证其在任何时间�1�7�地点�1�7�平台唯丄1�7，请求和响应消息的事物标识必须一致�1�7�该标识用于保证请求和响应的唯一对应以及消息的不重复怄1�7
//原语类型
//(type)        1   [0,255]     目前支持请求(0)，响庄1�7(1)，�1�7�知＄1�7�，递�1�7�1�7(3) 
//保留字段
//（reserved＄1�7      2    N/A        基础协议头的保留字段
//消息编码
//（code＄1�7                4   [0,2^32]    具体每种消息的编号，建议将对应的请求和响应消息连续编号，高位字节序，按照不同功能进行分组编号〄1�7
//协议数据埄1�7
//(data field)  n   n/a         唯一对应某个消息编码
public class XipHeader {

	public static final int HEADER_LENGTH = 28;
	
	public static final int XIP_REQUEST = 1;
	public static final int XIP_RESPONSE = 2;
	public static final int XIP_NOTIFY = 3;

	@ByteField(index = 0, bytes = 1)
	private int basicVer = 1;

	@ByteField(index = 1)
	private int length = 0;

	@ByteField(index = 2, bytes = 1)
	private int type = 1;

	@ByteField(index = 3, bytes = 2)
	private int reserved = 0;

	@ByteField(index = 4)
	private long firstTransaction;

	@ByteField(index = 5)
	private long secondTransaction;

	@ByteField(index = 6)
	private int messageCode;

	public int getBasicVer() {
		return basicVer;
	}

	public void setBasicVer(int basicVer) {
		this.basicVer = basicVer;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	public long getFirstTransaction() {
		return firstTransaction;
	}

	public void setFirstTransaction(long firstTransaction) {
		this.firstTransaction = firstTransaction;
	}

	public long getSecondTransaction() {
		return secondTransaction;
	}

	public void setSecondTransaction(long secondTransaction) {
		this.secondTransaction = secondTransaction;
	}

	public int getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(int messageCode) {
		if (messageCode <= 0) {
			throw new RuntimeException("invalid message code.");
		}
		this.messageCode = messageCode;
	}

	public void setTransaction(UUID uuid) {
		this.firstTransaction = uuid.getMostSignificantBits();
		this.secondTransaction = uuid.getLeastSignificantBits();
	}

	public UUID getTransactionAsUUID() {
		return new UUID(this.firstTransaction, this.secondTransaction);
	}

	public void setTypeForClass(Class<?> cls) {
		if (XipRequest.class.isAssignableFrom(cls)) {
			this.type = XIP_REQUEST;
		} else if (XipResponse.class.isAssignableFrom(cls)) {
			this.type = XIP_RESPONSE;
		} else if (XipNotify.class.isAssignableFrom(cls)) {
			this.type = XIP_NOTIFY;
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
