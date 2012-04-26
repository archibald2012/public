
package edu.hziee.common.serialization.bytebean.field;

import java.lang.reflect.Field;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;

/**
 * 读取给定字段的编解码描述的默认实玄1�7
 * 
 * @author wangqi
 * @version $Id: DefaultField2Desc.java 14 2012-01-10 11:54:14Z archie $
 */
public class DefaultField2Desc implements Field2Desc {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.Field2Desc#genDesc(
	 * java.lang.reflect.Field)
	 */
	@Override
	public ByteFieldDesc genDesc(Field field) {
		ByteField byteField = field.getAnnotation(ByteField.class);
		Class<?> clazz = field.getDeclaringClass();
		if (null != byteField) {
			try {
				DefaultFieldDesc desc = new DefaultFieldDesc()
						.setField(field)
						.setIndex(byteField.index())
						.setByteSize(byteField.bytes())
						.setCharset(byteField.charset())
						.setLengthField(
								byteField.length().equals("") ? null : clazz
										.getDeclaredField(byteField.length()))
						.setFixedLength(byteField.fixedLength());
				return desc;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
