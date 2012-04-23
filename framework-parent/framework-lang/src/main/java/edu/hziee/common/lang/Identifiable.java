/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Identifyable.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:19:41
 *******************************************************************************/
package edu.hziee.common.lang;

import java.util.UUID;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Identifiable.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Identifiable {
	void setIdentification(UUID id);
	UUID getIdentification();
}
