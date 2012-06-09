package edu.hziee.common.xslt2web.sys;

import java.util.UUID;

import edu.hziee.common.xslt2web.right.RightCollection;
import edu.hziee.common.xslt2web.xml.ErrorPageSource;
import edu.hziee.common.xslt2web.xml.HtmlPosition;
import edu.hziee.common.xslt2web.xml.IDoubleTransformAll;
import edu.hziee.common.xslt2web.xml.ITransformAll;
import edu.hziee.common.xslt2web.xml.XmlEmptySource;
import edu.hziee.common.xslt2web.xml.XsltGroup;

public class SessionGlobal {
	public static final String SESSION_CONST = "ToolkitSession";

	private XsltGroup ieFiles;
	private XsltGroup navFiles;
	private String xsltPath;
	private UserInfo info;
	private ITransformAll transform;
	private XmlEmptySource emptySource;
	private ErrorPageSource errorPage;
	private RightCollection rights;
	private Object guid;
	//private CachesHashTable caches;
	//private LogService log;
	private IDoubleTransformAll doubleTransform;

	public SessionGlobal() {
		ieFiles = new XsltGroup();
		navFiles = new XsltGroup();
		xsltPath = AppSetting.getCurrent().getXsltPath();
		ieFiles.setPath(xsltPath);
		navFiles.setPath(xsltPath);
		info = new UserInfo();
		rights = new RightCollection();
		errorPage = new ErrorPageSource();
		emptySource = new XmlEmptySource();
	}

	private void setTransformProps() {
		HtmlPosition[] items = HtmlPosition.values();
		for (HtmlPosition htmlPosition : items)
			transform.setItem(htmlPosition, emptySource);
		getIeFiles().assignTo(transform.getIeFiles());
		getNavFiles().assignTo(transform.getNavFiles());
	}

	private void setDoubleTransformProps() {
		HtmlPosition[] items = HtmlPosition.values();
		for (HtmlPosition htmlPosition : items)
			doubleTransform.setItem(htmlPosition, emptySource);

		getIeFiles().assignTo(doubleTransform.getIeFiles());
		getNavFiles().assignTo(doubleTransform.getNavFiles());
	}

	public final ITransformAll getTransform() {
		return transform;
	}

	public final void setTransform(ITransformAll transform) {
		this.transform = transform;
		setTransformProps();
	}

	public final IDoubleTransformAll getDoubleTransform() {
		return doubleTransform;
	}

	public final void setDoubleTransform(IDoubleTransformAll doubleTransform) {
		this.doubleTransform = doubleTransform;
		setDoubleTransformProps();
	}

	public final XsltGroup getIeFiles() {
		return ieFiles;
	}

	public final XsltGroup getNavFiles() {
		return navFiles;
	}

	public final String getXsltPath() {
		return xsltPath;
	}

	public final UserInfo getInfo() {
		return info;
	}

	public final RightCollection getRights() {
		return rights;
	}

	public final Object getGuid() {
		return guid;
	}

	public final void setGuid() {
		guid = UUID.randomUUID().toString();
	}

	public final ErrorPageSource getErrorPage() {
		return errorPage;
	}
}
