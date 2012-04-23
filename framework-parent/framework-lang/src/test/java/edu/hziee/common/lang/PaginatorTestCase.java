package edu.hziee.common.lang;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.lang.Paginator;

public class PaginatorTestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSlider() {
		Paginator pg = new Paginator();

		int[] slider = pg.getSlider();
		Assert.assertEquals(0, slider.length);

		pg.setItems(2012);
		slider = pg.getSlider();
		for (int i = 0; i < slider.length; i++) {
			Assert.assertEquals(i + 1, slider[i]);
		}
	}

	@Test
	public void testToString() {
		Paginator pg = new Paginator();

		System.out.println(pg);
		pg.setItems(2012);
		System.out.println(pg);
	}

	@Test
	public void testConstructor() {
		Paginator pg = new Paginator();

		Assert.assertTrue(pg.getItems() == 0);
		Assert.assertTrue(pg.getPage() == 0);
		Assert.assertTrue(pg.getItemsPerPage() == Paginator.DEFAULT_ITEMS_PER_PAGE);

		Assert.assertTrue(pg.getFirstPage() == 0);

		Assert.assertTrue(pg.getNextPage() == 0);
		Assert.assertTrue(pg.getNextPage(1) == 0);
		Assert.assertTrue(pg.getNextPage(2) == 0);

		Assert.assertTrue(pg.getPreviousPage() == 0);
		Assert.assertTrue(pg.getPreviousPage(1) == 0);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 0);
		Assert.assertTrue(pg.getEndIndex() == 0);
	}

	@Test
	public void testConstructor2() {
		Paginator pg = new Paginator(20);

		Assert.assertTrue(pg.getItems() == 0);
		Assert.assertTrue(pg.getPage() == 0);
		Assert.assertTrue(pg.getItemsPerPage() == 20);

		Assert.assertTrue(pg.getPages() == 0);
		Assert.assertTrue(pg.getFirstPage() == 0);

		Assert.assertTrue(pg.getNextPage() == 0);
		Assert.assertTrue(pg.getNextPage(1) == 0);
		Assert.assertTrue(pg.getNextPage(2) == 0);

		Assert.assertTrue(pg.getPreviousPage() == 0);
		Assert.assertTrue(pg.getPreviousPage(1) == 0);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 0);
		Assert.assertTrue(pg.getEndIndex() == 0);
	}

	@Test
	public void testConstructor3() {
		Paginator pg = new Paginator(30, 901);

		Assert.assertTrue(pg.getItems() == 901);
		Assert.assertTrue(pg.getPage() == 0);
		Assert.assertTrue(pg.getItemsPerPage() == 30);

		Assert.assertTrue(pg.getPages() == 31);
		Assert.assertTrue(pg.getFirstPage() == 0);
		Assert.assertTrue(pg.getLastPage() == 30);

		Assert.assertTrue(pg.getNextPage() == 1);
		Assert.assertTrue(pg.getNextPage(1) == 1);
		Assert.assertTrue(pg.getNextPage(2) == 2);

		Assert.assertTrue(pg.getPreviousPage() == 0);
		Assert.assertTrue(pg.getPreviousPage(1) == 0);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 0);
		Assert.assertTrue(pg.getEndIndex() == 29);
	}

	@Test
	public void testSetItems() {
		Paginator pg = new Paginator(20);
		pg.setItems(51);

		Assert.assertTrue(pg.getItems() == 51);
		Assert.assertTrue(pg.getPage() == 0);
		Assert.assertTrue(pg.getItemsPerPage() == 20);

		Assert.assertTrue(pg.getPages() == 3);
		Assert.assertTrue(pg.getFirstPage() == 0);
		Assert.assertTrue(pg.getLastPage() == 2);

		Assert.assertTrue(pg.getNextPage() == 1);
		Assert.assertTrue(pg.getNextPage(1) == 1);
		Assert.assertTrue(pg.getNextPage(2) == 2);

		Assert.assertTrue(pg.getPreviousPage() == 0);
		Assert.assertTrue(pg.getPreviousPage(1) == 0);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 0);
		Assert.assertTrue(pg.getEndIndex() == 19);
	}

	@Test
	public void testSetPage() {
		Paginator pg = new Paginator(50);
		pg.setItems(51);
		pg.setPage(2);

		Assert.assertTrue(pg.getItems() == 51);
		Assert.assertTrue(pg.getPage() == 1);
		Assert.assertTrue(pg.getItemsPerPage() == 50);

		Assert.assertTrue(pg.getPages() == 2);
		Assert.assertTrue(pg.getFirstPage() == 0);
		Assert.assertTrue(pg.getLastPage() == 1);

		Assert.assertTrue(pg.getNextPage() == 1);
		Assert.assertTrue(pg.getNextPage(1) == 1);
		Assert.assertTrue(pg.getNextPage(2) == 1);

		Assert.assertTrue(pg.getPreviousPage() == 0);
		Assert.assertTrue(pg.getPreviousPage(1) == 0);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 50);
		Assert.assertTrue(pg.getEndIndex() == 50);
	}

	@Test
	public void testSetItemsPerPage() {
		Paginator pg = new Paginator(50);
		pg.setItems(201);
		pg.setPage(2);

		Assert.assertTrue(pg.getItems() == 201);
		Assert.assertTrue(pg.getPage() == 2);
		Assert.assertTrue(pg.getItemsPerPage() == 50);

		Assert.assertTrue(pg.getPages() == 5);
		Assert.assertTrue(pg.getFirstPage() == 0);
		Assert.assertTrue(pg.getLastPage() == 4);

		Assert.assertTrue(pg.getNextPage() == 3);
		Assert.assertTrue(pg.getNextPage(1) == 3);
		Assert.assertTrue(pg.getNextPage(2) == 4);

		Assert.assertTrue(pg.getPreviousPage() == 1);
		Assert.assertTrue(pg.getPreviousPage(1) == 1);
		Assert.assertTrue(pg.getPreviousPage(2) == 0);

		Assert.assertTrue(pg.getBeginIndex() == 100);
		Assert.assertTrue(pg.getEndIndex() == 149);

		pg.setItemsPerPage(20);

		Assert.assertTrue(pg.getItems() == 201);
		Assert.assertTrue(pg.getPage() == 3);
		Assert.assertTrue(pg.getItemsPerPage() == 20);

		Assert.assertTrue(pg.getPages() == 11);
		Assert.assertTrue(pg.getFirstPage() == 0);
		Assert.assertTrue(pg.getLastPage() == 10);

		Assert.assertTrue(pg.getNextPage() == 4);
		Assert.assertTrue(pg.getNextPage(1) == 4);
		Assert.assertTrue(pg.getNextPage(2) == 5);

		Assert.assertTrue(pg.getPreviousPage() == 2);
		Assert.assertTrue(pg.getPreviousPage(1) == 2);
		Assert.assertTrue(pg.getPreviousPage(2) == 1);

		Assert.assertTrue(pg.getBeginIndex() == 60);
		Assert.assertTrue(pg.getEndIndex() == 79);
	}
}
