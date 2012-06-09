package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.EasySearchConfigItem;
import edu.hziee.common.xslt2web.sys.RegAttribute;

public class EasySearchAttribute extends RegAttribute<EasySearchAnnotation> {
	private boolean useCache;

	@Override
	protected void setValue(EasySearchAnnotation annotation) {
		setRegName(annotation.regName());
		setAuthor(annotation.author());
		setDescription(annotation.description());
		setCreateDate(annotation.createDate());
		setUseCache(annotation.useCache());
	}

	public final boolean isUseCache() {
		return useCache;
	}

	public final void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public static EasySearchAttribute create(EasySearchConfigItem item) {
		EasySearchAttribute result = new EasySearchAttribute();
		result.setRegName(item.getRegName());
		result.setDescription(item.getDescription());
		result.setAuthor("edu.hziee.common.xslt2web");
		//        result.CreateDate = DateTime.Today.ToString("yyyy-MM-dd");
		result.setUseCache(item.isUseCache());

		return result;
	}
}
