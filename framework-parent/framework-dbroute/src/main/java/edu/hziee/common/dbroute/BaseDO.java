package edu.hziee.common.dbroute;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.dbroute.config.DBRoute;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: BaseDO.java 4 2012-01-10 11:51:54Z archie $
 */
public class BaseDO implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7725115871533796806L;

	private DBRoute						dbRoute;

	public DBRoute getDbRoute() {
		return dbRoute;
	}

	public void setDbRoute(DBRoute dbRoute) {
		this.dbRoute = dbRoute;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
