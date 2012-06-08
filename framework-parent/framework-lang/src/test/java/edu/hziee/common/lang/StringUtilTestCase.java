/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    StringUtilTestCase.java
 * Creator:     wangqi
 * Create-Date: 2011-8-16 下午03:15:14
 *******************************************************************************/
package edu.hziee.common.lang;

import org.junit.Assert;
import org.junit.Test;

import edu.hziee.common.lang.StringUtil;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: StringUtilTestCase.java 14 2012-01-10 11:54:14Z archie $
 */
public class StringUtilTestCase {

	@Test
	public void testRightPad() {
		Assert.assertEquals("1230000000", StringUtil.rightPad("123", 10, '0'));
		Assert.assertEquals("123", StringUtil.rightPad("123", 1, '0'));
		Assert.assertNull(StringUtil.rightPad(null, 1, '0'));
		Assert.assertEquals("123aaaaaaa", StringUtil.rightPad("123", 10, 'a'));
	}
}
