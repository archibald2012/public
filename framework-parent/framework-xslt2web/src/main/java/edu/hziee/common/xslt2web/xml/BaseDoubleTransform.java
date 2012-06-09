package edu.hziee.common.xslt2web.xml;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;

public abstract class BaseDoubleTransform extends BaseTransform implements
		IDoubleTransformAll {
	private int documentNumber;
	private boolean post;
	private PageStyle style;
	private String operation;
	private Node[] xmlDocument;
	private XsltArgumentList args;

	public BaseDoubleTransform() {
		args = new XsltArgumentList();
		args.addParam("IsPost", post);
	}

	public final boolean isPost() {
		return post;
	}

	public final void setDocumentNumber(int number, PageStyle style,
			String operation) {
		this.documentNumber = number;
		this.xmlDocument = new Node[number];
		this.style = style;
		this.operation = operation;
	}

	public final void setPost(boolean post) {
		if (this.post != post) {
			this.post = post;
			args.addParam("IsPost", post);
		}
	}

	public final void setXmlDocument(int i, Node document) {
		xmlDocument[i] = document;
	}

	public final int getDocumentNumber() {
		return documentNumber;
	}

	public final PageStyle getStyle() {
		return style;
	}

	public final String getOperation() {
		return operation;
	}

	public final XsltArgumentList getArgs() {
		return args;
	}

	public final Node[] getXmlDocument() {
		return xmlDocument;
	}

	protected abstract String transform(String xmlContent);

	protected String transformContent(String xmlContent) {
		if (isShowSource())
			return xmlContent;
		else
			return transform(xmlContent);
	}

	public String transformContent(boolean isIe) {
		return transformContent(this.getItem(HtmlPosition.Content)
				.getXmlString());
	}
}
