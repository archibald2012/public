package edu.hziee.common.xslt2web.web;

import yjc.toolkit.sys.RegAttribute;

public final class WebPageAttribute extends RegAttribute<WebPageAnnotation> {

	@Override
	protected void setValue(WebPageAnnotation annotation) {
		setRegName(annotation.regName());
		setAuthor(annotation.author());
		setDescription(annotation.description());
		setCreateDate(annotation.createDate());
	}
}
