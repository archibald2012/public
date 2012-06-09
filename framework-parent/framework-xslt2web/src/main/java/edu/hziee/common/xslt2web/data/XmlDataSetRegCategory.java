package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.RegCategory;

public class XmlDataSetRegCategory extends
		RegCategory<BaseDataSet, XmlDataSetAnnotation> {
	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "XML_DATA_SET";
	private final static String DISPLAY_NAME = "Module≈‰÷√¿‡";

	public XmlDataSetRegCategory() {
		super(REG_NAME, DISPLAY_NAME, "xmldataset", XmlDataSetAnnotation.class,
				XmlDataSetAttribute.class);

		this.addRegClass(WebSingleXmlDataSet.class);
		this.addRegClass(WebMDXmlDataSet.class);
		this.addRegClass(WebSingleDListXmlDataSet.class);
		this.addRegClass(WebConfigXmlDataSet.class);
		this.addRegClass(WebAllRowXmlDataSet.class);
		this.addRegClass(WebListXmlDataSet.class);
		this.addRegClass(WebListDetailXmlDataSet.class);
		this.addRegClass(WebMultiXmlDataSet.class);
		this.addRegClass(WebMultiMDXmlDataSet.class);
		this.addRegClass(WebTreeXmlDataSet.class);
	}
}
