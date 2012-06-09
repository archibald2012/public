package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.EasySearchConfigItem;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.provider.LikeParamSearch;
import edu.hziee.common.xslt2web.provider.SimpleParamSearch;
import edu.hziee.common.xslt2web.sys.CodeSearchType;

public class CodeTableEasySearch extends EasySearch {
	private EasySearchConfigItem configItem;
	private EasySearchAttribute customAttribute;

	public CodeTableEasySearch(EasySearchConfigItem configItem) {
		super();
		this.configItem = configItem;
		customAttribute = EasySearchAttribute.create(this.configItem);
		EasySearchUtil.setEasySearchProperty(this, this.configItem);
	}

	@Override
	public EasySearchAttribute getCustomAttribute() {
		return customAttribute;
	}

	@Override
	protected String getEmptySearch(CodeSearchType codeType, String value) {
		switch (codeType) {
		case CodeValue:
			if (getType() == EasySearchType.Level
					|| getType() == EasySearchType.Level0)
				setSearch(getLevelSearch());
			else
				setSearch(LikeParamSearch.Search);
			return getSearchCondition(getSearch(), getCodeField(), value);
		case CodeName:
			setSearch(SimpleParamSearch.Search);
			return getSearchCondition(getSearch(), getNameField(), value);
		case CodePY:
			setSearch(LikeParamSearch.Search);
			return getSearchCondition(getSearch(), getPYField(), value
					.toUpperCase());
		}
		throw new ToolkitException("猪头，这里的代码不可能执行！");
	}

	@Override
	protected String getSelectFields() {
		return "CODE_VALUE, CODE_NAME, CODE_PY, CODE_SORT, CODE_DEL, CODE_VALUE CODE_INFO";
	}

}
