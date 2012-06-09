package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeDataConfigItem;
import edu.hziee.common.xslt2web.configxml.CodeTableConfigItem;
import edu.hziee.common.xslt2web.sys.RegAttribute;

public class CodeTableAttribute extends RegAttribute<CodeTableAnnotation> {
	private boolean useCache;

	public CodeTableAttribute() {
	}

	public final boolean isUseCache() {
		return useCache;
	}

	public final void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	@Override
	protected void setValue(CodeTableAnnotation annotation) {
		setRegName(annotation.regName());
		setAuthor(annotation.author());
		setDescription(annotation.description());
		setCreateDate(annotation.createDate());
		setUseCache(annotation.useCache());
	}

	public static CodeTableAttribute create(CodeTableConfigItem item) {
		CodeTableAttribute result = new CodeTableAttribute();
		result.setRegName(item.getRegName());
		result.setDescription(item.getDescription());
		result.setAuthor("edu.hziee.common.xslt2web");
		//        result.CreateDate = DateTime.Today.ToString("yyyy-MM-dd");
		result.setUseCache(item.isUseCache());

		return result;
	}

	public static CodeTableAttribute create(CodeDataConfigItem item) {
		CodeTableAttribute result = new CodeTableAttribute();
		result.setRegName(item.getRegName());
		result.setDescription(item.getDescription());
		result.setAuthor("edu.hziee.common.xslt2web");
		//        result.CreateDate = DateTime.Today.ToString("yyyy-MM-dd");
		result.setUseCache(false);

		return result;
	}
}
