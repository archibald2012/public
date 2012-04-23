/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    AbstractXipSignal.java
 * Creator:     wangqi
 * Create-Date: 2011-4-28 上午09:33:47
 *******************************************************************************/
package edu.hziee.common.serialization.protocol.xip;

import java.util.UUID;

import edu.hziee.common.lang.DefaultPropertiesSupport;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AbstractXipSignal.java 14 2012-01-10 11:54:14Z archie $
 */
public class AbstractXipSignal extends DefaultPropertiesSupport implements
		XipSignal {

	private UUID uuid = UUID.randomUUID();

	@Override
	public void setIdentification(UUID id) {
		this.uuid = id;
	}

	@Override
	public UUID getIdentification() {
		return this.uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractXipSignal other = (AbstractXipSignal) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
