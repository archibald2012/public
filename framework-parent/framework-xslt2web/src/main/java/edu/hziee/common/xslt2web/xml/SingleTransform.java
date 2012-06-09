package edu.hziee.common.xslt2web.xml;

import java.util.EnumSet;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class SingleTransform extends BaseTransform {
	private IXmlSource content;

	public SingleTransform() {
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
		return transformAll(isIe);
	}

	@Override
	public String transformAll(boolean isIe) {

		String content = getItem(HtmlPosition.Content).getXmlString();
		if (isShowSource())
			return content;
		content = getPartString(content, HtmlPosition.Content, isIe);
		String fmtStr = getTransformFmtStr(EnumSet.of(TransformPos.Head,
				TransformPos.HeadLeft, TransformPos.Bottom));
		if (StringUtil.isEmpty(fmtStr))
			return "";
		return String.format(fmtStr, content);
	}

	protected String getTransformFmtStr(EnumSet<TransformPos> of) {
		return "";
	}

}
