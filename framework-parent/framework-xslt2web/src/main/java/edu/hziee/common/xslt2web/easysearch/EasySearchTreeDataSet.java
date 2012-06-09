package edu.hziee.common.xslt2web.easysearch;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.configxml.LevelConfigItem;
import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.data.TreeUtil;
import edu.hziee.common.xslt2web.data.TreeXmlHelper;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.data.WebBaseDataSet;
import edu.hziee.common.xslt2web.easysearch.EasySearch;
import edu.hziee.common.xslt2web.easysearch.EasySearchType;
import edu.hziee.common.xslt2web.easysearch.EasySearchUtil;
import edu.hziee.common.xslt2web.sys.IHttpGetPage;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class EasySearchTreeDataSet extends WebBaseDataSet implements
		IHttpGetPage {
	private EasySearch item;
	private String rootID;

	public EasySearchTreeDataSet() {
	}

	private void calcTable() {
		DataTable table = this.getTables().getItem(item.getTableName());
		if (!table.getColumns().contains("CODE_PARENT"))
			table.getColumns().add("CODE_PARENT");
		if (!table.getColumns().contains("CODE_IS_LEAF"))
			table.getColumns().add("CODE_IS_LEAF", TypeCode.Int16);
		for (DataRow row : table.getRows()) {
			String value = row.getItem("CODE_VALUE").toString();
			LevelConfigItem levelItem = item.getLevel();
			if (item.getType() == EasySearchType.Level0)
				value = StringUtil.trimEnd(value, '0');
			int level = levelItem.getLevel(value);
			row.beginEdit();
			String parentValue = levelItem.getParentValue(level, value);
			if (!"0".equals(parentValue)
					&& item.getType() == EasySearchType.Level0)
				parentValue = StringUtil.padRight(parentValue, levelItem
						.getTotalLength(), '0');
			row.setItem("CODE_PARENT", parentValue);
			row.setItem("CODE_IS_LEAF", item.getLevel().isLeaf(level));
			row.endEdit();
		}
	}

	private String getRootID() {
		String rootId;
		ITreeFieldGroup treeInfo = item.getTree();
		TreeFieldGroup group = treeInfo.getTreeFields();
		IParamBuilder builder = TreeUtil.getIdParamBuilder(group,
				group.getId(), treeInfo.getRootID());
		rootId = DataSetUtil.executeScalar(
				this.getConnection(),
				String.format("SELECT %s FROM %s WHERE %s",
						group.getParentID(), treeInfo.getTableName(), builder
								.getSQL()), builder.getParams()).toString();
		return rootId;
	}

	public void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request) {
		String type = request.getParameter("Type");
		String parentID = request.getParameter("ID");
		item = EasySearchUtil.getRegEasySearch(this, type);
		item.setDataSet(this, null);
		EasySearchType codeType = item.getType();
		if (parentID == null) {
			String exID = request.getParameter("ExID");
			switch (codeType) {
			case Level:
			case Level0:
				rootID = "0";
				if (StringUtil.isEmpty(exID))
					item.levelDialogSearch("");
				else
					item.setLevelValue(exID, null);
				break;
			case Tree:
				ITreeFieldGroup treeInfo = item.getTree();
				if (StringUtil.isEmpty(exID))
					item.treeDialogSearch(parentID);
				else
					item.setTreeValue(exID, null);
				if (treeInfo.isParentID())
					rootID = treeInfo.getRootID();
				else
					rootID = getRootID();
				break;
			}
		} else {
			rootID = parentID;
			switch (codeType) {
			case Level:
			case Level0:
				item.levelDialogSearch(parentID);
				break;
			case Tree:
				item.treeDialogSearch(parentID);
				break;
			}
		}
		switch (codeType) {
		case Level:
		case Level0:
			calcTable();
			break;
		}
	}

	@Override
	public String getXmlString() {
		TreeXmlHelper xmlHelper = new TreeXmlHelper("CODE_VALUE", "CODE_NAME",
				"CODE_PARENT", "CODE_IS_LEAF");
		xmlHelper.addDataTable(this.getTables().getItem(item.getTableName()));
		return xmlHelper.toXml(rootID);
	}

}
