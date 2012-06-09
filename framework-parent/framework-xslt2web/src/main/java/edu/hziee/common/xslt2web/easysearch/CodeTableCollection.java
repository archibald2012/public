package edu.hziee.common.xslt2web.easysearch;

import java.util.ArrayList;
import java.util.HashMap;

import edu.hziee.common.xslt2web.sys.RegsCollection;

public class CodeTableCollection extends ArrayList<CodeTable> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, CodeTable> codeTableNames;

	public CodeTableCollection() {
		super();

		codeTableNames = new HashMap<String, CodeTable>();
	}

	public final CodeTable getItem(int index) {
		return this.get(index);
	}

	public final CodeTable getItem(String regName) {
		CodeTable result = codeTableNames.get(regName);
		return result;
	}

	@Override
	public void clear() {
		super.clear();
		codeTableNames.clear();
	}

	public final void add(RegsCollection regs, String regName) {
		if (!codeTableNames.containsKey(regName)) {
			CodeTable code = CodeTableUtil.getRegCodeTable(regs, regName);
			this.add(code);
			codeTableNames.put(regName, code);
		}
	}

}
