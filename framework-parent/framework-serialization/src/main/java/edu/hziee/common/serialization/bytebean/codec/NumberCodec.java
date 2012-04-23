/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    NumberCodec.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:02:59
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: NumberCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public interface NumberCodec {
	String convertCharset(String charset);
	byte[] short2Bytes(short value, int byteLength);
	byte[] int2Bytes(int value, int byteLength);
	byte[] long2Bytes(long value, int byteLength);
	byte[] float2Bytes(float value, int byteLength);
	byte[] double2Bytes(double value, int byteLength);
	short bytes2Short(byte[] bytes, int byteLength);
	int bytes2Int(byte[] bytes, int byteLength);
	long bytes2Long(byte[] bytes, int byteLength);
	float bytes2Float(byte[] bytes, int byteLength);
	double bytes2Double(byte[] bytes, int byteLength);
}
