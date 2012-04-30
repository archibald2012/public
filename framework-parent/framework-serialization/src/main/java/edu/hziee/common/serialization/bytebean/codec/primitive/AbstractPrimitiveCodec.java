
package edu.hziee.common.serialization.bytebean.codec.primitive;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecCategory;


/**
 * 
 * @author wangqi
 * @version $Id: AbstractPrimitiveCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public abstract class AbstractPrimitiveCodec implements ByteFieldCodec {

	
	@Override
	public FieldCodecCategory getCategory() {
		return null;
	}

}
