package edu.hziee.common.xslt2web.xml;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;

public interface IDoubleTransform {
	boolean getSupportDoubleTransform(PageStyle style, String operation);

	int getDocumentNumber(PageStyle style, String operation);

	Node getXmlDocument(int i, PageStyle style, String operation);

	String getDefaultXsltTemplate(PageStyle style, String operation);
}
