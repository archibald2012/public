package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.RegAttribute;

public class XmlDataSetAttribute extends RegAttribute<XmlDataSetAnnotation> {

	@Override
	protected void setValue(XmlDataSetAnnotation annotation) {
		setRegName(annotation.regName());
		setAuthor(annotation.author());
		setCreateDate(annotation.createDate());
		setDescription(annotation.description());
	}

}
