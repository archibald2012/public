package edu.hziee.common.xslt2web.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.DataSet;
import yjc.toolkit.data.UpdateKind;
import yjc.toolkit.exception.ErrorPageException;
import yjc.toolkit.exception.InformationException;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.IXmlHttpPostPage;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sysutil.FileUtil;
import yjc.toolkit.xml.TransformType;

public class WebXmlHttpPage extends WebXmlPage {
	private UpdateKind status = UpdateKind.Insert;

	private DataSet postDataSet;

	private IXmlHttpPostPage httpPostPage;

	protected class SubmitErrorPageExcpetion extends ErrorPageException {

		private static final long serialVersionUID = -763754801969280496L;

		// / <summary>
		// / ���캯��
		// / </summary>
		public SubmitErrorPageExcpetion() {
			// ErrorTitle = PageTitle = ErrorBody =
			// TKResUtil.GetResourceString("SubmitErrorPage_Title");
		}
	}

	private byte[] streamToData(InputStream stream) throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		int r = stream.read(buffer);
		while (r != -1) {
			s.write(buffer, 0, r);
			r = stream.read(buffer);
		}
		byte[] result = s.toByteArray();
		s.close();
		return result;
	}
	
	public WebXmlHttpPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		try {
			String content = "";
			if (isPost()) {
				if (AppSetting.getCurrent().isDebug()) {
					try {
						byte[] data = streamToData(getRequest().getInputStream());
						content = new String(data, "UTF8");
						String fileName = FileUtil.combin(AppSetting.getCurrent()
								.getXmlPath(), "Post.txt");
						FileUtil.saveFile(fileName, content, "GBK");
						//getRequest().getInputStream().mark(0);
						//getRequest().getInputStream().reset();
					} catch (Exception e) {

					}
				}
				postDataSet = new DataSet();
				postDataSet.readXml(content);
				if (AppSetting.getCurrent().isDebug()) {
					String fileName = FileUtil.combin(AppSetting.getCurrent()
							.getXmlPath(), "Post.xml");
					content = postDataSet.toXml();
					FileUtil.saveFile(fileName, content, "GBK");
				}

			}
		} catch (Exception ex) {
			if (AppSetting.getCurrent().isDebug()) {
				try {
					byte[] data = new byte[getRequest().getInputStream()
							.available()];
					getRequest().getInputStream().reset();
					getRequest().getInputStream().read(data, 0, data.length);
					String content = new String(data, "UTF8");
					String fileName = FileUtil.combin(AppSetting.getCurrent()
							.getXmlPath(), "Post.txt");
					FileUtil.saveFile(fileName, content, "GBK");
				} catch (Exception e) {

				}
			}
			throw new ToolkitException("�ύ��������ݲ���XML������ϸ��飡");
		}
	}

	@Override
	protected void writePage() {
		super.writePage();

		if (!isError()
				&& !getTransform().isShowSource()
				&& getSource() != null
				&& (getStyle() == PageStyle.Insert || getStyle() == PageStyle.Update))
			getResponseWriter().write(
					getHttpPostPage().getJScript(getStyle(), getOperation()));

	}

	@Override
	protected void checkSubmit() {
//		Object submitKey = null;
//		try {
//			submitKey = getPostDataSet().getTables().getItem("OtherInfo")
//					.getRows().getItem(0).getItem("DataSet");
//		} catch (Exception ex) {
//		}
//		if (submitKey != null
//				&& submitKey.toString().equals(
//						getSessionGbl().getGuid().toString())) {
//			getSessionGbl().setGuid();
//			getSource().getTables().getItem("Info").getRows().getItem(0)
//					.setItem("Guid", getSessionGbl().getGuid());
//		} else
//			throw new SubmitErrorPageExcpetion();
	}

	@Override
	protected void doPost() {
		getHttpPostPage().post(getStyle(), getOperation(), getRequest(),
				getPostDataSet());
		switch (getStyle()) {
		case Insert:
		case Update:
			succeedCommit();
			break;
		}
	}

	@Override
	protected void handleInformation(InformationException ex) {
		setTransformType(TransformType.Double);

		getXsltFile();
		if (AppSetting.getCurrent().isDebug()) {
			String fileName = FileUtil.combin(AppSetting.getCurrent()
					.getXmlPath(), "Error.xml");
			String content = getXmlString();
			FileUtil.saveFile(fileName, content, "GBK");
		}
		// Response.ContentEncoding = System.Text.Encoding.UTF8;

		writePostTransformData();
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);
		if (source != null) {
			if (source instanceof IXmlHttpPostPage)
				httpPostPage = (IXmlHttpPostPage) source;
			else
				throw new ToolkitException(
						"DataSet����֧��IXmlHttpPostPage�ӿڣ���֧��Ҳ��֧�֡�");
		}
	}

	private void writeRedirectURL(String url) {
		String alert = getHttpPostPage().getAlertString(getStyle(),
				getOperation(), getRequest(), getPostDataSet());
		// Response.ContentEncoding = System.Text.Encoding.UTF8;
		if ("".equals(alert))
			getResponseWriter().write("OK" + url);
		else
			getResponseWriter().write(
					String.format("OK;Alert;%s;Alert;%s", alert, url));
	}

	protected void succeedCommit() {
		if (getXmlPage() != null) {
			String s = getXmlPage().getDefaultPage(true, getStyle(),
					getOperation(), getRetURL());
			writeRedirectURL(s);
		}
	}

	public final IXmlHttpPostPage getHttpPostPage() {
		return httpPostPage;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	final UpdateKind getStatus() {
		return status;
	}

	final void setStatus(UpdateKind status) {
		this.status = status;
	}
}
