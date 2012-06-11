package edu.hziee.common.lang;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoundRobinTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {

		RoundRobin<Integer> rr = new RoundRobin<Integer>(new ArrayList<Integer>());

		Assert.assertFalse(rr.hasNext());
		Assert.assertNull(rr.next());

		rr = new RoundRobin<Integer>(Arrays.asList(new Integer[] { 1, 2 }));

		Assert.assertTrue(rr.hasNext());
		Assert.assertEquals(new Integer(1), rr.next());
		Assert.assertEquals(new Integer(2), rr.next());
		Assert.assertEquals(new Integer(1), rr.next());
		Assert.assertEquals(new Integer(2), rr.next());
	}
}
