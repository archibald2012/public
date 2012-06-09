package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.EasySearchConfigItem;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.IWebData;

public final class EasySearchUtil {

	private EasySearchUtil() {
	}

	public static EasySearch getRegEasySearch(IWebData webData, String regName) {
		EasySearchRegCategory reg = (EasySearchRegCategory) webData
				.getAppGlobal().getRegsCollection().get(
						EasySearchRegCategory.REG_NAME);
		if (reg == null)
			throw new ToolkitException("没有发现EasySearch在系统中注册！");
		EasySearch search = reg.newInstance(regName);
		search.setWebData(webData);
		return search;
	}

	final static void setEasySearchProperty(EasySearch search,
			EasySearchConfigItem item) {
		search.setTableName(item.getTableName());
		search.setCodeField(item.getCodeField());
		search.setNameField(item.getNameField());
		search.setPYField(item.getPYField());
		search.setInfoField(item.getInfoField());
		search.setOtherFields(item.getOtherFields());
		search.setDefaultOrder(item.getDefaultOrder());
		search.setTopCount(item.getTopCount());
		search.setSql(item.getSQL());
		search.setType(item.getType());
		search.setLevel(item.getLevel());
		search.setTree(item.getTree());
		if (item.getDataRight() != null) {
			// search.setSupportData(item.getDataRight().isSupportData());
			search.setOwnerField(item.getDataRight().getOwnerField());
			search.setRightType(item.getDataRight().getRightType());
		}

		// if (item.Search != string.Empty)
		// search.Search = SearchUtil.GetRegParamSearch(item.Search);
	}
}
