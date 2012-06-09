package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

@XmlDataSetAnnotation(regName = WebTreeXmlDataSet.___REG_NAME, description = "支持树状结构显示", author = "YJC", createDate = "2008-10-01")
public class WebTreeXmlDataSet extends WebSingleXmlDataSet {
	final static String ___REG_NAME = "Tree";
	private TreeHelper helper;

	public WebTreeXmlDataSet(ModuleXml module) {
		super(module);
		setDisablePage(EnumSet.of(DisableFunction.List));
	}

	@Override
	protected void setResolvers() {
		super.setResolvers();
		helper = new TreeHelper(this, getModule());
	}

	@Override
	protected void committingData() {
		super.committingData();
		helper.committingData();
	}

	@Override
	protected void delete() {
		helper.delete();
		commit();
	}

	@Override
	protected String getDataXslt(PageStyle style, String operation) {
		String result = helper.getDataXslt(style, operation);
		if ("".equals(result))
			return super.getDataXslt(style, operation);
		else
			return result;
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return helper.getDefaultPage(isPost, style, operation, retURL);
	}

	@Override
	public String getDefaultXsltTemplate(PageStyle style, String operation) {
		String result = helper.getDefaultXsltTemplate(style, operation);
		if ("".equals(result))
			return super.getDefaultXsltTemplate(style, operation);
		else
			return result;
	}

	@Override
	public boolean getSupportDoubleTransform(PageStyle style, String operation) {
		return helper.getSupportDoubleTransform(style, operation);
	}

	@Override
	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		return helper.getXsltFile(isIe, style, operation);
	}

	@Override
	protected void navigateData(PageStyle style) {
		if (style != PageStyle.Custom)
			super.navigateData(style);
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		if (style != PageStyle.Custom)
			super.processCodeTables(style);
	}

	@Override
	public String getXmlString() {
		if (this.getTables().contains("_TREE")) {
			TreeXmlHelper xmlHelper = new TreeXmlHelper("ID", "NAME",
					"PARENT_ID", "ISLEAF");
			xmlHelper.addDataTable(this.getTables().getItem("_TREE"));
			return xmlHelper.toXml(this.getTables()
					.getItem("_TREE_PARENT_INFO").getRows().getItem(0).getItem(
							"ROOT_ID").toString());
		} else
			return super.getXmlString();
	}

}
