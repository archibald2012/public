
package edu.hziee.common.serialization.bytebean.codec.bean;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.context.DecContextFactory;
import edu.hziee.common.serialization.bytebean.context.EncContextFactory;

/**
 * 对象的编码解码器
 * 
 * @author Archibald.Wang
 * @version $Id: BeanFieldCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public interface BeanFieldCodec extends ByteFieldCodec {

	/**
	 * 编解码对象对应的字节长度
	 * 
	 * @param clazz
	 * @return
	 */
	int getStaticByteSize(Class<?> clazz);

	/**
	 * 解码上下文工厄1�7
	 * 
	 * @return
	 */
	DecContextFactory getDecContextFactory();

	/**
	 * 编码上下文工厄1�7
	 * 
	 * @return
	 */
	EncContextFactory getEncContextFactory();
}
