package edu.hziee.common.lang;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.lang.Object2XML;

public class Object2XMLTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		SampleSignal obj = new SampleSignal();
		obj.setIntField(1);
		obj.setShortField((byte) 1);
		obj.setByteField((byte) 1);
		obj.setLongField(1L);
		obj.setStringField("中文");

		obj.setByteArrayField(new byte[] { 127 });

		String outFileName = "/tmp/" + String.format("%08d", 1) + ".xml";
		FileUtil.createFile(outFileName);
		String outFilePath = Object2XML.object2XML(obj, outFileName);

		Object assertobj = Object2XML.xml2Object(outFilePath);

		Assert.assertEquals(assertobj, obj);
	}
}
