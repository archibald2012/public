package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.DataRowCollection;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class UniqueRowConstraint extends BaseConstraint {
	private String message;

	public UniqueRowConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		this.message = String.format("%s÷ÿ∏¥£°", displayName);
		setFirstCheck(true);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;

		DataRowCollection rows = getPostDataSet().getTables().getItem(
				getTableName()).getRows();
		for (int i = 0; i < position; ++i) {
			if (value.trim().equals(
					rows.getItem(i).getItem(getFieldName()).toString().trim()))
				return false;
		}
		return true;
	}

	@Override
	public String getJavaScript() {
		return "";
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}

}
