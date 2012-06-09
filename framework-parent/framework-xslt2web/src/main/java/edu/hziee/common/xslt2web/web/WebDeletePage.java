package edu.hziee.common.xslt2web.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.IDeletePage;
import yjc.toolkit.sys.PageStyle;

public class WebDeletePage extends WebDataSetPage {
	private IDeletePage deletePage;

	public WebDeletePage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setStyle(PageStyle.Delete);
		setSubFunctionKey(DELETE_SUB_KEY);
	}

	@Override
	protected void doGet() {
		super.doGet();
		succeedCommit();
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);
		if (source != null) {
			if (source instanceof IDeletePage)
				deletePage = (IDeletePage) source;
			else
				throw new ToolkitException(
						"��ȻҪ��Delete��DataSet��֧��IDeletePage��ô���ԣ�");
		}
	}

	public final IDeletePage getDeletePage() {
		return deletePage;
	}

	protected void succeedCommit() {
		String s = getDeletePage().getDefaultPage(true, getStyle(),
				getOperation(), getRetURL());
		try {
			getResponse().sendRedirect(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
