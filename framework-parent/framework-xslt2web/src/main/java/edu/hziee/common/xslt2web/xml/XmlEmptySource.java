package edu.hziee.common.xslt2web.xml;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class XmlEmptySource implements IXmlSource {
	private String encoding;
	private String content;

	public XmlEmptySource() {
		this.content = "";
		this.encoding = "gb2312";
	}

	public final String getEncoding() {
		return this.encoding;
	}

	public String getXmlString() {
		return StringUtil.getXmlString(content, encoding);
	}

	public final void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	protected final String getContent() {
		return content;
	}

	protected final void setContent(String content) {
		this.content = content;
	}

}
