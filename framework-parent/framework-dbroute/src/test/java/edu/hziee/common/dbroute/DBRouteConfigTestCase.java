
package edu.hziee.common.dbroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import edu.hziee.common.dbroute.config.DBRoute;
import edu.hziee.common.dbroute.config.DBRouteConfig;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: DBRouteConfigTestCase.java 4 2012-01-10 11:51:54Z archie $
 */

@ContextConfiguration(locations = { "classpath:spring/applicationContext_DBRouteConfig.xml" })
public class DBRouteConfigTestCase extends AbstractJUnit4SpringContextTests {

	@Resource
	private DBRouteConfig dbRouteConfig;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRoutingDB_ByXid() {
		List<String> assertobj = dbRouteConfig.routingDB(DBRoute
				.getRoute("db1"));

		assertNotNull(assertobj);
		assertEquals(1, assertobj.size());
		assertEquals("db1", assertobj.get(0));

		assertobj = dbRouteConfig.routingDB(DBRoute.getRoute("db1, db2"));

		assertNotNull(assertobj);
		assertEquals(2, assertobj.size());
		assertEquals("db1", assertobj.get(0));
		assertEquals("db2", assertobj.get(1));

		assertobj = dbRouteConfig.routingDB(DBRoute.getRoute("db3"));

		assertNotNull(assertobj);
		assertEquals(1, assertobj.size());

		assertobj = dbRouteConfig.routingDB(DBRoute.getRoute("db1, db2, db3"));

		assertNotNull(assertobj);
		assertEquals(3, assertobj.size());
		assertEquals("db1", assertobj.get(0));
		assertEquals("db2", assertobj.get(1));
		assertEquals("db3", assertobj.get(2));

		assertobj = dbRouteConfig.routingDB(DBRoute
				.getRoute("db1, db2, db3, db4"));

		assertNotNull(assertobj);
		assertEquals(3, assertobj.size());
		assertEquals("db1", assertobj.get(0));
		assertEquals("db2", assertobj.get(1));
		assertEquals("db3", assertobj.get(2));
	}
	@Test
	public void testRoutingDB_ByItemId() {
		List<String> assertobj = dbRouteConfig.routingDB(DBRoute
				.getRouteByItem("itemId", "6000"));
		assertNotNull(assertobj);
		assertEquals(0, assertobj.size());

		assertobj = dbRouteConfig.routingDB(DBRoute.getRouteByItem("website",
				"taobao"));
		assertNotNull(assertobj);
		assertEquals(0, assertobj.size());

		Map<String, String> items = new HashMap<String, String>();
		items.put("website", "taobao");
		items.put("itemId", "6000");
		assertobj = dbRouteConfig.routingDB(DBRoute.getRouteByItems(items));
		assertNotNull(assertobj);
		assertEquals(1, assertobj.size());
		assertEquals("db2", assertobj.get(0));

		items = new HashMap<String, String>();
		items.put("website", "taobao");
		items.put("itemId", "4000");
		assertobj = dbRouteConfig.routingDB(DBRoute.getRouteByItems(items));
		assertNotNull(assertobj);
		assertEquals(1, assertobj.size());
		assertEquals("db1", assertobj.get(0));

		assertobj = dbRouteConfig.routingDB(DBRoute.getRouteByItem("website",
				"360buy"));
		assertNotNull(assertobj);
		assertEquals(1, assertobj.size());
		assertEquals("db3", assertobj.get(0));

		assertobj = dbRouteConfig.routingDB(DBRoute.getRouteByItem("itemId",
				"a000"));
		assertNotNull(assertobj);
		assertEquals(0, assertobj.size());
	}

	@Test
	public void testRoutingDB_BySqlId() {
		List<String> assertobj = dbRouteConfig.routingDB("FINDUSER-BY-USER-ID");
		assertNotNull(assertobj);
		assertEquals(2, assertobj.size());
		assertEquals("db1", assertobj.get(0));
		assertEquals("db2", assertobj.get(1));

		assertobj = dbRouteConfig.routingDB("FINDUSER-BY-USER-NAME");
		assertNotNull(assertobj);
		assertEquals(0, assertobj.size());
	}

	public void setDbRouteConfig(DBRouteConfig dbRouteConfig) {
		this.dbRouteConfig = dbRouteConfig;
	}

}
