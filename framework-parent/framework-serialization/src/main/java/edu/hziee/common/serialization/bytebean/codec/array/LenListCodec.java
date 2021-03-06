
package edu.hziee.common.serialization.bytebean.codec.array;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecCategory;
import edu.hziee.common.serialization.bytebean.codec.primitive.AbstractPrimitiveCodec;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: LenListCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class LenListCodec extends AbstractPrimitiveCodec implements
		ByteFieldCodec {

	private static final Logger logger = LoggerFactory
			.getLogger(LenListCodec.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#getCategory
	 * ()
	 */
	@Override
	public FieldCodecCategory getCategory() {
		// TODO Auto-generated method stub
		return null;
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

		// 默认4个字节存储List长度
		DecResult ret = ctx.getCodecOf(int.class).decode(
				ctx.getDecContextFactory().createDecContext(ctx.getDecBytes(),
						int.class, ctx.getDecOwner(), null));
		int listLength = (Integer) ret.getValue();
		byte[] bytes = ret.getRemainBytes();
		Class<?> compomentClass = getCompomentClass(ctx.getField());

		ArrayList<Object> list = new ArrayList<Object>(listLength);
		if (listLength > 0) {
			ByteFieldCodec anyCodec = ctx.getCodecOf(FieldCodecCategory.ANY);

			for (int idx = 0; idx < listLength; idx++) {
				ret = anyCodec.decode(ctx.getDecContextFactory()
						.createDecContext(bytes, compomentClass,
								ctx.getDecOwner(), null));
				list.add(ret.getValue());
				bytes = ret.getRemainBytes();
			}
		}

		return new DecResult(list, bytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#encode
	 * (com.taotaosou.common.serialization.bytebean.context.EncContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public byte[] encode(EncContext ctx) {
		ArrayList<Object> list = (ArrayList<Object>) ctx.getEncObject();
		int listLength = (null != list ? list.size() : 0);
		Class<?> compomentClass = getCompomentClass(ctx.getField());

		// 默认4个字节存储List长度
		byte[] bytes = ctx.getCodecOf(int.class).encode(
				ctx.getEncContextFactory().createEncContext(listLength,
						int.class, null));

		if (listLength > 0) {

			ByteFieldCodec anyCodec = ctx.getCodecOf(FieldCodecCategory.ANY);

			for (int idx = 0; idx < listLength; idx++) {
				bytes = ArrayUtils.addAll(bytes, anyCodec.encode(ctx
						.getEncContextFactory().createEncContext(list.get(idx),
								compomentClass, null)));
			}
		}
		return bytes;
	}

	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { ArrayList.class };
	}

	public Class<?> getCompomentClass(Field field) {
		if (null == field) {
			String errmsg = "LenListCodec: field is null, can't get compoment class.";
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}
		Type type = field.getGenericType();

		if (null == type || !(type instanceof ParameterizedType)) {
			String errmsg = "LenListCodec: getGenericType invalid, can't get compoment class."
					+ "/ cause field is [" + field + "]";
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return clazz;
	}

}
