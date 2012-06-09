package edu.hziee.common.xslt2web.web;

import java.util.EnumSet;

import yjc.toolkit.sysutil.XslTransformUtil;
import yjc.toolkit.xml.BaseTransform;
import yjc.toolkit.xml.HtmlPosition;
import yjc.toolkit.xml.IXmlSource;
import yjc.toolkit.xml.TransformPos;
import yjc.toolkit.xml.XsltArgumentList;

class InternalDailogTransform extends BaseTransform {
	private IXmlSource[] source;
	private XsltArgumentList args;
	private boolean post;

	public InternalDailogTransform() {
		super();
		args = new XsltArgumentList();
		source = new IXmlSource[1];
		args.addParam("IsPost", post);
	}

	@Override
	public IXmlSource getItem(HtmlPosition index) {
		if (index == HtmlPosition.Content)
			return source[0];
		return null;
	}

	@Override
	public void setItem(HtmlPosition index, IXmlSource value) {
		if (index == HtmlPosition.Content)
			source[0] = value;
	}

	public final boolean isPost() {
		return post;
	}

	public final void setPost(boolean post) {
		if (this.post != post) {
			this.post = post;
			args.addParam("IsPost", post);
		}
	}

	@Override
	public String transformAll(boolean isIe, EnumSet<TransformPos> flags) {
		return transformContent(isIe);
	}

	@Override
	public String transformAll(boolean isIe) {
		return transformContent(isIe);
	}

	@Override
	public String transformContent(boolean isIe) {
		String content = this.getItem(HtmlPosition.Content).getXmlString();
		if (isShowSource())
			return content;
		content = XslTransformUtil.transform(content,
				getIeFiles().getContent(), args);
		return content;
	}
}
