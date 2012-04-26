
package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ExceptionListener.java 14 2012-01-10 11:54:14Z archie $
 */
public interface ExceptionListener<T> {
	void onException(List<T> records);
}
