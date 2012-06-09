package edu.hziee.common.xslt2web.web;

import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.DataRow;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.IListPage;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sysutil.EnumAdapter;
import yjc.toolkit.sysutil.StringUtil;
import yjc.toolkit.sysutil.WebUtil;
import yjc.toolkit.xml.TransformType;

public class WebListPage extends WebXmlHttpPage {
	private IListPage listPage;

	public WebListPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		setStyle(PageStyle.List);
		setSubFunctionKey(LIST_SUB_KEY);
	}

	@SuppressWarnings("unchecked")
	private void setAdjustSelfURL(String condition) {
		StringBuffer url = getRequest().getRequestURL().append('?');

		if (!"".equals(condition))
			url.append(String.format("condition=%s&", WebUtil
					.encodeURL(condition)));
		EnumAdapter<String> keys = new EnumAdapter<String>(getRequest()
				.getParameterNames());
		HashMap<String, String> ignoreParams = getListPage().getIgnoreParams();
		for (String key : keys) {
			if (key == null || ignoreParams.containsKey(key.toUpperCase()))
				continue;
			url.append(String.format("%s=%s&", key, StringUtil
					.getDefaultStr(getRequest().getParameter(key))));
		}

		getUrlRow().setItem("DSelfURL", url.toString());
	}

	public final IListPage getListPage() {
		return listPage;
	}

	private void adjustSelfURL() {
		setAdjustSelfURL(StringUtil.getDefaultStr(getRequest().getParameter(
				"condition")));
	}

	private void adjustSelfURL2(String sqlCon) {
		setAdjustSelfURL(sqlCon);
		DataRow row = getUrlRow();
		row.setItem("SelfURL", WebUtil.encodeURL(row.getItem("DSelfURL")
				.toString()));
	}

	@Override
	protected void doPost() {
		setTransformType(TransformType.Double);

		super.doPost();
		getSessionGlobal().setGuid();
		getPostDataSet().getTables().getItem("OtherInfo").getRows().getItem(0)
				.setItem("DataSet", getSessionGlobal().getGuid());
		String sqlCon = getListPage().getQueryCondition(getPostDataSet());
		adjustSelfURL2(sqlCon);

		getXsltFile();
		writePostTransformData();
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);
		if (source != null) {
			if (source instanceof IListPage)
				listPage = (IListPage) source;
			else
				throw new ToolkitException("��ȻҪ��List��DataSet��֧��IListPage��ô���ԣ�");
		}
	}

	@Override
	protected void setData() {
		adjustSelfURL();
		super.setData();
	}
}
