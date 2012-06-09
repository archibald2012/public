package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;
import java.util.HashMap;

import edu.hziee.common.xslt2web.sysutil.DataSetUtil;

public class TreeXmlHelper {
	private HashMap<String, ArrayList<DataRow>> treeData;
	private String idField;
	private String nameField;
	private String parentIDField;
	private String isLeafField;

	public TreeXmlHelper(String idField, String nameField,
			String parentIDField, String isLeafField) {

		treeData = new HashMap<String, ArrayList<DataRow>>();
		this.idField = idField;
		this.nameField = nameField;
		this.parentIDField = parentIDField;
		this.isLeafField = isLeafField;
	}

	private final void addRow(DataRow row) {
		String id = row.getItem(parentIDField).toString();
		ArrayList<DataRow> list;
		if (treeData.containsKey(id))
			list = treeData.get(id);
		else {
			list = new ArrayList<DataRow>();
			treeData.put(id, list);
		}
		list.add(row);
	}

	public final void addDataTable(DataTable table) {
		for (DataRow row : table.getRows())
			addRow(row);
	}

	private void appendXml(StringBuilder builder, String rootId) {
		ArrayList<DataRow> list = treeData.get(rootId);
		if (list == null)
			return;
		for (DataRow row : list) {
			String id = row.getItem(idField).toString();
			builder.append(String
					.format("  <item child='%s' id='%s' text='%s'", 1 - Integer
							.parseInt(row.getItem(isLeafField).toString()), id,
							DataSetUtil.escapeString(row.getItem(nameField)
									.toString())));
			if (treeData.containsKey(id)) {
				builder.append(">\r\n");
				appendXml(builder, id);
				builder.append("  </item>\r\n");
			} else {
				builder.append("/>\r\n");
			}
		}
	}

	public final String toXml(String rootID) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("<tree id='%s'>\r\n", rootID));
		appendXml(builder, rootID);
		builder.append("</tree>\r\n");
		return builder.toString();
	}

}
