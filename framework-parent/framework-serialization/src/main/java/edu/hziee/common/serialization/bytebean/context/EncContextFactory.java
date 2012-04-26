
package edu.hziee.common.serialization.bytebean.context;

import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: EncContextFactory.java 14 2012-01-10 11:54:14Z archie $
 */
public interface EncContextFactory {
	EncContext createEncContext(Object encObject, Class<?> type,
			ByteFieldDesc desc);
}
