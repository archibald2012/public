package edu.hziee.common.xslt2web.web;

import yjc.toolkit.data.DataAdapterRegCategory;
import yjc.toolkit.data.DataSetRegCategory;
import yjc.toolkit.data.XmlDataSetRegCategory;
import yjc.toolkit.easysearch.CodeTableRegCategory;
import yjc.toolkit.easysearch.EasySearchRegCategory;
import yjc.toolkit.provider.DbProviderRegCategory;
import yjc.toolkit.provider.LoginOrgIDExpression;
import yjc.toolkit.provider.LoginUserIDExpression;
import yjc.toolkit.provider.QueryStringParamExpression;
import yjc.toolkit.provider.SqlProviderRegCategory;
import yjc.toolkit.provider.UniIDParamExpression;
import yjc.toolkit.sys.ExpressionRegCategory;
import yjc.toolkit.sys.ParamExpressionRegCategory;
import yjc.toolkit.sys.RegsCollection;

public class StandardRegsCollection extends RegsCollection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StandardRegsCollection() {
		this.addRegCategory(new DataSetRegCategory());
		this.addRegCategory(new DataAdapterRegCategory());
		this.addRegCategory(new WebPageRegCategory());
		this.addRegCategory(new DbProviderRegCategory());
		this.addRegCategory(new SqlProviderRegCategory());
		this.addRegCategory(new XmlDataSetRegCategory());
		this.addRegCategory(new CodeTableRegCategory());
		this.addRegCategory(new EasySearchRegCategory());

		ExpressionRegCategory expression = new ExpressionRegCategory();
		expression.addRegClass(LoginUserIDExpression.class);
		expression.addRegClass(LoginOrgIDExpression.class);
		this.addRegCategory(expression);

		ParamExpressionRegCategory paramExpression = new ParamExpressionRegCategory();
		paramExpression.addRegClass(QueryStringParamExpression.class);
		paramExpression.addRegClass(UniIDParamExpression.class);
		this.addRegCategory(paramExpression);
	}
}
