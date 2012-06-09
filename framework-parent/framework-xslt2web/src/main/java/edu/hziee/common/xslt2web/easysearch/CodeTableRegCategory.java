package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.sys.XmlRegCategory;

public class CodeTableRegCategory extends
		XmlRegCategory<CodeTable, CodeTableAnnotation> {

	private static final long serialVersionUID = 1L;
	public final static String REG_NAME = "CODE_TABLE";
	private final static String DISPLAY_NAME = "´úÂë±í";

	public CodeTableRegCategory() {
		super(REG_NAME, DISPLAY_NAME, "CodeTable", CodeTableAnnotation.class,
				CodeTableAttribute.class);

		addXmlCache(CodeTableConfigTable.REG_NAME, new CodeTableConfigTable());
		addXmlCache(CodeDataConfigTable.REG_NAME, new CodeDataConfigTable());
		
		addConfigBaseClass(CodeTableConfigTable.REG_NAME, "",
				XmlCodeTable.class);
		addConfigBaseClass(CodeDataConfigTable.REG_NAME, "",
				XmlDataCodeTable.class);
	}
}
