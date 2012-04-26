
package edu.hziee.common.serialization.bytebean.context;

import edu.hziee.common.serialization.bytebean.codec.FieldCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * @author wangqi
 * @version $Id: DefaultEncContextFactory.java 14 2012-01-10 11:54:14Z archie $
 */
public class DefaultEncContextFactory implements EncContextFactory {

	private	FieldCodecProvider	codecProvider;
	private	NumberCodec			numberCodec;
	

	/* (non-Javadoc)
	 * @see com.taotaosou.common.serialization.bytebean.context.EncContextFactory#createEncContext(java.lang.Object, java.lang.Class, com.taotaosou.common.serialization.bytebean.field.ByteFieldDesc)
	 */
	public EncContext createEncContext(Object encObject, Class<?> type,
			ByteFieldDesc desc) {
		return new DefaultEncContext()
				.setCodecProvider(codecProvider)
				.setEncClass(type)
				.setEncObject(encObject)
				.setNumberCodec(numberCodec)
				.setFieldDesc(desc)
				.setEncContextFactory(this);
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
