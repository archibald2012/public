/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ByteField.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 上午11:04:15
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 定义协议字段
 * 
 * @author wangqi
 * @version $Id: ByteField.java 14 2012-01-10 11:54:14Z archie $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ByteField {

	/**
	 * 消息体中的索引位罄1�7
	 * 
	 * @return
	 */
	int index();

	/**
	 * 在消息体中的字节长度，为-1时，取字段类型的长度
	 * 
	 * @return
	 */
	int bytes() default -1;

	/**
	 * 定义字段类型的长度字殄1�7
	 * 
	 * @return
	 */
	String length() default "";

	/**
	 * 定义字段类型的字符集
	 * 
	 * @return
	 */
	String charset() default "UTF-16";

	/**
	 * 定义字段的为定长字节
	 * 
	 * @return
	 */
	int fixedLength() default -1;

	/**
	 * 定义字段描述
	 * 
	 * @return
	 */
	String description() default "";
}
