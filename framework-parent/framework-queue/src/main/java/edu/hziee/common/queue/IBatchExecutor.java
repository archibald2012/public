
package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: IBatchExecutor.java 14 2012-01-10 11:54:14Z archie $
 */
public interface IBatchExecutor<T> {

	void execute(List<T> records);
}
