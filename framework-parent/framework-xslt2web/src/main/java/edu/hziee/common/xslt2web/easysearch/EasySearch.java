package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.LevelConfigItem;
import edu.hziee.common.xslt2web.configxml.MarcoConfigItem;
import edu.hziee.common.xslt2web.configxml.TreeConfigItem;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.provider.BaseLevelSQL;
import edu.hziee.common.xslt2web.provider.ClassicPYParamSearch;
import edu.hziee.common.xslt2web.provider.EqualParamSearch;
import edu.hziee.common.xslt2web.provider.LayerParamSearch;
import edu.hziee.common.xslt2web.provider.Level0SQL;
import edu.hziee.common.xslt2web.provider.LevelParamSearch;
import edu.hziee.common.xslt2web.provider.LevelSQL;
import edu.hziee.common.xslt2web.provider.LevelValueParamSearch;
import edu.hziee.common.xslt2web.provider.LikeParamSearch;
import edu.hziee.common.xslt2web.provider.SimpleParamSearch;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.right.ISupportDataRight;
import edu.hziee.common.xslt2web.sys.CodeSearchType;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.SearchSQLType;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sys.WebDataListener;
import edu.hziee.common.xslt2web.sysutil.SqlUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class EasySearch implements ISupportDataRight {
	public final static int TOP_COUNT = 15;
	private final static String EASY_TABLE_NAME = "EASY_SEARCH";

	private String tableName;
	private String codeField;
	private String nameField;
	private String pYField;
	private String infoField;
	private String otherFields;
	private String defaultOrder;
	private String selectFields;
	private int topCount;
	private IParamSearch search;
	// private ISearch2 search2;
	private boolean isDefaultSearch;

	private DataSet postDataSet;
	private BaseDataSet dataSet;
	private EasySearchAttribute customAttribute;
	private boolean searchCache;
	// private EasySearchCacheHashTable cacheHashTable;

	private SQLParamEventArgs args;
	private RefEventArgs refArgs;
	private MarcoConfigItem sql;

	private EasySearchType type;
	private LevelConfigItem level;
	private BaseLevelSQL levelSQL;
	private TreeConfigItem tree;
	private IParamSearch levelSearch;

	private boolean supportData;
	private DataRightEventArgs dataRightArgs;
	private String ownerField;
	private String rightType;
	private SearchSQLType dataRightType;
	private IWebData webData;
	private ExpressionDataListener dataListener;

	public EasySearch() {
		selectFields = "";
		topCount = EasySearch.TOP_COUNT;
		defaultOrder = "";
		type = EasySearchType.Normal;
		isDefaultSearch = true;
		args = new SQLParamEventArgs(this);
		// fCacheHashTable = (EasySearchCacheHashTable)
		// GlobalVariable.AppGbl.Caches[AppCachesHashTable.EASY_SEARCH_CACHE_NAME];
		dataRightType = SearchSQLType.String;
	}

	public boolean isSupportData() {
		return supportData;
	}

	public void setRightProperties(Object data, DataRightEventArgs e) {
	}

	public void setSupportData(boolean supportData) {
		this.supportData = supportData;
	}

	protected String getSelectFields() {
		if ("".equals(selectFields)) {
			StringBuilder builder = new StringBuilder();
			if ("CODE_VALUE".equals(codeField))
				builder.append(codeField).append(", ");
			else
				builder.append(codeField).append(" CODE_VALUE, ");
			if ("CODE_NAME".equals(nameField))
				builder.append(nameField);
			else
				builder.append(nameField).append(" CODE_NAME");
			if (!StringUtil.isEmpty(pYField))
				builder.append(", ").append(pYField);
			if (!StringUtil.isEmpty(infoField)) {
				if ("CODE_INFO".equals(infoField))
					builder.append(", CODE_INFO");
				else
					builder.append(", ").append(infoField).append(" CODE_INFO");
			}
			if (!StringUtil.isEmpty(otherFields))
				builder.append(", ").append(otherFields);
			if (type == EasySearchType.Tree && tree != null) {
				TreeFieldGroup fields = tree.getTreeFields();
				if ("CODE_PARENT".equals(fields.getParentID()))
					builder.append(", CODE_PARENT");
				else
					builder.append(", ").append(fields.getParentID()).append(
							" CODE_PARENT");
				if ("CODE_IS_LEAF".equals(fields.getIsLeaf()))
					builder.append(", CODE_IS_LEAF");
				else
					builder.append(", ").append(fields.getIsLeaf()).append(
							" CODE_IS_LEAF");
				builder.append(", ").append(fields.getLayer());
			}
			selectFields = builder.toString();
		}
		return selectFields;
	}

	public final String getTableName() {
		return tableName;
	}

	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public final String getCodeField() {
		return codeField;
	}

	public final void setCodeField(String codeField) {
		this.codeField = codeField == null ? null : codeField.trim();
	}

	public final String getNameField() {
		return nameField;
	}

	public final void setNameField(String nameField) {
		this.nameField = nameField == null ? null : nameField.trim();
	}

	public final String getPYField() {
		return pYField;
	}

	public final void setPYField(String field) {
		pYField = field;
	}

	public final String getInfoField() {
		return infoField;
	}

	public final void setInfoField(String infoField) {
		this.infoField = infoField;
	}

	public final String getOtherFields() {
		return otherFields;
	}

	public final void setOtherFields(String otherFields) {
		this.otherFields = otherFields;
	}

	public final String getDefaultOrder() {
		return defaultOrder;
	}

	public final void setDefaultOrder(String defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public final int getTopCount() {
		return topCount;
	}

	public final void setTopCount(int topCount) {
		this.topCount = topCount;
	}

	public final IParamSearch getSearch() {
		return search;
	}

	public final void setSearch(IParamSearch search) {
		this.search = search;
		searchCache = false;
		isDefaultSearch = false;
	}

	public final EasySearchType getType() {
		return type;
	}

	public final void setType(EasySearchType type) {
		this.type = type;
	}

	public final LevelConfigItem getLevel() {
		return level;
	}

	public final void setLevel(LevelConfigItem level) {
		this.level = level;
		if (level != null) {
			// Debug.Assert(fType == CodeTableType.Level || fType ==
			// CodeTableType.Level0,
			// "CodeType必须是Level或者Level0，才能设置LevelConfigItem。");
			if (type == EasySearchType.Level)
				levelSQL = new LevelSQL();
			else
				levelSQL = new Level0SQL();
			levelSQL.prepare(level);
			levelSearch = new LevelParamSearch(levelSQL);
		}
	}

	public final TreeConfigItem getTree() {
		return tree;
	}

	public final void setTree(TreeConfigItem tree) {
		this.tree = tree;
		// if (tree != null)
		// Debug.Assert(fType == CodeTableType.Tree,
		// "CodeType必须是Tree，才能设置TreeConfigItem。");
	}

	public final String getOwnerField() {
		return ownerField;
	}

	public final void setOwnerField(String ownerField) {
		this.ownerField = ownerField;
	}

	public final String getRightType() {
		return rightType;
	}

	public final void setRightType(String rightType) {
		this.rightType = rightType;
	}

	public final IWebData getWebData() {
		return webData;
	}

	final void setWebData(IWebData webData) {
		this.webData = webData;
	}

	public final DataSet getPostDataSet() {
		// Debug.Assert(fPostDataSet != null,
		// "PostDataSet是null，如果是HttpGet，那你是不要指望用的。"
		// + "如果是XmlHttpPost，那么你没有赋值");
		return postDataSet;
	}

	public final BaseDataSet getDataSet() {
		// Debug.Assert(fDataSet != null, "DataSet是null，你就想用，看看你赋值了没有？");
		return dataSet;
	}

	public final ExpressionDataListener getDataListener() {
		if (dataListener == null)
			dataListener = new WebDataListener(webData, getDataSet());
		return dataListener;
	}

	public final DbConnection getDbConnection() {
		return getDataSet().getConnection();
	}

	public EasySearchAttribute getCustomAttribute() {
		if (customAttribute == null) {
			EasySearchAnnotation annotation = this.getClass().getAnnotation(
					EasySearchAnnotation.class);
			if (annotation != null) {
				customAttribute = new EasySearchAttribute();
				customAttribute.setValue(annotation);
			}
			// else
			// Debug.Assert(fDataSet != null, "DataSet是null，你就想用，看看你赋值了没有？");
		}
		return customAttribute;
	}

	public final boolean isSearchCache() {
		return searchCache && getCustomAttribute().isUseCache();
	}

	public final IParamSearch getLevelSearch() {
		return levelSearch;
	}

	public final MarcoConfigItem getSql() {
		return sql;
	}

	public final void setSql(MarcoConfigItem sql) {
		this.sql = sql;
	}

	final RefEventArgs getRefArgs() {
		if (refArgs == null)
			refArgs = new RefEventArgs(this);
		return refArgs;
	}

	final DataRightEventArgs getDataRightArgs() {
		if (dataRightArgs == null)
			dataRightArgs = new DataRightEventArgs(this);
		return dataRightArgs;
	}

	private void setDataSet(BaseDataSet dataSet) {
		this.dataSet = dataSet;
	}

	private void setArgs(String where, EasySearchSQLType sqlType) {
		setArgs(topCount, where, sqlType);
	}

	private void setArgs(int topCount, String where, EasySearchSQLType sqlType) {
		args.setProperties(topCount, getSelectFields(), tableName, where,
				defaultOrder, sqlType, isSearchCache());
		setSQL(args);
		if (sql != null)
			args.setWhere(StringUtil.addSqlCon(args.getWhere(), sql.toString(
					webData.getAppGlobal().getRegsCollection(),
					getDataListener())));
		if (supportData) {
			DataRightEventArgs e = getDataRightArgs();
			e.setProperty(getTableName());
			e.setOwnerField(ownerField);
			e.setRightType(rightType);
			setRightProperties(this, e);
			if (!e.isChecked()) {
				// ISessionGlobal global = GlobalVariable.SessionGbl;
				// IParamBuilder builder =
				// global.Rights.DataRight2.GetPublicSql(e.RightType,
				// e.OwnerField.ToString(), global.Info.UserID);
				// if (builder.Params.Length > 0)
				// {
				// fArgs.AddParams(builder, false);
				// dataRightType = SearchSQLType.Param;
				// }
				// fArgs.Where = StringUtil.AddSqlCon(fArgs.Where, builder.SQL);
			}
		}
	}

	private boolean setData(DbCommand command, DataSet dataSet, String tableName) {
		boolean result = true;
		if (isSearchCache()) {
			try {
				// DataTable table =
				// fCacheHashTable.Select(getCustomAttribute().getRegName(),
				// args);
				// table.setTableName(tableName);
				// dataSet.getTables().add(table);
				// selector.TableName = tableName;
				// selector.HostDataSet.Tables.Add(table);
			} catch (Exception ex) {
				result = false;
			}
		} else
			try {
				switch (args.getSqlType()) {
				case Various:
				case Exist:
				case Info:
					command.selectSql(tableName, args.getSQL(), dataSet, args
							.getParams());
					break;
				case List:
					command.selectSql(tableName, args.getSQL(), dataSet);
					break;
				}
			} catch (Exception ex) {
				result = false;
			}

		return result;
	}

	private Object[] setRefSQL(DataTable refTable, String where) {
		SearchSQLType refType = (this.type == EasySearchType.Tree && tree
				.isOnlyLeafSelect()) ? SearchSQLType.Param
				: SearchSQLType.String;
		if (refTable != null && refTable.getRows().size() > 0) {
			String sql = "";
			RefEventArgs refArgs = getRefArgs();
			for (DataRow row : refTable.getRows()) {
				refArgs.setProperties(TypeCode.String, row.getItem("Field")
						.toString(), row.getItem("RefValue").toString());
				setRefSQL(refArgs);
				switch (refArgs.getType()) {
				case Param:
					if (!StringUtil.isEmpty(refArgs.getBuilder().getSQL())) {
						sql += " AND " + refArgs.getBuilder().getSQL();
						refType = SearchSQLType.Param;
						args.addParams(refArgs.getBuilder(), false);
					}
					break;
				case String:
					if (!StringUtil.isEmpty(refArgs.getSQL()))
						sql += " AND " + refArgs.getSQL();
					break;
				}
			}
			where += ("".equals(where)) ? ("".equals(sql)) ? "" : sql
					.substring(4) : sql;
		}
		return new Object[] { refType, where };
	}

	private String getWhereSQL(IParamSearch search, SQLParamEventArgs args,
			String fieldName, String value) {
		return getWhereSQL(search, TypeCode.String, args, fieldName, value);
	}

	private String getWhereSQL(IParamSearch search, TypeCode type,
			SQLParamEventArgs args, String fieldName, String value) {
		args.clear();
		switch (search.getType()) {
		case Param:
			IParamBuilder builder = search.getParamCondition(type, fieldName,
					value);
			args.addParams(builder);
			return builder.getSQL();
		case String:
			return search.getCondition(fieldName, value);
		}
		// Debug.Assert(false, "程序不可能执行到这里！");
		return "";
	}

	private int fillDataSet(IParamSearch search, SQLParamEventArgs args,
			SearchSQLType refType) {
		return fillDataSet(EASY_TABLE_NAME, search, args, refType);
	}

	private int fillDataSet(String tableName, IParamSearch search,
			SQLParamEventArgs args, SearchSQLType refType) {
		return fillDataSet(tableName, search, args, refType, true);
	}

	private int fillDataSet(String tableName, IParamSearch search,
			SQLParamEventArgs args, SearchSQLType refType, boolean removeTable) {
		if (removeTable && dataSet.getTables().contains(tableName))
			dataSet.getTables().remove(tableName);

		if (isSearchCache()) {
			// DataTable table = fCacheHashTable.Select(CustomAttribute.RegName,
			// fArgs);
			// table.setTableName(tableName);
			// dataSet.getTables().add(table);
			// return table.getRows().size();
			return 0;
		} else {
			DbCommand command = dataSet.getConnection().createCommand();
			int refValue = search.getType().ordinal() | refType.ordinal()
					| dataRightType.ordinal();

			if (refValue == SearchSQLType.Param.ordinal())
				command.selectSql(tableName, args.getSQL(), dataSet, args
						.getParams());
			else if (refValue == SearchSQLType.String.ordinal())
				command.selectSql(tableName, args.getSQL(), dataSet);
			return dataSet.getTables().getItem(tableName).getRows().size();
		}
	}

	private String getLevelValue(int level, String value) {
		if ("".equals(value) || this.type == EasySearchType.Level)
			return value;
		int len = this.level.getSubItem().get(level).getSubTotalLength();
		len = Math.max(len, value.length());
		value = StringUtil.padRight(value, len, '0');

		return value;
	}

	protected final String getSearchCondition(IParamSearch search,
			TypeCode type, String fieldName, String value) {
		return getWhereSQL(search, type, args, fieldName, value);
	}

	protected final String getSearchCondition(IParamSearch search,
			String fieldName, String value) {
		return getWhereSQL(search, args, fieldName, value);
	}

	final void setDataSet(BaseDataSet dataSet, DataSet postDataSet) {
		setDataSet(dataSet);
		this.postDataSet = postDataSet;
	}

	protected String getEmptySearch(CodeSearchType codeType, String value) {
		if (StringUtil.isEmpty(pYField)) {
			switch (codeType) {
			case CodeValue:
				if (type == EasySearchType.Level
						|| type == EasySearchType.Level0) {
					search = levelSearch;
					return getSearchCondition(search, codeField, value);
				} else {
					search = LikeParamSearch.Search;
					return getSearchCondition(search, nameField, value);
				}
			case CodeName:
				search = SimpleParamSearch.Search;
				return getSearchCondition(search, nameField, value);
			case CodePY:
				search = ClassicPYParamSearch.Search;
				return getSearchCondition(search, nameField, value);
			}
		} else {
			switch (codeType) {
			case CodeValue:
				if (type == EasySearchType.Level
						|| type == EasySearchType.Level0) {
					search = levelSearch;
					return getSearchCondition(search, codeField, value);
				} else {
					search = LikeParamSearch.Search;
					return getSearchCondition(search, nameField, value);
				}
			case CodeName:
				search = SimpleParamSearch.Search;
				return getSearchCondition(search, nameField, value);
			case CodePY:
				search = LikeParamSearch.Search;
				return getSearchCondition(search, pYField, value);
			}
		}

		throw new ToolkitException("猪啊，你的代码居然会执行到这里。");
	}

	protected void setSQL(SQLParamEventArgs e) {
	}

	protected void setRefSQL(RefEventArgs e) {
	}

	public final void setQueryDataSet(DataTable table, DataRow row,
			String fieldName) {
		String hdName = "hd" + fieldName;
		if (table.getColumns().contains(hdName)) {
			row.setItem(fieldName, row.getItem(hdName));
			table.getColumns().remove(hdName);
		}
	}

	public int levelDialogSearch(String value) {
		if (this.type == EasySearchType.Level0)
			value = StringUtil.trimEnd(value, '0');
		int level = this.level.getLevel(value);
		value = getLevelValue(level, value);
		String where = "";
		if (isDefaultSearch) {
			where = getEmptySearch(CodeSearchType.CodeValue, value);
			isDefaultSearch = true;
		} else
			where = getWhereSQL(search, args, codeField, value);
		topCount = 0;
		setArgs(where, EasySearchSQLType.FastSearch);

		return fillDataSet(tableName, search, args, SearchSQLType.Param);
	}

	public int treeDialogSearch(String value) {
		String where = "";
		ITreeFieldGroup treeInfo = this.getTree();
		TreeFieldGroup fields = treeInfo.getTreeFields();
		if (isDefaultSearch) {
			search = EqualParamSearch.Search;
			if (value == null) {
				this.tree.setBaseValueData(webData.getAppGlobal()
						.getRegsCollection(), getDataListener());
				if (treeInfo.isParentID())
					where = getSearchCondition(search, fields.getIdType(),
							fields.getParentID(), treeInfo.getRootID());
				else
					where = getSearchCondition(search, fields.getIdType(),
							fields.getId(), treeInfo.getRootID());
			} else
				where = getSearchCondition(search, fields.getIdType(), fields
						.getParentID(), value);
			isDefaultSearch = true;
		} else
			where = getWhereSQL(search, args, codeField, value);
		topCount = 0;
		setArgs(where, EasySearchSQLType.FastSearch);

		return fillDataSet(tableName, search, args, SearchSQLType.Param);
	}

	public int setLevelValue(String value, DataTable refTable) {
		if (this.type == EasySearchType.Level0)
			value = StringUtil.trimEnd(value, '0');
		int level = this.level.getLevel(value);
		value = getLevelValue(level, value);
		String where = "";
		if (isDefaultSearch) {
			search = new LevelValueParamSearch(this.levelSQL, level);
			where = getSearchCondition(search, codeField, value);
			isDefaultSearch = true;
		} else
			where = getWhereSQL(search, args, codeField, value);
		topCount = 0;
		setArgs(where, EasySearchSQLType.FastSearch);

		return fillDataSet(tableName, search, args, SearchSQLType.Param);
	}

	public int setTreeValue(String value, DataTable refTable) {
		String where = "";
		this.tree.setBaseValueData(webData.getAppGlobal().getRegsCollection(),
				getDataListener());
		if (isDefaultSearch) {
			search = new LayerParamSearch(this.tree, this.getDbConnection());
			where = getSearchCondition(search, codeField, value);
			isDefaultSearch = true;
		} else
			where = getWhereSQL(search, args, codeField, value);
		topCount = 0;
		setArgs(where, EasySearchSQLType.FastSearch);

		return fillDataSet(tableName, search, args, SearchSQLType.Param);
	}

	// private int internalLevelDialogSearch(int level, String value,
	// DataTable refTable, String tableName) {
	// args.clear();
	// String where = "";
	// if (isDefaultSearch) {
	// where = getEmptySearch(CodeSearchType.CodeValue, value);
	// isDefaultSearch = true;
	// } else
	// where = getWhereSQL(search, args, codeField, value);
	//
	// Object[] data = setRefSQL(refTable, where);
	// SearchSQLType refType = (SearchSQLType) data[0];
	// where = (String) data[1];
	//
	// topCount = 0;
	// setArgs(where, EasySearchSQLType.FastSearch);
	//
	// return fillDataSet(tableName, search, args, refType);
	// }
	//
	// public final int levelDialogSearch(int level, String value,
	// DataTable refTable) {
	// // Debug.Assert(fType == CodeTableType.Level || fType ==
	// // CodeTableType.Level0, "类型必须是Level或者Level0类型");
	// DataTable addTable = DataSetUtil.createDataTable("_TREE", "ISLEAF",
	// "LEVEL");
	// addTable.getRows().add(
	// new Object[] {
	// level == this.level.getSubItem().size() - 1 ? 1 : 0,
	// this.level.getSubItem().size() });
	// dataSet.getTables().add(addTable);
	//
	// value = getLevelValue(level, value);
	//
	// return internalLevelDialogSearch(level, value, refTable,
	// EASY_TABLE_NAME);
	// }
	//
	// public final int treeDialogSearch(String value, DataTable refTable) {
	// // Debug.Assert(fType == EasySearchType.Tree, "类型必须是Tree类型");
	// args.clear();
	// String baseValue = ""; // tree.getBaseValue();
	// DataTable addTable = DataSetUtil.createDataTable("_TREE",
	// "ONLY_SELECT_LEFT", "BASE_VALUE", "BASE_FIELD");
	// addTable.getRows().add(
	// new Object[] { tree.isOnlyLeafSelect() ? 1 : 0, baseValue,
	// tree.isParentID() });
	// dataSet.getTables().add(addTable);
	//
	// String where = "";
	// String searchField;
	// if ("".equals(value)) {
	// // 根节点
	// value = baseValue;
	// searchField = tree.getBaseSearchField();
	// } else {
	// // 非根节点
	// searchField = tree.getTreeFields().getParentID();
	// }
	//
	// if (isDefaultSearch) {
	// search = EqualParamSearch.Search;
	// where = getSearchCondition(search, searchField, value);
	// } else
	// where = getWhereSQL(search, args, searchField, value);
	//
	// Object[] data = setRefSQL(refTable, where);
	// SearchSQLType refType = (SearchSQLType) data[0];
	// where = (String) data[1];
	//
	// topCount = 0;
	// setArgs(where, EasySearchSQLType.FastSearch);
	//
	// return fillDataSet(EASY_TABLE_NAME, search, args, refType);
	// }

	public final int fastSearch(String value, DataTable refTable) {
		args.clear();
		String where = "";
		if (!StringUtil.isEmpty(value)) {
			if (searchCache) {
			} else {
				if (isDefaultSearch) {
					CodeSearchType codeType = SqlUtil.parseSearchValue(value);
					where = getEmptySearch(codeType, value);
					isDefaultSearch = true;
				} else
					where = getWhereSQL(search, args, nameField, value);
			}
		} else if (type == EasySearchType.Level
				|| type == EasySearchType.Level0) {
			if (isDefaultSearch) {
				where = getEmptySearch(CodeSearchType.CodeValue, "");
				isDefaultSearch = true;
			} else
				where = getWhereSQL(search, args, nameField, value);
		} else if (isDefaultSearch)
			search = SimpleParamSearch.Search;

		if (type == EasySearchType.Tree && tree.isOnlyLeafSelect()) {
			IParamBuilder builder = SQLParamBuilder.getEqualSQL(TypeCode.Int16,
					tree.getTreeFields().getIsLeaf(), "1");
			where = StringUtil.addSqlCon(where, builder.getSQL());
			args.addParams(builder, false);
		}

		Object[] data = setRefSQL(refTable, where);
		SearchSQLType refType = (SearchSQLType) data[0];
		where = (String) data[1];

		setArgs(where, EasySearchSQLType.FastSearch);

		return fillDataSet(search, args, refType);
	}

	public final boolean setVariousData(DbCommand command, DataSet dataSet,
			String tableName, String value) {
		args.clear();
		String where;
		if (isSearchCache()) {
			where = String.format("CODE_NAME = '%s'", value);
		} else {
			IParamBuilder builder = SQLParamBuilder.getEqualSQL(
					TypeCode.String, nameField, value);
			args.addParams(builder);
			where = builder.getSQL();
		}
		setArgs(where, EasySearchSQLType.Various);
		return setData(command, dataSet, tableName);
	}

	public final boolean setExistData(DbCommand command, DataSet dataSet,
			String tableName, String value, String valueID) {
		args.clear();
		String where;
		if (isSearchCache()) {
			where = String.format("CODE_VALUE = '%s' AND CODE_NAME = '%s'",
					valueID, value);
		} else {
			IParamBuilder builder = SQLParamBuilder.getCompositeSQL(
					"%s = %s AND %s = %s", new TypeCode[] { TypeCode.String,
							TypeCode.String }, new String[] { codeField,
							nameField }, valueID, value);
			args.addParams(builder);
			where = builder.getSQL();
		}

		setArgs(where, EasySearchSQLType.Exist);
		return setData(command, dataSet, tableName);
	}

	public final boolean setInfoData(DbCommand command, DataSet dataSet,
			String tableName, String value) {
		args.clear();
		String where;
		if (isSearchCache()) {
			where = String.format("CODE_VALUE = '%s'", value);
		} else {
			IParamBuilder builder = SQLParamBuilder.getEqualSQL(
					TypeCode.String, codeField, value);
			args.addParams(builder);
			where = builder.getSQL();
		}
		setArgs(where, EasySearchSQLType.Info);
		return setData(command, dataSet, tableName);
	}

	public final void setListData(DbCommand command, DataSet dataSet,
			String tableName, String inList) {
		args.clear();
		String codeValue = (isSearchCache()) ? "CODE_VALUE" : codeField;
		setArgs(0, String.format("%s IN (%s)", codeValue, inList),
				EasySearchSQLType.List);
		setData(command, dataSet, tableName);
	}
}
