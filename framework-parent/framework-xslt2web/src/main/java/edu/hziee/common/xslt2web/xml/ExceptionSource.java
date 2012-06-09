package edu.hziee.common.xslt2web.xml;

import java.util.Date;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.sys.UserInfo;
import edu.hziee.common.xslt2web.sysutil.Convert;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.xml.IXmlSource;

public class ExceptionSource implements IXmlSource {
	private String encoding;
	private Exception ex;
	private DataSet dataSet;
	private static final String[] FIELDS = { "Source", "Page", "URL",
			"DateTime", "Message", "ErrorSource", "StackTrace", "UserID",
			"RoleID", "Type" };

	ExceptionSource(String source, String errorPage, String url, UserInfo info,
			Exception ex) {
		super();
		this.ex = ex;
		this.encoding = "gb2312";
		this.dataSet = new DataSet("Toolkit");
		DataTable table = DataSetUtil.createDataTable("Error", FIELDS);
		this.dataSet.getTables().add(table);
		DataRow row = table.newRow();
		row.beginEdit();
		row.setItem("Source", source);
		row.setItem("Page", errorPage);
		row.setItem("URL", url);
		row.setItem("DateTime", Convert.DATETIME_FORMAT.format(new Date()));
		row.setItem("UserID", info.getUserID() == null ? "" : info.getUserID()
				.toString());
		row.setItem("RoleID", info.getRoleID() == null ? "" : info.getRoleID()
				.toString());
		handleException(row, this.ex);
		row.endEdit();
		table.getRows().add(row);
	}

	protected final String getStackTrace(Exception ex) {
		StringBuilder builder = new StringBuilder();
		StackTraceElement[] elements = ex.getStackTrace();
		for (StackTraceElement stackTraceElement : elements) {
			builder.append(String.format("at %s.%s() in %s : line %d\n",
					stackTraceElement.getClassName(), stackTraceElement
							.getMethodName(), stackTraceElement.getFileName(),
					stackTraceElement.getLineNumber()));
		}
		return builder.toString();
	}

	protected void handleException(DataRow row, Exception ex) {
		row.setItem("Message", ex.getMessage());
		row.setItem("StackTrace", getStackTrace(ex));
		row.setItem("Type", ex.getClass().toString());
	}

	public final String getEncoding() {
		return encoding;
	}

	public String getXmlString() {
		return dataSet.toXml(encoding);
	}

	public final void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
