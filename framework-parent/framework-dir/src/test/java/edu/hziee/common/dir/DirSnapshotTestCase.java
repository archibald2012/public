package edu.hziee.common.dir;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.lang.FileUtil;

public class DirSnapshotTestCase {

	@Before
	public void setUp() throws Exception {
		//FileUtil.createFile("DirSnapshotTestCase.txt");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testConstruct() {
		/*String dirName = "/DirSnapshotTestCase";
		String[] includeSuffix = new String[] { ".txt" };
		DirSnapshot snapshot = new DirSnapshot(dirName, includeSuffix);

		Map<String, Long> files = snapshot.getFileInfos();

		Assert.assertEquals(1, files.size());*/
	}
}
