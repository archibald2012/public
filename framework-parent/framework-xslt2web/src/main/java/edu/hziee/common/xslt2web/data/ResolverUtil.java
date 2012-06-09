package edu.hziee.common.xslt2web.data;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.easysearch.CodeTable;
import edu.hziee.common.xslt2web.exception.NoRecordException;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.RegsCollection;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public final class ResolverUtil {
	private ResolverUtil() {
	}

	public static int getPageTable(DataSet dataSet, int totalCount,
			int pageNumber, int pageSize) {
		DataTable page = DataSetUtil.createDataTable("Page", "TotalRows",
				"CurrentPage", "TotalPage", "PageSize");
		int totalPage = (int) Math.ceil(totalCount / (double) pageSize);
		page.getRows().add(
				new Object[] { totalCount, pageNumber, totalPage, pageSize });
		dataSet.getTables().add(page);
		return totalPage;
	}

	public static DataTable getPageInfoTable(int page, int totalPage) {
		if (totalPage == 0)
			return null;
		DataTable pageTable = DataSetUtil.createDataTable("NewPage", "Item");
		int start = ((int) Math.ceil(page / 10.0) - 1) * 10 + 1;
		for (int i = 0; i < 10; ++i) {
			DataRow row = pageTable.newRow();
			row.setItem("Item", start++);
			pageTable.getRows().add(row);
			if (start > totalPage)
				break;
		}

		return pageTable;
	}

	public static void setDefaultValues(AbstractXmlDataAdapter resolver,
			HttpServletRequest request) {
		DataTable table = resolver.getHostTable();
		if (table == null)
			return;
		for (DataRow row : table.getRows())
			for (DataColumn col : table.getColumns()) {
				String value = request.getParameter(col.getColumnName());
				if (!StringUtil.isEmpty(value))
					try {
						row.setItem(col, value);
					} catch (Exception ex) {
					}
			}
	}

	public static void setKeyValues(AbstractXmlDataAdapter resolver,
			HttpServletRequest request, Object key) {
		try {
			if (resolver.getKeyCount() == 1)
				resolver.selectWithKeys(key);
			else {
				Object[] values = new Object[resolver.getKeyCount()];
				int i = 0;
				for (String aKey : resolver.getKeyFieldArray())
					values[i++] = request.getParameter(aKey);
				resolver.selectWithKeys(values);
			}
		} catch (RuntimeException ex) {
			throw ex;
			// throw new InvalidIDException();
		}
		if (resolver.getHostTable().getRows().size() == 0)
			throw new NoRecordException();
	}

	public static void setOldKeyValues(AbstractXmlDataAdapter resolver,
			DataSet postDataSet) {
		DataTable table = postDataSet.getTables().getItem(
				resolver.getTableName());
		if (table == null) {
			resolver.selectTableStructure();
			return;
		}
		for (DataRow srcRow : table.getRows()) {
			if (resolver.getKeyCount() == 1)
				resolver.selectWithKeys(srcRow.getItem("OLD_"
						+ resolver.getKeyFields()));
			else {
				Object[] fieldValues = new String[resolver.getKeyCount()];
				String[] keyFieldArray = resolver.getKeyFieldArray();
				for (int i = 0; i < resolver.getKeyCount(); ++i)
					fieldValues[i] = srcRow.getItem("OLD_" + keyFieldArray[i]);
				resolver.selectWithKeys(fieldValues);
			}
		}
	}

	public static void processRegCodeTables(AbstractXmlTableResolver resolver,
			HashMap<String, CodeTable> codeTables, DbCommand selector,
			PageStyle style) {
		resolver.setRegCodeTables(style);
		for (CodeTable codeTable : resolver.getRegCodeTables()) {
			boolean canCached = codeTable.getAttribute().isUseCache();
			String regName = codeTable.getAttribute().getRegName();
			if (!codeTables.containsKey(regName)) {
				if (resolver.getHostDataSet().getTables().contains(regName))
					continue;

				codeTables.put(regName, codeTable);
				if (canCached) {
					codeTable.select(selector, resolver.getHostDataSet());
					// ICacheHashTable code =
					// GlobalVariable.AppGbl.Caches[AppCachesHashTable.CODE_TABLE_NAME];
					// DataTableCacheItem table =
					// (DataTableCacheItem)code.GetItem(tableName);
					// if (table.IsCreated)
					// {
					// selector.SelectSql(tableName, sql);
					// table.Table = selector.HostTable.Copy();
					// }
					// else
					// selector.HostDataSet.Tables.Add(table.Table.Copy());
				} else
					codeTable.select(selector, resolver.getHostDataSet());
			}
		}
	}

	public static String getDefaultURL(AbstractXmlDataAdapter resolver,
			String saveMethod, String source, PageStyle style,
			String insertPage, String detailPage, String listPage) {
		switch (style) {
		case Insert:
		case Update:
			if ("savenew".equals(saveMethod))
				return String.format("../toolkit/web%spage?Source=%s",
						insertPage, source);
			else if ("save".equals(saveMethod)) {
				DataRow row;
				try {
					row = resolver.getHostTable().getRows().getItem(0);
				} catch (Exception ex) {
					return "";
				}
				if (resolver.getKeyCount() == 1) {
					String value = row.getItem(resolver.getKeyFields())
							.toString();
					return String.format(
							"../toolkit/web%spage?ID=%s&Source=%s", detailPage,
							value, source);
				} else {
					String queryString = "";
					for (String key : resolver.getKeyFieldArray()) {
						queryString += String.format("&%s=%s", key, row
								.getItem(key));
					}
					return "../toolkit/web" + detailPage + "page"
							+ "?ID=-1&Source=" + source + queryString;
				}

			}
		case Delete:
			return String.format("../toolkit/web%spage?Source=%s", listPage,
					source);
		default:
			return "";
		}
	}

	public static String getDefault2URL(AbstractXmlDataAdapter resolver,
			String saveMethod, String source, PageStyle style) {
		return getDefaultURL(resolver, saveMethod, source, style, "insert",
				"detail", "list");
	}

	public static String getDefaultXmlURL(AbstractXmlDataAdapter resolver,
			String saveMethod, String source, PageStyle style) {
		return getDefaultURL(resolver, saveMethod, source, style, "insertxml",
				"detailxml", "listxml");
	}

	public static AbstractXmlDataAdapter newDataAdapter(RegsCollection regs,
			String regName, BaseDataSet dataSet) {
		DataAdapterRegCategory category = (DataAdapterRegCategory) regs
				.get(DataAdapterRegCategory.REG_NAME);
		if (category == null)
			throw new ToolkitException("");
		return category.newArgsInstance(regName, dataSet);
	}

	public static AbstractXmlDataAdapter newDataAdapter(RegsCollection regs,
			ResolverConfigItem item, BaseDataSet dataSet) {
		AbstractXmlDataAdapter result = newDataAdapter(regs, item.getRegName(),
				dataSet);
		if (result instanceof AbstractXmlTableResolver)
			((AbstractXmlTableResolver) result).setRightArgs(item);
		return result;
	}

	public static AbstractXmlTableResolver newTabResolver(
			ResolverConfigItem item, BaseDataSet dataSet) {
		XmlTableResolver result = new XmlTableResolver(dataSet);
		result.setXmlFile(item.getXml());
		result.setRightArgs(item);
		return result;
	}

	public static void getConstraints(UpdateKind status,
			AbstractXmlTableResolver... resolvers) {
		for (AbstractXmlTableResolver resolver : resolvers)
			resolver.getConstraints(status);
	}

	public static void getConstraints(UpdateKind status,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers) {
		getConstraints(status, (AbstractXmlTableResolver[]) resolvers.toArray());
	}

	public static void getConstraints(
			UpdateKind status,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers,
			TableAdapterCollection<AbstractXmlTableResolver>... resolverCollections) {
		getConstraints(status, resolvers.toArray(resolverCollections));
	}
}
