package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.LayerParamBuilder;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class LayerParamSearch implements IParamSearch {
	private ITreeFieldGroup treeInfo;
	private DbConnection connection;
	
	public LayerParamSearch(ITreeFieldGroup treeInfo, DbConnection connection) {
		super();
		this.treeInfo = treeInfo;
		this.connection = connection;
	}

	public String getCondition(String fieldName, String fieldValue) {
		return null;
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return new LayerParamBuilder(treeInfo, connection, fieldValue);
	}

	public SearchSQLType getType() {
		return SearchSQLType.Param;
	}

}
