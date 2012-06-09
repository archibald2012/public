package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeTableConfigItem;
import edu.hziee.common.xslt2web.sys.RegsCollection;

public final class CodeTableUtil {
	private CodeTableUtil() {
	}

	static void setCodeTableProperty(CodeTable codeTable,
			CodeTableConfigItem item) {
		codeTable.setTableName(item.getTableName());
		codeTable.setCodeField(item.getCodeField());
		codeTable.setNameField(item.getNameField());
		codeTable.setWhere(item.getWhere());
		codeTable.setOrderBy(item.getOrderBy());
	}

	public static CodeTable getRegCodeTable(RegsCollection collection,
			String regName) {
		CodeTableRegCategory regs = (CodeTableRegCategory) collection
				.get(CodeTableRegCategory.REG_NAME);
		//Debug.Assert(regs != null, "没有发现CodeTable在系统中注册！");
		return regs.newInstance(regName);
	}
}
