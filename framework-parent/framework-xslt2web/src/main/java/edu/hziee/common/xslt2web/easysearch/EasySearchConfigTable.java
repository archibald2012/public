package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeTableEasySearchConfigItem;
import edu.hziee.common.xslt2web.configxml.EasySearchXml;
import edu.hziee.common.xslt2web.sys.IXmlConfigItem;
import edu.hziee.common.xslt2web.sys.XmlConfigTable;

public class EasySearchConfigTable extends XmlConfigTable {
	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "EasySearch2";

	public EasySearchConfigTable() {
		super(REG_NAME, "EasySearch2", new EasySearchXml());
	}

	public synchronized IXmlConfigItem get(Object key) {
		IXmlConfigItem result = super.get(key);
		if (result == null) {
			if (key.toString().startsWith("CD_")) {
				result = new CodeTableEasySearchConfigItem(key.toString());
				put(result.getRegName(), result);
				return result;
			}
			return null;
		} else
			return result;
	}
}
