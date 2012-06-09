package edu.hziee.common.xslt2web.xml;

import java.util.EnumSet;

import edu.hziee.common.xslt2web.sysutil.XslTransformUtil;

public class BaseTransform implements ITransformAll {
	private XsltGroup ieFiles;
	private XsltGroup navFiles;
	private boolean showSource;

	public BaseTransform() {
		ieFiles = new XsltGroup();
		navFiles = new XsltGroup();
	}

	protected final String getPartString(String xmlStr, HtmlPosition position,
			boolean isIe) {
		if (isIe)
			return XslTransformUtil.transform(xmlStr, getIeFiles().getItem(
					position));
		else
			return XslTransformUtil.transform(xmlStr, getNavFiles().getItem(
					position));
	}

	public void assign(ITransformAll source) {
		if (source == null)
			return;
		for (int i = HtmlPosition.Head.ordinal(); i <= HtmlPosition.Bottom
				.ordinal(); ++i) {
			HtmlPosition index = HtmlPosition.values()[i];
			setItem(index, source.getItem(index));
		}
		source.getIeFiles().assignTo(this.getIeFiles());
		source.getNavFiles().assignTo(this.getNavFiles());
		setShowSource(source.isShowSource());
	}

	public final XsltGroup getIeFiles() {
		return ieFiles;
	}

	public IXmlSource getItem(HtmlPosition index) {
		return null;
	}

	public final XsltGroup getNavFiles() {
		return navFiles;
	}

	public final boolean isShowSource() {
		return this.showSource;
	}

	public void setItem(HtmlPosition index, IXmlSource value) {
	}

	public final void setShowSource(boolean showSource) {
		this.showSource = showSource;
	}

	public String transformAll(boolean isIe) {
		return "";
	}

	public String transformContent(boolean isIe) {
		String content = getItem(HtmlPosition.Content).getXmlString();
		if (showSource)
			return content;
		return getPartString(content, HtmlPosition.Content, isIe);
	}

	public String transformAll(boolean isIe, EnumSet<TransformPos> flags) {
		return "";
	}

}
