/**
 * 
 */
package edu.hziee.common.metrics;

import java.io.Serializable;

/**
 * @author Administrator
 * 
 */
public class CorrelationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String requester;

	public CorrelationInfo() {

	}

	public CorrelationInfo(String id, String requester) {
		this.id = id;
		this.requester = requester;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

}
