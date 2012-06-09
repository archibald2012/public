package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.constraint.VariousConstraint;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.sys.IWebData;

public class EasySearchConstraint extends VariousConstraint {
	private EasySearch easySearch;
	private IWebData webData;
	private String regName;

	public EasySearchConstraint(String fieldName, String displayName,
			IWebData webData, String regName) {
		super(fieldName, displayName);
		this.webData = webData;
		this.regName = regName;
	}

	public final EasySearch getEasySearch() {
		if (easySearch == null)
			easySearch = EasySearchUtil.getRegEasySearch(webData, regName);
		easySearch.setDataSet(getDataSet(), getPostDataSet());
		return easySearch;
	}

	@Override
	protected boolean setExistData(DbCommand command, BaseDataSet dataSet,
			String tableName, String value, String valueID, int position) {
		return getEasySearch().setExistData(command, dataSet, tableName, value,
				valueID);
	}

	@Override
	protected boolean setInfoData(DbCommand command, BaseDataSet dataSet,
			String tableName, String value, int position) {
		return getEasySearch().setInfoData(command, dataSet, tableName, value);
	}

	@Override
	protected boolean setVariousData(DbCommand command, BaseDataSet dataSet,
			String tableName, String value, int position) {
		return getEasySearch().setVariousData(command, dataSet, tableName,
				value);
	}

}
