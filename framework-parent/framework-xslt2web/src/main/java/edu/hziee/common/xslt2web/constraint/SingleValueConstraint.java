package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataRowVersion;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class SingleValueConstraint extends BaseConstraint {

	public SingleValueConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;

		DataRow srow = getDataSet().getTables().getItem(getTableName())
				.getRows().getItem(position);
		String original = null;
		try {
			original = srow.getItem(getFieldName(), DataRowVersion.Original)
					.toString();
		} catch (Exception ex) {
		}

		if (original != null && value == original) {
			return true;
		} else {
			String selcount = String.format(
					"SELECT COUNT(*) FROM %s WHERE %s ='%s'", getTableName(),
					getFieldName(), value);
			int count = Integer.parseInt(DataSetUtil.executeScalar(
					getDataSet().getConnection(), selcount).toString());
			return count == 0;
		}
	}

	@Override
	public String getJavaScript() {
		return "";
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), String.format("%s²»Î¨Ò»",
				getDisplayName()), position);
	}

}
