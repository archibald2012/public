package edu.hziee.common.forbidden.decorate;

public interface DecoratorHandler {

	String decorate(String filterContent);

	int getUpperLimit();

	String getReplaceText();

}
