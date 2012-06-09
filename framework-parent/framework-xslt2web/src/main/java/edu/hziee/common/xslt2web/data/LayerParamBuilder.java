package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;

public class LayerParamBuilder implements IParamBuilder {
	private String sql;
	private DbDataParameter[] params;

	public LayerParamBuilder(ITreeFieldGroup treeInfo, DbConnection connection, String exID) {
		prepareSQL(treeInfo, connection, exID);
	}

	private void prepareSQL(ITreeFieldGroup treeInfo, DbConnection connection, String exID) {
		TreeFieldGroup fields = treeInfo.getTreeFields();
		ArrayList<DbDataParameter> paramList = new ArrayList<DbDataParameter>();

		IParamBuilder builder = SQLParamBuilder.getEqualSQL(TypeCode.String,
				treeInfo.isParentID() ? fields.getParentID() : fields.getId(),
				treeInfo.getRootID());
		String baseSql = String.format("SELECT %s FROM %s WHERE ", fields
				.getLayer(), treeInfo.getTableName());
		String parentLayer = DataSetUtil.executeScalar(connection,
				baseSql + builder.getSQL(), builder.getParams()).toString();
		builder = SQLParamBuilder.getEqualSQL(TypeCode.String, fields.getId(),
				exID);
		String layer = DataSetUtil.executeScalar(connection,
				baseSql + builder.getSQL(), builder.getParams()).toString();
		StringBuilder sqlBuilder = new StringBuilder();
		if (layer.length() > parentLayer.length()) {
			int len = layer.length() / 3;
			for (int i = len - 1; i >= 0; --i) {
				int subLen = i * 3;
				String subStr = layer.substring(0, subLen);
				sqlBuilder.append(String.format("%s LIKE ? OR ", fields
						.getLayer()));
				DbDataParameter param = DbDataParameter
						.createParameter(TypeCode.String);
				param.setValue(subStr + "___");
				paramList.add(param);

				if (parentLayer.equals(subStr))
					break;
			}
		}

		sqlBuilder.append(String.format("%s = ?",
				treeInfo.isParentID() ? fields.getParentID() : fields.getId()));
		DbDataParameter param = DbDataParameter.createParameter(fields
				.getIdType());
		param.setValue(treeInfo.getRootID());
		paramList.add(param);

		sql = sqlBuilder.toString();
		params = new DbDataParameter[paramList.size()];
		params = paramList.toArray(params);
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}

}
