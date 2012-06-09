package edu.hziee.common.serialization.bytebean.codec.bean;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import edu.hziee.common.serialization.bytebean.DummyBean;
import edu.hziee.common.serialization.bytebean.NestedBean;
import edu.hziee.common.serialization.bytebean.SampleByteBean;
import edu.hziee.common.serialization.bytebean.codec.AnyCodec;
import edu.hziee.common.serialization.bytebean.codec.DefaultCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.array.LenArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.array.LenListCodec;
import edu.hziee.common.serialization.bytebean.codec.bean.EarlyStopBeanCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.BooleanCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ByteCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.CStyleStringCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.IntCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LenByteArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LongCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ShortCodec;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.DefaultDecContextFactory;
import edu.hziee.common.serialization.bytebean.context.DefaultEncContextFactory;
import edu.hziee.common.serialization.bytebean.field.DefaultField2Desc;

public class BeanCodecTestCase {

	private EarlyStopBeanCodec beanCodec;

	private DefaultEncContextFactory encContextFactory;
	private DefaultDecContextFactory decContextFactory;

	@Before
	public void setUp() throws Exception {
		DefaultCodecProvider codecProvider = new DefaultCodecProvider();

		// 初始化解码器集合
		codecProvider.addCodec(new AnyCodec()).addCodec(new ByteCodec())
				.addCodec(new ShortCodec()).addCodec(new IntCodec())
				.addCodec(new LongCodec()).addCodec(new CStyleStringCodec())
				.addCodec(new LenByteArrayCodec()).addCodec(new LenListCodec())
				.addCodec(new LenArrayCodec()).addCodec(new BooleanCodec());

		// 对象解码器需要指定字段注释读取方泄1�7
		beanCodec = new EarlyStopBeanCodec(new DefaultField2Desc());
		codecProvider.addCodec(beanCodec);

		encContextFactory = new DefaultEncContextFactory();
		decContextFactory = new DefaultDecContextFactory();

		encContextFactory.setCodecProvider(codecProvider);
		encContextFactory.setNumberCodec(DefaultNumberCodecs
				.getBigEndianNumberCodec());

		decContextFactory.setCodecProvider(codecProvider);
		decContextFactory.setNumberCodec(DefaultNumberCodecs
				.getBigEndianNumberCodec());

		beanCodec.setDecContextFactory(decContextFactory);
		beanCodec.setEncContextFactory(encContextFactory);
	}

	@After
	public void tearDown() throws Exception {
		beanCodec = null;
		encContextFactory = null;
		decContextFactory = null;
	}

	@Test
	public void testBeanCodec_emptyValue() {

		SampleByteBean bean = new SampleByteBean();

		byte[] assertObj = beanCodec.encode(encContextFactory.createEncContext(
				bean, SampleByteBean.class, null));

		System.out.println(ArrayUtils.toString(assertObj));

		DecResult result = beanCodec.decode(decContextFactory.createDecContext(
				assertObj, SampleByteBean.class, null, null));

		System.out.println(result.getValue());

		Assert.assertEquals(bean, (SampleByteBean) result.getValue());
	}

	@Test
	public void testBeanCodec_beanEmpty() {

		DummyBean bean = new DummyBean();

		byte[] assertObj = beanCodec.encode(encContextFactory.createEncContext(
				bean, DummyBean.class, null));

		DecResult result = beanCodec.decode(decContextFactory.createDecContext(
				assertObj, DummyBean.class, null, null));

		System.out.println(result.getValue());

		Assert.assertEquals(0, assertObj.length);
	}

	@Test
	public void testBeanCodec_normal() {

		SampleByteBean bean = new SampleByteBean();
		bean.setIntField(1);
		bean.setShortField((byte) 1);
		bean.setByteField((byte) 1);
		bean.setLongField(1L);
		bean.setStringField("中文");

		bean.setByteArrayField(new byte[] { 127 });

		ArrayList<NestedBean> listField = new ArrayList<NestedBean>();
		NestedBean bean1 = new NestedBean();
		bean1.setIntField(1);
		bean1.setShortField((short) 1);
		listField.add(bean1);
		NestedBean bean2 = new NestedBean();
		bean2.setIntField(2);
		bean2.setShortField((short) 2);
		listField.add(bean2);
		bean.setListField(listField);

		NestedBean bean3 = new NestedBean();
		bean3.setIntField(3);
		bean3.setShortField((short) 3);
		bean.setBeanField(bean3);

		byte[] assertObj = beanCodec.encode(encContextFactory.createEncContext(
				bean, SampleByteBean.class, null));

		System.out.println(ArrayUtils.toString(assertObj));

		DecResult result = beanCodec.decode(decContextFactory.createDecContext(
				assertObj, SampleByteBean.class, null, null));

		System.out.println(result.getValue());

		Assert.assertEquals(bean, (SampleByteBean) result.getValue());
	}

}
