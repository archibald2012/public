/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Field2Desc.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:39:07
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.field;

import java.lang.reflect.Field;

/**
 * 读取给定字段的编解码描述
 * 
 * @author Archibald.Wang
 * @version $Id: Field2Desc.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Field2Desc {
	ByteFieldDesc genDesc(Field field);
}
