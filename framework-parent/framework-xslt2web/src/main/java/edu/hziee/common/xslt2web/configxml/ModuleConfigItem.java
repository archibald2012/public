package edu.hziee.common.xslt2web.configxml;

import java.util.EnumSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.DisableFunction;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class ModuleConfigItem {
	private Node configNode;
	private boolean httpPost;
	private boolean supportData;
	private boolean prompt;
	private boolean sortQuery;
	private String title;
	private String dataSet;
	private int pageSize;
	private TabSheetsConfigItem tabSheets;
	private MarcoConfigItem filterSQL;
	private EnumSet<DisableFunction> disablePage;
	private String defaultOrder = "";
	private int defaultSortField;
	private FunctionRightConfigItem functionRight;
	private ResolversConfigItem resolvers;
	private RelationsConfigItem relations;
	private XsltFilesConfigItem xsltFiles;
	private DefaultPageConfigItem defaultPage;

	public ModuleConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isHttpPost() {
		return this.httpPost;
	}

	public final boolean isSupportData() {
		return this.supportData;
	}

	public final boolean isPrompt() {
		return this.prompt;
	}

	public final boolean isSortQuery() {
		return this.sortQuery;
	}

	public final String getTitle() {
		return this.title;
	}

	public final String getDataSet() {
		return this.dataSet;
	}

	public final TabSheetsConfigItem getTabSheets() {
		return this.tabSheets;
	}

	public final MarcoConfigItem getFilterSQL() {
		return this.filterSQL;
	}

	public final EnumSet<DisableFunction> getDisablePage() {
		return this.disablePage;
	}

	public final String getDefaultOrder() {
		return this.defaultOrder;
	}

	public final int getDefaultSortField() {
		return this.defaultSortField;
	}

	public final FunctionRightConfigItem getFunctionRight() {
		return this.functionRight;
	}

	public final ResolversConfigItem getResolvers() {
		return this.resolvers;
	}

	public final RelationsConfigItem getRelations() {
		return this.relations;
	}

	public final XsltFilesConfigItem getXsltFiles() {
		return this.xsltFiles;
	}

	public final DefaultPageConfigItem getDefaultPage() {
		return this.defaultPage;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.httpPost = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("HttpPost"), false);
		this.supportData = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("SupportData"), false);
		this.prompt = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("Prompt"), false);
		this.sortQuery = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("SortQuery"), false);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:Title".equals(childNode.getNodeName()))
				this.title = childNode.getTextContent();
			else if ("tk:DataSet".equals(childNode.getNodeName()))
				this.dataSet = childNode.getTextContent();
			else if ("tk:PageSize".equals(childNode.getNodeName()))
				this.pageSize = XmlUtil.getNodeIntValue(childNode, 0);
			else if ("tk:TabSheets".equals(childNode.getNodeName()))
				this.tabSheets = TabSheetsConfigItem.parse(childNode);
			else if ("tk:FilterSQL".equals(childNode.getNodeName()))
				this.filterSQL = MarcoConfigItem.parse(childNode);
			else if ("tk:DisablePage".equals(childNode.getNodeName())) {
				String disable = XmlUtil.getNodeContent(childNode);
				String[] disablePages = disable.split("\\|");
				this.disablePage = EnumSet.noneOf(DisableFunction.class);
				for (String s : disablePages) {
					try {
						this.disablePage.add((DisableFunction) Enum.valueOf(
								DisableFunction.class, s));
					} catch (Exception e) {
					}
				}
			} else if ("tk:DefaultOrder".equals(childNode.getNodeName()))
				this.defaultOrder = XmlUtil.getNodeContent(childNode);
			else if ("tk:DefaultSortField".equals(childNode.getNodeName()))
				this.defaultSortField = XmlUtil.getNodeIntValue(childNode, 0);
			else if ("tk:FunctionRight".equals(childNode.getNodeName()))
				this.functionRight = FunctionRightConfigItem.parse(childNode);
			else if ("tk:Resolvers".equals(childNode.getNodeName()))
				this.resolvers = ResolversConfigItem.parse(childNode);
			else if ("tk:Relations".equals(childNode.getNodeName()))
				this.relations = RelationsConfigItem.parse(childNode);
			else if ("tk:XsltFiles".equals(childNode.getNodeName()))
				this.xsltFiles = XsltFilesConfigItem.parse(childNode);
			else if ("tk:DefaultPage".equals(childNode.getNodeName()))
				this.defaultPage = DefaultPageConfigItem.parse(childNode);
		}
	}

	public static ModuleConfigItem parse(Node node) {
		if (node == null)
			return null;
		ModuleConfigItem result = new ModuleConfigItem();
		result.readXml(node);
		return result;
	}

	public final int getPageSize() {
		return pageSize;
	}
}