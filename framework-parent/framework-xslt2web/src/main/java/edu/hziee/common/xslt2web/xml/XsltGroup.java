package edu.hziee.common.xslt2web.xml;

import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class XsltGroup {
	private String path;
	private String header;
	private String headLeft;
	private String bottom;
	private String content;
	private String error;

	private String getPathName(String fileName) {
		if (StringUtil.isEmpty(fileName))
			return "";
		else
			return FileUtil.combin(path, fileName);
	}

	public final void assignTo(XsltGroup dest) {
		dest.setHeader(header);
		dest.setHeadLeft(headLeft);
		dest.setBottom(bottom);
		dest.setContent(content);
		dest.setPath(path);
		dest.setError(error);
	}

	public final String getPath() {
		return path;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	public final String getHeader() {
		return getPathName(header);
	}

	public final void setHeader(String header) {
		this.header = header;
	}

	public final String getHeadLeft() {
		return getPathName(headLeft);
	}

	public final void setHeadLeft(String headLeft) {
		this.headLeft = headLeft;
	}

	public final String getBottom() {
		return getPathName(bottom);
	}

	public final void setBottom(String bottom) {
		this.bottom = bottom;
	}

	public final String getContent() {
		return getPathName(content);
	}

	public final void setContent(String content) {
		this.content = content;
	}

	public final String getError() {
		return error;
	}

	public final void setError(String error) {
		this.error = error;
	}

	public final String getItem(HtmlPosition position) {
		switch (position) {
		case Head:
			return getHeader();
		case HeadLeft:
			return getHeadLeft();
		case Content:
			return getContent();
		case Bottom:
			return getBottom();
		default:
			return "";
		}
	}

	public final void setItem(HtmlPosition position, String value) {
		switch (position) {
		case Head:
			setHeader(value);
			break;
		case HeadLeft:
			setHeadLeft(value);
			break;
		case Content:
			setContent(value);
			break;
		case Bottom:
			setBottom(value);
			break;
		}
	}
}
