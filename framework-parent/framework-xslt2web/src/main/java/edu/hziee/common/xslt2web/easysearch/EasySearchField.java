package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.constraint.VariousConstraint;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataNavEventArgs;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.IWebData;

public class EasySearchField extends BaseEasySearchField {
	private String regName;
	private StringBuilder values;
	private int count;
	private boolean createConstraint;
	private EasySearch item;
	private IWebData webData;

	public EasySearchField(String fieldName, String displayName,
			String regName, BaseDataSet dataSet, boolean createConstraint) {
		super(fieldName, displayName, dataSet);

		setWebData(dataSet);
		this.regName = regName;
		this.createConstraint = createConstraint;
	}

	public final EasySearch getItem() {
		if (item == null)
			item = EasySearchUtil.getRegEasySearch(webData, regName);
		return item;
	}

	private void setWebData(BaseDataSet dataSet) {
		if (dataSet instanceof IWebData)
			webData = (IWebData) dataSet;
	}

	@Override
	public void beginNavigateData(EventArgs e) {
		count = 0;
		values = new StringBuilder();
	}

	@Override
	public void endNavigateData(EventArgs e) {
		if (count > 0) {
			DbCommand command = getDataSet().getConnection().createCommand();
			EasySearch item = getItem();
			item.setListData(command, getDataSet(), getFieldName(), values
					.toString());
		}
	}

	@Override
	public VariousConstraint getConstraint() {
		return createConstraint ? new EasySearchConstraint(getFieldName(),
				getDisplayName(), webData, regName) : null;
	}

	@Override
	public void navigatingData(DataNavEventArgs e) {
		if (e.getRow().getTable().getColumns().contains(getFieldName())) {
			if (count++ != 0)
				values.append(", ");
			values.append(String.format("'%s'", e.getRow().getItem(
					getFieldName())));
		}
	}

}
