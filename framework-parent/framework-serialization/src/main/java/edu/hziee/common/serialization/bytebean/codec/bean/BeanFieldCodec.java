package edu.hziee.common.serialization.bytebean.codec.bean;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.context.DecContextFactory;
import edu.hziee.common.serialization.bytebean.context.EncContextFactory;

/**
 * @author Archibald.Wang
 * @version $Id: BeanFieldCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public interface BeanFieldCodec extends ByteFieldCodec {

	int getStaticByteSize(Class<?> clazz);

	DecContextFactory getDecContextFactory();

	EncContextFactory getEncContextFactory();
}
