package edu.hziee.common.test.client;

import edu.hziee.common.test.db.BaseTransactionalXDataSetTestCase;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AbstractClientTestCase.java 14 2012-01-10 11:54:14Z archie $
 */
public class AbstractClientTestCase extends BaseTransactionalXDataSetTestCase {

	protected Client client = null;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
