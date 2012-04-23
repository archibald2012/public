package edu.hziee.common.dbroute;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.hziee.common.dbroute.BaseDAO;

public class BaseDAOTestCase {

	private AbstractApplicationContext ctx;
	private BaseDAO baseDAO;

	@Before
	public void setUp() throws Exception {
		//ctx = new ClassPathXmlApplicationContext(new String[] { "spring/applicationContextBaseDAOTestCase.xml" });
		//baseDAO = (BaseDAO) ctx.getBean("baseDAO");
	}

	@After
	public void tearDown() throws Exception {
		ctx = null;
		baseDAO = null;
	}

	@Test
	public void testQueryForCount() throws Exception {
		//baseDAO.getQueryDelegate().queryForCount("", new DBRoute());
	}

	@Test
	public void testQueryForObject() throws Exception {

	}

	@Test
	public void testQueryForList() throws Exception {

	}

	@Test
	public void testQueryForPagedList() throws Exception {

	}

	@Test
	public void testQueryForMergedList() throws Exception {

	}

	@Test
	public void testInsert() throws Exception {

	}

	@Test
	public void testDelete() throws Exception {

	}

	@Test
	public void testUpdate() throws Exception {

	}

	@Test
	public void testBatchInsert() throws Exception {

	}

	@Test
	public void testBatchUpdate() throws Exception {

	}
}
