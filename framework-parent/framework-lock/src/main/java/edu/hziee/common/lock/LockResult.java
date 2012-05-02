/**
 * 
 */
package edu.hziee.common.lock;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LockResult {

	private LockStatus	status		= LockStatus.LOCK_ACQUIRED;
	private Throwable		exception	= null;
	private String			lockedId	= null;

	public LockResult setLockedId(String lockedId) {
		this.lockedId = lockedId;
		return this;
	}

	public LockResult setException(Throwable e) {
		this.exception = e;
		return this;
	}

	public LockResult setStatus(LockStatus status) {
		this.status = status;
		return this;
	}

	public Throwable getException() {
		return exception;
	}

	public LockStatus getStatus() {
		return status;
	}

	public String getLockedId() {
		return lockedId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
