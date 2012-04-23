/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    MsgCode2TypeMetainfo.java
 * Creator:     wangqi
 * Create-Date: 2011-4-28 上午09:46:29
 *******************************************************************************/
package edu.hziee.common.serialization.protocol.meta;


/**
 * TODO
 * @author wangqi
 * @version $Id: MsgCode2TypeMetainfo.java 14 2012-01-10 11:54:14Z archie $
 */
public interface MsgCode2TypeMetainfo {
	Class<?> find(int value);
}
