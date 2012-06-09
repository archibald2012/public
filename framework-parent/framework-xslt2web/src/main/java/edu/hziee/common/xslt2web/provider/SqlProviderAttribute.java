package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sys.RegAttribute;

public class SqlProviderAttribute extends RegAttribute<SqlProviderAnnotation> {

	@Override
	protected void setValue(SqlProviderAnnotation annotation) {
		setRegName(annotation.regName());
		setAuthor(annotation.author());
		setDescription(annotation.description());
		setCreateDate(annotation.createDate());
	}

}
