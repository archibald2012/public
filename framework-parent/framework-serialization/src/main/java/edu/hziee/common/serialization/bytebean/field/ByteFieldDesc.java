/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ByteFieldDesc.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:28:03
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.field;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * 字段的编解码描述
 * 
 * @author Archibald.Wang
 * @version $Id: ByteFieldDesc.java 14 2012-01-10 11:54:14Z archie $
 */
public interface ByteFieldDesc {
	int getIndex();
	int getByteSize();
	Field getField();
	Class<?> getFieldType();
	boolean hasLength();
	int getLength(Object owner);
	int getStringLengthInBytes(Object owner);
	String getCharset();
	String getDescription();
	int getFixedLength();

	static final Comparator<ByteFieldDesc> comparator = new Comparator<ByteFieldDesc>() {
		public int compare(ByteFieldDesc desc1, ByteFieldDesc desc2) {
			int ret = desc1.getIndex() - desc2.getIndex();
			if (0 == ret) {
				throw new RuntimeException("field1:" + desc1.getField()
						+ "/field2:" + desc2.getField()
						+ " has the same index value, internal error.");
			}
			return ret;
		}
	};
}
