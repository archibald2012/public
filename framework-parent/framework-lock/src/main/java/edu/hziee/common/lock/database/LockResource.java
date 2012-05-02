/**
 * 
 */
package edu.hziee.common.lock.database;

import java.util.Date;

/**
 * @author Administrator
 * 
 */
public class LockResource {

	private String	lockId;
	private String	masterInstance;
	private long		masterTime;
	private Date		updateTime;

	public String getLockId() {
		return lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

	public String getMasterInstance() {
		return masterInstance;
	}

	public void setMasterInstance(String masterInstance) {
		this.masterInstance = masterInstance;
	}

	public long getMasterTime() {
		return masterTime;
	}

	public void setMasterTime(long masterTime) {
		this.masterTime = masterTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
