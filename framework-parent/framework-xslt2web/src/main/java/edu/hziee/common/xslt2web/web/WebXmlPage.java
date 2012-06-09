package edu.hziee.common.xslt2web.web;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.DataRow;
import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.DataTable;
import yjc.toolkit.exception.ErrorPageException;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.IXmlPage;
import yjc.toolkit.sysutil.DataSetUtil;
import yjc.toolkit.sysutil.FileUtil;
import yjc.toolkit.sysutil.StringUtil;
import yjc.toolkit.sysutil.WebUtil;
import yjc.toolkit.xml.HtmlPosition;
import yjc.toolkit.xml.IDoubleTransform;
import yjc.toolkit.xml.IDoubleTransformAll;
import yjc.toolkit.xml.ITransformAll;
import yjc.toolkit.xml.IXmlSource;
import yjc.toolkit.xml.TransformType;

public class WebXmlPage extends WebDataSetPage implements IXmlSource {
	public static final String INSERT_SUB_KEY = "01";

	public static final String UPDATE_SUB_KEY = "02";

	public static final String LIST_SUB_KEY = "04";

	public static final String DETAIL_SUB_KEY = "05";

	public static final String VIEW_TAG = "_toolkit";

	public static final String VIEW_VALUE = "xml";

	private ITransformAll transform;

	private IDoubleTransformAll doubleTransform;

	private DataTable url;

	private DataTable info;

	private String defaultURL;

	private String ieXslFile = "";

	private String navXslFile = "";

	private String encoding;

	private boolean isModule;

	private boolean setDefaultXslt;

	private IXmlPage xmlPage;

	private TransformType transformType;

	public WebXmlPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		encoding = getSessionGlobal().getInfo().getEncoding();
		isModule = false;
		transformType = TransformType.Single;
		createTables();
	}

	private void createTables() {
		url = DataSetUtil.createDataTable("URL", "RetURL", "SelfURL",
				"DRetURL", "DSelfURL");
		url.getRows().add(url.newRow());

		String[] infoFields = { "UserID", "RoleID", "Source", "Module", "Guid" };
		info = DataSetUtil.createDataTable("Info", infoFields);
		DataRow row = info.newRow();
		DataSetUtil.setRowValues(row, infoFields, getSessionGlobal().getInfo()
				.getUserID(), getSessionGlobal().getInfo().getRoleID(),
				getRequest().getParameter("Source"), (isModule) ? 1 : 0,
				getSessionGlobal().getGuid());
		info.getRows().add(row);
	}

	public String getEncoding() {
		return this.encoding;
	}

	public String getXmlString() {
		String xml = getSource() == null ? "" : getSource().getXmlString();
		return xml;
		// return String.format("<?xml version='1.0' encoding='%s'?>\n%s",
		// encoding, xml);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public final String getIeXslFile() {
		return ieXslFile;
	}

	public final void setIeXslFile(String ieXslFile) {
		this.ieXslFile = ieXslFile;
	}

	public final String getNavXslFile() {
		return navXslFile;
	}

	public final void setNavXslFile(String navXslFile) {
		this.navXslFile = navXslFile;
	}

	public final ITransformAll getTransform() {
		return transform;
	}

	public final void setTransform(ITransformAll transform) {
		if (this.transform == transform)
			return;
		setTransformProperty(transform, this.transform);
		this.transform = transform;
	}

	private void setTransformProperty(ITransformAll source, ITransformAll dest) {
		// ITransformAll dest = this.transform;
		if (dest == null) {
			getSessionGlobal().getIeFiles().assignTo(source.getIeFiles());
			getSessionGlobal().getNavFiles().assignTo(source.getNavFiles());
			source.setItem(HtmlPosition.Content, this);
			source.getIeFiles().setContent(this.ieXslFile);
			source.getNavFiles().setContent(this.navXslFile);
			source.setShowSource(VIEW_VALUE.equalsIgnoreCase(getRequest()
					.getParameter(VIEW_TAG)));
		} else {
			if (source != null)
				source.assign(dest);
		}
	}

	public final IDoubleTransformAll getDoubleTransform() {
		return doubleTransform;
	}

	public final void setDoubleTransform(IDoubleTransformAll doubleTransform) {
		if (this.doubleTransform == doubleTransform)
			return;
		setTransformProperty(doubleTransform, this.doubleTransform);
		this.doubleTransform = doubleTransform;
	}

	public final boolean isModule() {
		return isModule;
	}

	public final void setModule(boolean isModule) {
		if (this.isModule != isModule) {
			this.isModule = isModule;
			this.info.getRows().getItem(0).setItem("Module", isModule ? 1 : 0);
		}
	}

	public final String getDefaultURL() {
		return defaultURL;
	}

	public final void setDefaultURL(String defaultURL) {
		this.defaultURL = defaultURL;
		if ("".equals(getRetURL())) {
			DataRow row = url.getRows().getItem(0);
			row.setItem("RetURL", defaultURL);
			row.setItem("DRetURL", WebUtil.encodeURL(defaultURL));
		}
	}

	public final TransformType getTransformType() {
		return transformType;
	}

	public final void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}

	public final IXmlPage getXmlPage() {
		return xmlPage;
	}

	@Override
	protected void doGet() {
		setData();
		getXsltFile();
		writePage();
	}

	protected void writePage() {
		ITransformAll transform = getTransformAll(isPost());
		getResponse().setContentType(
				transform.isShowSource() ? "text/xml" : "text/html");
		String html = transform.transformAll(isIe());
		getResponseWriter().write(html);
	}

	private ITransformAll getTransformAll(boolean post) {
		switch (transformType) {
		case Double:
			if (getSource() instanceof IDoubleTransform
					&& ((IDoubleTransform) getSource())
							.getSupportDoubleTransform(getStyle(),
									getOperation())) {
				IDoubleTransform transform = (IDoubleTransform) getSource();
				int number = transform.getDocumentNumber(getStyle(),
						getOperation());
				doubleTransform.setDocumentNumber(number, getStyle(),
						getOperation());
				for (int i = 0; i < number; ++i)
					doubleTransform.setXmlDocument(i, transform.getXmlDocument(
							i, getStyle(), getOperation()));
				doubleTransform.setPost(post);
				if (setDefaultXslt) {
					String template = transform.getDefaultXsltTemplate(
							getStyle(), getOperation());
					doubleTransform.getIeFiles().setContent(template);
					doubleTransform.getNavFiles().setContent(template);
				}
				return doubleTransform;
			} else
				return transform;
		case Single:
			return transform;
		default:
			return null;
		}
	}

	private void setTransformFile(ITransformAll transform, String ieXslFile,
			String navXslFile) {
		transform.getIeFiles().setContent(ieXslFile);
		transform.getNavFiles().setContent(navXslFile);
	}

	private boolean isDefaultFile(String fileName, String xsltFile) {
		if (StringUtil.isEmpty(fileName))
			return true;
		else {
			File file = new File(xsltFile);
			return !file.exists();
		}
	}

	private boolean isSetDefaultXslt() {
		if (isIe())
			return isDefaultFile(ieXslFile, doubleTransform.getIeFiles()
					.getContent());
		else
			return isDefaultFile(navXslFile, doubleTransform.getNavFiles()
					.getContent());
	}

	protected void getXsltFile() {
		if (StringUtil.isEmpty(ieXslFile))
			ieXslFile = xmlPage.getXsltFile(true, getStyle(), getOperation());
		if (StringUtil.isEmpty(navXslFile)) {
			navXslFile = xmlPage.getXsltFile(false, getStyle(), getOperation());
			if (StringUtil.isEmpty(navXslFile))
				navXslFile = ieXslFile;
		}

		setTransformFile(transform, ieXslFile, navXslFile);
		setTransformFile(doubleTransform, ieXslFile, navXslFile);
		setDefaultXslt = isSetDefaultXslt();

		setDefaultURL(WebUtil.decodeURL(getXmlPage().getDefaultPage(isPost(),
				getStyle(), getOperation(), getRetURL())));
	}

	protected void setData() {
		getXmlPage().setData(false, getStyle(), getOperation(), getRequest());
		transformType = TransformType.Double;
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);

		if (source != null) {
			if (source instanceof IXmlPage)
				xmlPage = (IXmlPage) source;
			else
				throw new ToolkitException(source.getClass().toString()
						+ "���ͱ���֧��IXmlPage�ӿڣ���֧���޷�ִ�У�");
			source.getTables().add(url);
			source.getTables().add(info);
			source.getTables().add(
					DataSetUtil.map2DataTable("QueryString", getRequest()
							.getParameterMap()));
		}
	}

	@Override
	protected void initPageData() {
		setTransform(getSessionGlobal().getTransform());
		setDoubleTransform(getSessionGlobal().getDoubleTransform());

		super.initPageData();

		encoding = getSessionGlobal().getInfo().getEncoding();
		DataRow urlRow = url.getRows().getItem(0);
		urlRow.setItem("SelfURL", getEncodeURL());
		urlRow.setItem("DSelfURL", getSelfURL());
		if (!"".equals(getRetURL())) {
			urlRow.setItem("RetURL", getRetURL());
			urlRow.setItem("DRetURL", WebUtil.encodeURL(getRetURL()));
		} else if (!StringUtil.isEmpty(defaultURL)) {
			setDefaultURL(defaultURL);
		}
	}

	protected final void writePostTransformData() {
		String html = getTransformAll(true).transformContent(isIe());

		if (AppSetting.getCurrent().isDebug()) {
			String fileName = FileUtil.combin(AppSetting.getCurrent()
					.getXmlPath(), "Post.html");
			FileUtil.saveFile(fileName, html, "GBK");
		}

		getResponseWriter().write(html);
	}

	protected final DataRow getUrlRow() {
		return url.getRows().getItem(0);
	}

	private void handleErrorPage(String ieErrorXslt, String navErrorXslt,
			IXmlSource source) {
		transformType = TransformType.Single;

		transform = getSessionGlobal().getTransform();
		transform.getIeFiles().setContent(ieErrorXslt);
		transform.getNavFiles().setContent(navErrorXslt);
		transform.setItem(HtmlPosition.Content, source);
		transform.setShowSource(isPost() ? false : VIEW_VALUE.equals(StringUtil
				.getDefaultStr(getRequest().getParameter(VIEW_TAG))
				.toLowerCase()));
		if (isGet())
			writePage();
		else {
			getResponseWriter().write(getTransform().transformContent(isIe()));
		}
	}

	@Override
	protected void handleBaseException(RuntimeException ex) {
		try {
			super.handleBaseException(ex);
		} catch (Exception e) {
		}

		if (AppSetting.getCurrent().isShowException()) {
			String xslt = "../Tk2Template/Exception.xslt";
			handleErrorPage(xslt, xslt, getExceptionSource());
		} else
			handleErrorPage(new InternalErrorPageException(getErrorID()));
	}

	@Override
	protected void handleErrorPage(ErrorPageException ex) {
		getSessionGlobal().getErrorPage().setErrorPage(ex);
		handleErrorPage(transform.getIeFiles().getError(), transform
				.getNavFiles().getError(), getSessionGlobal().getErrorPage());
	}

	@Override
	protected void handleToolkit(ToolkitException ex) {
		try {
			super.handleToolkit(ex);
		} catch (Exception e) {
		}

		String xslt = "../Tk2Template/Exception.xslt";
		handleErrorPage(xslt, xslt, getExceptionSource());
	}
}
