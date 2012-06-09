package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeDataConfigItem;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.data.DataTableCollection;
import edu.hziee.common.xslt2web.data.DbCommand;

public class XmlDataCodeTable extends CodeTable {
	private CodeDataConfigItem configItem;
	private CodeTableAttribute customAttribute;
	private DataTable table;

	public XmlDataCodeTable(CodeDataConfigItem config) {
		super();
		configItem = config;
		customAttribute = CodeTableAttribute.create(config);
		setTableName(configItem.getRegName());
		this.table = configItem.getCodeTable();
	}

	@Override
	public boolean canEasySearch() {
		return false;
	}

	@Override
	public CodeTableAttribute getAttribute() {
		return customAttribute;
	}

	@Override
	public String getSql(BaseDataSet dataSet) {
		return "";
	}

	public final DataTable getTable() {
		return table;
	}

	@Override
	public void select(DbCommand command, BaseDataSet dataSet) {
		DataTableCollection tables = dataSet.getTables();
		if (!tables.contains(getTableName()))
			tables.add(table.copy());
	}

}
