package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.IDataProvider;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.IHttpGetPage;
import yjc.toolkit.sys.INeedWebData;

public class WebDataSetPage extends WebBasePage {
	public final static String DELETE_SUB_KEY = "03";

	private BaseDataSet source;
	private IHttpGetPage httpGetPage;
	private String operation;
	private IDataProvider dataProvider;

	public WebDataSetPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		operation = "";
	}

	public final BaseDataSet getSource() {
		return source;
	}

	public void setSource(BaseDataSet source) {
		this.source = source;
		if (source != null) {
			if (AppSetting.getCurrent().usePrompt())
				source.setConnectionString(AppSetting.getCurrent()
						.getConnectionString(), AppSetting.getCurrent()
						.getUser(), AppSetting.getCurrent().getPassword());
			else
				source.setConnectionString(AppSetting.getCurrent()
						.getConnectionString());
			if (source instanceof INeedWebData)
				((INeedWebData) source).setData(getRequest(), getResponse(),
						getAppGlobal(), getSessionGlobal());
			if (source instanceof IHttpGetPage)
				httpGetPage = (IHttpGetPage) source;
			else
				throw new ToolkitException(source.getClass().toString()
						+ "���ͱ���֧��IHttpGetPage�ӿڣ���֧���޷�ִ�У�");
		}
	}

	public final String getOperation() {
		return operation;
	}

	public final IDataProvider getDataProvider() {
		return dataProvider;
	}

	public final void setDataProvider(IDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public final void setOperation(String operation) {
		this.operation = operation;
	}

	public final IHttpGetPage getHttpGetPage() {
		return httpGetPage;
	}

	@Override
	protected void doGet() {
		if (httpGetPage != null)
			httpGetPage.setData(isPost(), getStyle(), getOperation(),
					getRequest());
	}

	@Override
	protected void initPageData() {
		if (dataProvider != null) {
			String source = getRequest().getParameter("Source");
			setSource(dataProvider.createDataSet(getAppGlobal()
					.getRegsCollection(), source));
		}
	}
}
