
package edu.hziee.common.serialization.bytebean.context;

import java.lang.reflect.Field;

import edu.hziee.common.serialization.bytebean.codec.FieldCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * 字段编解码上下文
 * 
 * @author Archibald.Wang
 * @version $Id: FieldCodecContext.java 14 2012-01-10 11:54:14Z archie $
 */
public interface FieldCodecContext extends FieldCodecProvider{
	ByteFieldDesc getFieldDesc();
	Field getField();
	NumberCodec getNumberCodec();
	int getByteSize();
}
