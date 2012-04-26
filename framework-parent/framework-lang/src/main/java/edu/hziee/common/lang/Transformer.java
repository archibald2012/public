
package edu.hziee.common.lang;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Transformer.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Transformer<FROM, TO> {
	public TO transform(FROM from);
}
