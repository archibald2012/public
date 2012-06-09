package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.sys.XmlRegCategory;

public class EasySearchRegCategory extends
		XmlRegCategory<EasySearch, EasySearchAnnotation> {
	private static final long serialVersionUID = 1L;
	
	public final static String REG_NAME = "EASYSEARCH2";
	private final static String DISPLAY_NAME = "EasySearch";

	public EasySearchRegCategory() {
		super(REG_NAME, DISPLAY_NAME, "EasySearch2",
				EasySearchAnnotation.class, EasySearchAttribute.class);

		addXmlCache(EasySearchConfigTable.REG_NAME, new EasySearchConfigTable());
		addConfigBaseClass("EasySearch", XmlEasySearch.class);
		addConfigBaseClass("CodeTableEasySearch", CodeTableEasySearch.class);
	}
}
