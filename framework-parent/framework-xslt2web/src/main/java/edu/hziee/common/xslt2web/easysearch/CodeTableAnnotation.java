package edu.hziee.common.xslt2web.easysearch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeTableAnnotation {
	String regName() default "";
	String author() default "";
	String description() default "";
	String createDate() default "";
	boolean useCache() default true;
}
