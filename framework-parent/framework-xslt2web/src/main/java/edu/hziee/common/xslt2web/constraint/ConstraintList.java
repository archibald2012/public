package edu.hziee.common.xslt2web.constraint;

import java.util.ArrayList;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataRowState;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DataTable;

class ConstraintList extends ArrayList<BaseConstraint> {
	private static final long serialVersionUID = 1L;
	
	private BaseDataSet dataSet;
	private boolean isSerious;

	public ConstraintList(BaseDataSet dataSet) {
		super();
		this.dataSet = dataSet;
	}

	public void check(DataSet postDataSet, String tableName,
			ErrorObjectCollection errorObjects) {
		for (BaseConstraint constraint : this) {
			DataTable table = postDataSet.getTables().getItem(tableName);
			if (table == null)
				continue;
			if (table.getColumns().indexOf(constraint.getFieldName()) == -1)
				continue;
			constraint.setDataSet(dataSet, postDataSet);
			int i = 0;
			for (DataRow row : table.getRows()) {
				if (row.getRowState() == DataRowState.Deleted) {
					++i;
					continue;
				}
				String value = row.getItem(constraint.getFieldName())
						.toString();
				if (!errorObjects.isExists(constraint.getFieldName(), i)
						&& !constraint.checkError(value, i)) {
					BaseErrorObject error = constraint.newErrorObject(i);
					if (error != null) {
						errorObjects.add(error);
						isSerious = isSerious || constraint.isSerious();
					}
				}
				++i;
			}
		}
	}
}
