package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.AbstractXmlDataAdapter;
import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.sys.PageStyle;

public class DefaultPageConfigItem {
	private Node configNode;
	private ArrayList<DefaultItemConfigItem> defaultItem;
	private DefaultItemConfigItem[][] searchItems;

	public DefaultPageConfigItem() {
		this.defaultItem = new ArrayList<DefaultItemConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	private DefaultItemConfigItem getDefaultPageItem(PageStyle style,
			boolean isPost) {
		if (searchItems == null)
			return null;
		return searchItems[style.ordinal()][isPost ? 1 : 0];
	}

	private String getIDUrl(AbstractXmlDataAdapter resolver) {
		DataTable table = resolver.getHostTable();
		//		Debug.Assert(table.Rows.Count > 0, 
		//			String.Format("%s表的Row为0，这样无法获得Detail或者Update的地址", table.TableName));
		DataRow row = table.getRows().getItem(table.getRows().size() - 1);
		if (resolver.getKeyCount() == 1)
			return String.format("ID=%s", row.getItem(resolver.getKeyFields()));
		else {
			String queryString = "";
			for (String key : resolver.getKeyFieldArray())
				queryString += String.format("&%s=%s", key, row.getItem(key));
			return "ID=-1" + queryString;
		}
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:DefaultItem".equals(childNode.getNodeName()))
				this.defaultItem.add(DefaultItemConfigItem.parse(childNode));
		}

		searchItems = new DefaultItemConfigItem[ModuleXml.PAGE_STYLE_LENGTH][2];
		for (DefaultItemConfigItem item : this.defaultItem)
			searchItems[item.getSource().ordinal()][item.isPost() ? 1 : 0] = item;
	}

	public String getAlertString(PageStyle style) {
		DefaultItemConfigItem item = getDefaultPageItem(style, true);
		return item == null ? "" : item.getPrompt();
	}

	public String getDefaultPage(AbstractXmlDataAdapter resolver,
			boolean isPost, PageStyle style, String source) {
		DefaultItemConfigItem item = getDefaultPageItem(style, isPost);
		if (item == null)
			return "";
		switch (item.getDest()) {
		case Insert:
			return String.format("../toolkit/webinsertxmlpage?Source=%s",
					source);
		case Update:
			return String.format("../toolkit/webupdatexmlpage?Source=%s&%s",
					source, getIDUrl(resolver));
		case Detail:
			return String.format("../toolkit/webdetailxmlpage?Source=%s&%s",
					source, getIDUrl(resolver));
		case List:
			return String
					.format("../toolkit/weblistxmlpage?Source=%s", source);
		case Custom:
			return item.getCustomValue(resolver.getHostDataSet());
		}
		//Debug.Assert(false, "程序不应该执行到这里。");
		return "";
	}

	public static DefaultPageConfigItem parse(Node node) {
		if (node == null)
			return null;
		DefaultPageConfigItem result = new DefaultPageConfigItem();
		result.readXml(node);
		return result;
	}
}