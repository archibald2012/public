package edu.hziee.common.xslt2web.xml;

import java.util.EnumSet;

public interface ITransformAll {
	XsltGroup getIeFiles();

	XsltGroup getNavFiles();

	IXmlSource getItem(HtmlPosition index);

	void setItem(HtmlPosition index, IXmlSource value);

	boolean isShowSource();

	void setShowSource(boolean showSource);

	String transformContent(boolean isIe);

	String transformAll(boolean isIe);

	String transformAll(boolean isIe, EnumSet<TransformPos> flags);

	void assign(ITransformAll source);
}
