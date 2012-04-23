/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DecResult.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午07:58:30
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.context;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: DecResult.java 14 2012-01-10 11:54:14Z archie $
 */
public class DecResult {
	private Object value;
	private byte[] bytes;

	public DecResult(Object value, byte[] bytes) {
		this.value = value;
		this.bytes = bytes;
	}

	public Object getValue() {
		return value;
	}

	public byte[] getRemainBytes() {
		return bytes;
	}
}
