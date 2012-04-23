/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DataSetConfig.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午09:54:30
 *******************************************************************************/
package edu.hziee.common.test.db;

import org.dbunit.IDatabaseTester;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: DatasetConfig.java 14 2012-01-10 11:54:14Z archie $
 */
public class DatasetConfig {
	
	private IDatabaseTester databaseTester;
	
	private boolean transactional;

	private String location;
	private String dsName;
	private String setupOperation;
	private String teardownOperation;

	public DatasetConfig location(String location) {
		this.location = location;
		return this;
	}

	public DatasetConfig dsName(String dsName) {
		this.dsName = dsName;
		return this;
	}

	public DatasetConfig setupOperation(String setupOperation) {
		this.setupOperation = setupOperation;
		return this;
	}

	public DatasetConfig teardownOperation(String teardownOperation) {
		this.teardownOperation = teardownOperation;
		return this;
	}

	public DatasetConfig(IDatabaseTester databaseTester, boolean transactional) {
		this.databaseTester = databaseTester;
		this.transactional = transactional;
	}

	public IDatabaseTester getDatabaseTester() {
		return databaseTester;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public String getLocation() {
		return location;
	}

	public String getDsName() {
		return dsName;
	}

	public String getSetupOperation() {
		return setupOperation;
	}

	public String getTeardownOperation() {
		return teardownOperation;
	}

}
