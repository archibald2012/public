/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Transformer.java
 * Creator:     wangqi
 * Create-Date: 2011-6-14 下午03:56:12
 *******************************************************************************/
package edu.hziee.common.lang;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Transformer.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Transformer<FROM, TO> {
	public TO transform(FROM from);
}
