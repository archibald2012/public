/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DefaultDecContextFactory.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午12:58:53
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.context;

import edu.hziee.common.serialization.bytebean.codec.FieldCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * @author wangqi
 * @version $Id: DefaultDecContextFactory.java 14 2012-01-10 11:54:14Z archie $
 */
public class DefaultDecContextFactory implements DecContextFactory {

	private	FieldCodecProvider	codecProvider;
	private NumberCodec numberCodec;
	

	/* (non-Javadoc)
	 * @see com.taotaosou.common.serialization.bytebean.context.DecContextFactory#createDecContext(byte[], java.lang.Class, java.lang.Object, com.taotaosou.common.serialization.bytebean.field.ByteFieldDesc)
	 */
	public DecContext createDecContext(byte[] decBytes, Class<?> targetType,
			Object parent, ByteFieldDesc desc) {
		return new DefaultDecContext()
				.setCodecProvider(codecProvider)
				.setDecBytes(decBytes)
				.setDecClass(targetType)
				.setDecOwner(parent)
				.setNumberCodec(numberCodec)
				.setFieldDesc(desc)
				.setDecContextFactory(this);
	}

	public FieldCodecProvider getCodecProvider() {
		return codecProvider;
	}

	public void setCodecProvider(FieldCodecProvider codecProvider) {
		this.codecProvider = codecProvider;
	}

	public NumberCodec getNumberCodec() {
		return numberCodec;
	}

	public void setNumberCodec(NumberCodec numberCodec) {
		this.numberCodec = numberCodec;
	}

}
