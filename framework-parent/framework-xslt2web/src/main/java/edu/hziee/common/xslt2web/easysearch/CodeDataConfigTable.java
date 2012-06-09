package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeDataXml;
import edu.hziee.common.xslt2web.sys.XmlConfigTable;

class CodeDataConfigTable extends XmlConfigTable {
	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "CodeTableData";

	public CodeDataConfigTable() {
		super(REG_NAME, "CodeData", new CodeDataXml());
	}
}
