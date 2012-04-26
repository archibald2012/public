
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
