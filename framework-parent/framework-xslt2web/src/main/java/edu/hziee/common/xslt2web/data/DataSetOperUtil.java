package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.configxml.ModuleConfigItem;
import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.provider.GlobalProvider;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;
import edu.hziee.common.xslt2web.sysutil.XPathUtil;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

final class DataSetOperUtil {
	private DataSetOperUtil() {
	}

	static HashMap<String, String> getIngoreParams() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("RETURL", null);
		result.put("PAGE", null);
		result.put("SORT", null);
		result.put("ORDER", null);
		result.put("CCHAR", null);
		result.put("ID", null);
		result.put("CONDITION", null);
		result.put("COUNT", null);
		result.put("TAB", null);

		return result;
	}

	static String getDefaultXsltTemplate(PageStyle style, String operation) {
		switch (style) {
		case Insert:
			return "..\\Tk2Template\\InsertTemplate.xslt";
		case Update:
			return "..\\Tk2Template\\UpdateTemplate.xslt";
		case Detail:
			return "..\\Tk2Template\\DetailTemplate.xslt";
		case List:
			return "..\\Tk2Template\\ListTemplate.xslt";
		}
		return "";
	}

	static String getDataXslt(PageStyle style, String operation) {
		String xsltFile = "";
		switch (style) {
		case Insert:
			xsltFile = "Tk2Template\\InsertTrans.xslt";
			break;
		case Update:
			xsltFile = "Tk2Template\\UpdateTrans.xslt";
			break;
		case Detail:
			xsltFile = "Tk2Template\\DetailTrans.xslt";
			break;
		case List:
			xsltFile = "Tk2Template\\ListTrans.xslt";
			break;
		}
		return xsltFile;
	}

	static Object[] SetListData(DataSet dataSet, boolean isPost,
			HttpServletRequest request, DataSet postDataSet,
			AbstractXmlTableResolver listView, int sortField,
			String defaultOrder, String orderField, boolean sortQuery) {
		String queryCondition;
		QueryParamCondition condition;
		int count = -1;
		int pageNumber = 1;
		String order = "";
		String tab = "";

		DataTable sortTable = DataSetUtil.createDataTable("Sort", "SortField",
				"Order", "CChar", "SqlCon", "Count", "Tab");
		DataRow sortRow = sortTable.newRow();
		sortTable.getRows().add(sortRow);

		String sqlCon = "";
		tab = request.getParameter("Tab");
		if (isPost) {
			sortRow.beginEdit();
			sortRow.setItem("SortField", sortField);
			String orderParam = "";
			if (sortQuery) {
				orderParam = StringUtil.getDefaultStr(
						request.getParameter("Order")).toUpperCase();
				if (!"ASC".equals(orderParam) && !"DESC".equals(orderParam))
					orderParam = "";
				if (sortField != 0)
					order = String.format("ORDER BY %s %s", orderField,
							orderParam);
				orderParam = "DESC".equals(orderParam) ? "ASC" : "DESC";
			} else
				order = "";
			sortRow.setItem("Order", orderParam);
			DataTable table = postDataSet.getTables().getItem(
					listView.getTableName());
			if (dataSet.getTables().contains("_CONDITION"))
				dataSet.getTables().remove("_CONDITION");
			DataTable conditionTable = table.copy();
			conditionTable.setTableName("_CONDITION");
			dataSet.getTables().add(conditionTable);
			condition = listView.getQueryCondition(table);
			String guid = postDataSet.getTables().getItem("OtherInfo")
					.getRows().getItem(0).getItem("DataSet").toString();
			queryCondition = guid;
			ListQueryCondition.setCondition(request, guid, condition);
			sortRow.setItem("SqlCon", guid);

			sortRow.setItem("Tab", tab);
			sortRow.endEdit();
		} else {
			// String sort = StringUtil
			// .getDefaultStr(request.getParameter("Sort"));
			String orderParam = StringUtil.getDefaultStr(
					request.getParameter("Order")).toUpperCase();
			if (!"ASC".equals(orderParam) && !"DESC".equals(orderParam))
				orderParam = defaultOrder;
			String sqlChar = StringUtil.getDefaultStr(
					request.getParameter("cChar")).trim();

			queryCondition = StringUtil.getDefaultStr(
					request.getParameter("condition")).trim();
			condition = ListQueryCondition
					.getCondition(request, queryCondition);
			if (sortField != 0)
				order = String.format("ORDER BY %s %s", orderField, orderParam);
			count = condition.isEmpty() ? -1 : StringUtil.getDefaultInt(request
					.getParameter("Count"), -1);
			pageNumber = StringUtil.getDefaultInt(request.getParameter("Page"),
					1);

			sortRow.beginEdit();
			sortRow.setItem("SortField", sortField);
			sortRow
					.setItem("Order", "DESC".equals(orderParam) ? "ASC"
							: "DESC");
			sortRow.setItem("CChar", sqlChar);
			sortRow.setItem("SqlCon", sqlCon);
			sortRow.setItem("Tab", tab);
			sortRow.endEdit();
		}
		dataSet.getTables().add(sortTable);
		return new Object[] { sortRow, queryCondition, condition, count,
				pageNumber, order, tab };
	}

	public static int fillListTable(BaseDataSet dataSet,
			AbstractXmlDataAdapter listView, QueryParamCondition sqlCon,
			String order, int pageNumber, int pageSize, int count,
			String listFields) {
		int totalCount = count;
		DbDataParameter[] parameters = sqlCon.getParams();
		String whereSQL = StringUtil.getSqlCon("", sqlCon.getSql());
		if (count == -1) {
			totalCount = 0;
			try {
				totalCount = Integer.parseInt(DataSetUtil.executeScalar(
						dataSet.getConnection(),
						String.format("SELECT COUNT(*) FROM %s %s", listView
								.getTableName(), whereSQL), parameters)
						.toString());
			} catch (Exception ex) {
			}
		}

		if (totalCount != 0) {
			int recCount = pageSize * pageNumber;
			String strSql = GlobalProvider.getSqlProvider().getListSql(
					listFields, listView.getTableName(), whereSQL,
					listView.getKeyFieldInfos(), order, recCount - pageSize,
					recCount);

			listView.setCommands(EnumSet.of(AdapterCommand.Select));
			listView.getSelectCommand().setCommandText(strSql);
			DbParameterCollection collection = listView.getSelectCommand()
					.getParameters();
			collection.clear();
			for (DbDataParameter param : parameters)
				collection.add(param);

			GlobalProvider.getSqlProvider().setListData(
					listView.getSelectCommand(), dataSet, recCount - pageSize,
					pageSize, listView.getTableName());
			listView.addVirtualFields();
		}

		int totalPage = ResolverUtil.getPageTable(dataSet, totalCount,
				pageNumber, pageSize);
		DataTable table = ResolverUtil.getPageInfoTable(pageNumber, totalPage);
		if (table != null)
			dataSet.getTables().add(table);

		return totalCount;
	}

	private static XPathExpression orderFieldExpr;
	private static XPathExpression fieldNameExpr;
	private static XPathExpression sortFieldExpr;
	private static XPathExpression listFieldExpr;

	public static String[] getOrderFields(Node node) {
		if (orderFieldExpr == null)
			orderFieldExpr = XPathUtil
					.getXPathExpression("/Toolkit/tk:Table/List/tk:Field");
		NodeList nodes = XPathUtil.executeNodes(node, orderFieldExpr);
		int count = nodes.getLength();

		String[] orderFieldNames = new String[count];
		if (fieldNameExpr == null)
			fieldNameExpr = XPathUtil.getXPathExpression("tk:FieldName");
		if (sortFieldExpr == null)
			sortFieldExpr = XPathUtil
					.getXPathExpression("tk:Extension/@SortField");
		for (int i = 0; i < count; i++) {
			Node fieldNode = nodes.item(i);
			String sortField = XmlUtil.getNodeContent(XPathUtil
					.executeSingleNode(fieldNode, sortFieldExpr));
			if (!"".equals(sortField))
				orderFieldNames[i] = sortField;
			else {
				String fieldName = XmlUtil.getNodeContent(XPathUtil
						.executeSingleNode(fieldNode, fieldNameExpr));
				orderFieldNames[i] = fieldName;
			}
		}
		return orderFieldNames;
	}

	public static String getListFields(Node node) {
		if (listFieldExpr == null)
			listFieldExpr = XPathUtil
					.getXPathExpression("/Toolkit/tk:Table/*/tk:Field[@Kind='Data']");
		if (fieldNameExpr == null)
			fieldNameExpr = XPathUtil.getXPathExpression("tk:FieldName");
		NodeList nodes = XPathUtil.executeNodes(node, listFieldExpr);
		StringBuilder builder = new StringBuilder();
		int count = nodes.getLength();
		for (int i = 0; i < count; i++) {
			Node fieldNode = nodes.item(i);
			String fieldName = XmlUtil.getNodeContent(XPathUtil
					.executeSingleNode(fieldNode, fieldNameExpr));
			if (i > 0)
				builder.append(", ");
			builder.append(fieldName);
		}
		return builder.toString();
	}

	public static void setDataSetProperty(WebUpdateDataSet dataSet,
			ModuleXml moduleXml) {
		ModuleConfigItem module = moduleXml.getModule();
		dataSet.setDefaultOrder(module.getDefaultOrder());
		dataSet.setDefaultSortField(module.getDefaultSortField());
		dataSet.setDisablePage(module.getDisablePage());
		dataSet.setFuncType(module.getFunctionRight().getFuncType());
		dataSet.setFunctionKey(module.getFunctionRight().getFunctionKey());
		dataSet.setFilterSQL(module.getFilterSQL());
		dataSet.setSupportData(module.isSupportData());
		dataSet.setSortQuery(module.isSortQuery());
		dataSet.setPageSize(module.getPageSize());
	}

	public static void setDataSetProperty(WebListDataSet dataSet,
			ModuleXml moduleXml) {
		ModuleConfigItem module = moduleXml.getModule();
		dataSet.setDefaultOrder(module.getDefaultOrder());
		dataSet.setDefaultSortField(module.getDefaultSortField());
		dataSet.setFuncType(module.getFunctionRight().getFuncType());
		dataSet.setFunctionKey(module.getFunctionRight().getFunctionKey());
		dataSet.setFilterSQL(module.getFilterSQL());
		dataSet.setSupportData(module.isSupportData());
		dataSet.setSortQuery(module.isSortQuery());
		dataSet.setPageSize(module.getPageSize());
	}
}
