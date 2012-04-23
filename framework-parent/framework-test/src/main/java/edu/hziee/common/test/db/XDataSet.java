/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    XDataSet.java
 * Creator:     Administrator
 * Create-Date: 2011-5-19 下午01:36:42
 *******************************************************************************/
package edu.hziee.common.test.db;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: XDataSet.java 14 2012-01-10 11:54:14Z archie $
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface XDataSet {
	String[] locations() default {};

	String[] dsNames() default {};

	String setupOperation() default "REFRESH";

	String teardownOperation() default "DELETE";

	String fileType() default "xls";
}
