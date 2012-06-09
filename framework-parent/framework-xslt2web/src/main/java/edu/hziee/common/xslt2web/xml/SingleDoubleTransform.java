package edu.hziee.common.xslt2web.xml;

import java.util.EnumSet;

import edu.hziee.common.xslt2web.sysutil.XslTransformUtil;

public class SingleDoubleTransform extends BaseDoubleTransform {
	private IXmlSource content;

	public SingleDoubleTransform() {
	}

	@Override
	protected String transform(String xmlContent) {
		getArgs().addParam("Doc", getXmlDocument()[0]);

		return XslTransformUtil.transform(xmlContent,
				getIeFiles().getContent(), getArgs());
	}

	@Override
	public IXmlSource getItem(HtmlPosition index) {
		if (index == HtmlPosition.Content)
			return content;
		else
			return null;
	}

	@Override
	public void setItem(HtmlPosition index, IXmlSource value) {
		if (index == HtmlPosition.Content)
			content = value;
	}

	@Override
	public String transformAll(boolean isIe, EnumSet<TransformPos> flags) {
		return this.transformContent(isIe);
	}

	@Override
	public String transformAll(boolean isIe) {
		return this.transformContent(isIe);
	}

}
