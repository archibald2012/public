
package edu.hziee.common.serialization.bytebean.codec;

import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * 
 * @author Archibald.Wang
 * @version $Id: ByteFieldCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public interface ByteFieldCodec {

	FieldCodecCategory getCategory();

	Class<?>[] getFieldType();

	DecResult decode(DecContext ctx);

	byte[] encode(EncContext ctx);
}
