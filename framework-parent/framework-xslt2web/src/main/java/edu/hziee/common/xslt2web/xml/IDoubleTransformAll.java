package edu.hziee.common.xslt2web.xml;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;

public interface IDoubleTransformAll extends ITransformAll {
	boolean isPost();

	void setPost(boolean post);

	void setDocumentNumber(int number, PageStyle style, String operation);

	void setXmlDocument(int i, Node document);
}
