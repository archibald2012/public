package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeTableXml;
import edu.hziee.common.xslt2web.configxml.DefaultCodeTableConfigItem;
import edu.hziee.common.xslt2web.sys.IXmlConfigItem;
import edu.hziee.common.xslt2web.sys.XmlConfigTable;

class CodeTableConfigTable extends XmlConfigTable {
	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "RegCodeTable";

	public CodeTableConfigTable() {
		super(REG_NAME, "CodeTable", new CodeTableXml());
	}

	@Override
	public synchronized IXmlConfigItem get(Object key) {
		IXmlConfigItem result = super.get(key);
		if (result == null) {
			if (key.toString().startsWith("CD_")) {
				result = new DefaultCodeTableConfigItem(key.toString());
				put(result.getRegName(), result);
				return result;
			}
			return null;
		} else
			return result;
	}

}
