/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    LenArrayCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 涓嬪崍02:22:07
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec.array;

import java.lang.reflect.Array;

import org.apache.commons.lang.ArrayUtils;

import edu.hziee.common.serialization.bytebean.codec.AbstractCategoryCodec;
import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecCategory;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: LenArrayCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class LenArrayCodec extends AbstractCategoryCodec implements
		ByteFieldCodec {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#getCategory
	 * ()
	 */
	@Override
	public FieldCodecCategory getCategory() {
		return FieldCodecCategory.ARRAY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#decode
	 * (com.taotaosou.common.serialization.bytebean.context.DecContext)
	 */
	@Override
	public DecResult decode(DecContext ctx) {
		// 榛樿4涓瓧鑺傚瓨鍌ㄦ暟缁勯暱搴�
		DecResult ret = ctx.getCodecOf(int.class).decode(
				ctx.getDecContextFactory().createDecContext(ctx.getDecBytes(),
						int.class, ctx.getDecOwner(), null));
		int arrayLength = (Integer) ret.getValue();
		byte[] bytes = ret.getRemainBytes();

		Object array = null;
		if (arrayLength > 0) {
			Class<?> fieldClass = ctx.getDecClass();
			Class<?> compomentClass = fieldClass.getComponentType();

			array = Array.newInstance(compomentClass, arrayLength);
			ByteFieldCodec anyCodec = ctx.getCodecOf(FieldCodecCategory.ANY);

			for (int idx = 0; idx < arrayLength; idx++) {
				ret = anyCodec.decode(ctx.getDecContextFactory()
						.createDecContext(bytes, compomentClass,
								ctx.getDecOwner(), null));
				Array.set(array, idx, ret.getValue());
				bytes = ret.getRemainBytes();
			}
		}

		return new DecResult(array, bytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#encode
	 * (com.taotaosou.common.serialization.bytebean.context.EncContext)
	 */
	@Override
	public byte[] encode(EncContext ctx) {
		Object array = ctx.getEncObject();
		int arrayLength = (null != array ? Array.getLength(array) : 0);

		// 榛樿鍐�瓧鑺傚瓨鍌ㄦ暟缁勯暱搴�
		byte[] bytes = ctx.getCodecOf(int.class).encode(
				ctx.getEncContextFactory().createEncContext(arrayLength,
						int.class, null));

		if (arrayLength > 0) {
			Class<?> fieldClass = ctx.getEncClass();
			Class<?> compomentClass = fieldClass.getComponentType();

			ByteFieldCodec anyCodec = ctx.getCodecOf(FieldCodecCategory.ANY);

			for (int idx = 0; idx < arrayLength; idx++) {
				bytes = ArrayUtils.addAll(bytes, anyCodec.encode(ctx
						.getEncContextFactory().createEncContext(
								Array.get(array, idx), compomentClass, null)));
			}
		}
		return bytes;
	}

}
