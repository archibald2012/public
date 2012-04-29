package edu.hziee.common.serialization.bytebean.context;

import java.lang.reflect.Field;

import edu.hziee.common.serialization.bytebean.ByteBeanUtil;
import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecCategory;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AbstractCodecContext.java 14 2012-01-10 11:54:14Z archie $
 */
public class AbstractCodecContext extends ByteBeanUtil implements FieldCodecContext {

	protected FieldCodecProvider	codecProvider	= null;

	protected ByteFieldDesc				fieldDesc;
	protected NumberCodec					numberCodec;
	protected Class<?>						targetType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taotaosou.common.serialization.bytebean.context.FieldCodecContext #getFieldDesc()
	 */
	@Override
	public ByteFieldDesc getFieldDesc() {
		return fieldDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taotaosou.common.serialization.bytebean.context.FieldCodecContext #getField()
	 */
	@Override
	public Field getField() {
		if (null != this.fieldDesc) {
			return this.fieldDesc.getField();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taotaosou.common.serialization.bytebean.context.FieldCodecContext #getNumberCodec()
	 */
	@Override
	public NumberCodec getNumberCodec() {
		if (null != fieldDesc) {
			// TODO 字节序暂默认使用小端
			return DefaultNumberCodecs.getLittleEndianNumberCodec();
		}
		return numberCodec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taotaosou.common.serialization.bytebean.context.FieldCodecContext #getByteSize()
	 */
	@Override
	public int getByteSize() {
		int ret = -1;
		if (null != fieldDesc) {
			ret = fieldDesc.getByteSize();
		} else if (null != targetType) {
			ret = super.type2DefaultByteSize(targetType);
		}
		return ret;
	}

	public ByteFieldCodec getCodecOf(FieldCodecCategory type) {
		if (null != codecProvider) {
			return codecProvider.getCodecOf(type);
		} else {
			return null;
		}
	}

	public ByteFieldCodec getCodecOf(Class<?> clazz) {
		if (null != codecProvider) {
			return codecProvider.getCodecOf(clazz);
		} else {
			return null;
		}
	}

}
