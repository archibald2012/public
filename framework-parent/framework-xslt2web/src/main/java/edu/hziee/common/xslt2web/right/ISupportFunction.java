package edu.hziee.common.xslt2web.right;

import edu.hziee.common.xslt2web.sys.PageStyle;

public interface ISupportFunction {
	Object getFunctionKey();

	FunctionRightType getFuncType();

	Object getSubFunctionKey(PageStyle style, String operation);
}
