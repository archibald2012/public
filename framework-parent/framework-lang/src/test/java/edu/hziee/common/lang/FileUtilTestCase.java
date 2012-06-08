package edu.hziee.common.lang;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.lang.FileUtil;

public class FileUtilTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetClassFilePath() {
		System.out.println(FileUtil.getClassFilePath(FileUtilTestCase.class));
	}
}
