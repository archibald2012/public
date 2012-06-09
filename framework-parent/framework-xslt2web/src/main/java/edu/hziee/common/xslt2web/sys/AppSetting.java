package edu.hziee.common.xslt2web.sys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.hziee.common.xslt2web.configxml.AppConfigXml;
import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class AppSetting {
	private static AppSetting current;

	private String requestEncoding;
	private String responseGetEncoding;
	private String responsePostEncoding;

	// Application Path
	private String solutionPath;
	private String xmlPath;
	private String dataXmlPath;
	private String moduleXmlPath;
	private String easySearchPath;
	private String xsltPath;
	private String pluginPath;
	private String errorPath;

	// Create class
	private String appStartClass;
	private String sessionStartClass;

	// Database information
	private String connectionString;
	private String user;
	private String password;
	private boolean pooled;
	private DataSource dataSource;
	private String dbProvider;
	private String sqlProvider;
	private int idStep;

	// Debug flag
	private boolean showException;
	private boolean useCache;
	private boolean debug;

	// Other flag
	private boolean useInternalXslt;
	private int maxExcelRecords;
	private RegsCollection regs;

	public AppSetting(AppConfigXml config) {
		this.requestEncoding = config.getEncoding().getRequest();
		this.responseGetEncoding = config.getEncoding().getResponseGet();
		this.responsePostEncoding = config.getEncoding().getResponsePost();

		this.solutionPath = config.getApplicationItem().getPath();
		this.xmlPath = FileUtil.combin(this.solutionPath, "Xml");
		this.dataXmlPath = FileUtil.combin(this.xmlPath, "Data");
		this.moduleXmlPath = FileUtil.combin(this.xmlPath, "Module");
		this.easySearchPath = FileUtil.combin(this.xmlPath, "EasySearch2");
		this.xsltPath = FileUtil.combin(this.xmlPath, "Project");
		this.pluginPath = FileUtil.combin(this.xmlPath, "plugin");
		this.errorPath = FileUtil.combin(this.solutionPath, "Error");

		this.appStartClass = config.getApplicationItem().getAppStartClass();
		this.sessionStartClass = config.getApplicationItem()
				.getSessionStartClass();

		this.connectionString = config.getDatabaseItem().getConnectionString()
				.getValue();
		this.user = config.getDatabaseItem().getConnectionString().getUser();
		this.password = config.getDatabaseItem().getConnectionString()
				.getPassword();
		this.pooled = config.getDatabaseItem().getConnectionString().isPool();
		this.dbProvider = config.getDatabaseItem().getDbProvider();
		this.sqlProvider = config.getDatabaseItem().getSqlProvider();
		this.idStep = config.getDatabaseItem().getIDStep();

		this.showException = config.getDebugItem().isShowException();
		this.useCache = config.getDebugItem().isUseCache();
		this.debug = config.getDebugItem().isDebug();

		this.useInternalXslt = config.getOtherItem().isUseInternalXslt();
		this.maxExcelRecords = config.getOtherItem().getMaxExcelRecords();
	}

	public static final AppSetting getCurrent() {
		return current;
	}

	public static final void setCurrent(AppSetting current) {
		AppSetting.current = current;
	}

	public final String getRequestEncoding() {
		return requestEncoding;
	}

	public final String getResponseGetEncoding() {
		return responseGetEncoding;
	}

	public final String getResponsePostEncoding() {
		return responsePostEncoding;
	}

	public final String getSolutionPath() {
		return solutionPath;
	}

	public final String getErrorPath() {
		return errorPath;
	}

	public final String getXmlPath() {
		return xmlPath;
	}

	public final String getDataXmlPath() {
		return dataXmlPath;
	}

	public final String getModuleXmlPath() {
		return moduleXmlPath;
	}

	public final String getEasySearchPath() {
		return easySearchPath;
	}

	public final String getXsltPath() {
		return xsltPath;
	}

	public final String getAppStartClass() {
		return appStartClass;
	}

	public final String getSessionStartClass() {
		return sessionStartClass;
	}

	public final String getConnectionString() {
		return connectionString;
	}

	public final String getUser() {
		return user;
	}

	public final String getPassword() {
		return password;
	}

	public final boolean usePrompt() {
		return !(StringUtil.isEmpty(user) && StringUtil.isEmpty(password));
	}

	private synchronized void setDataSource() throws NamingException {
		if (dataSource == null) {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup(connectionString);
		}
	}

	public final Connection getConnection() throws SQLException,
			NamingException {
		if (pooled) {
			if (dataSource == null)
				setDataSource();
			return dataSource.getConnection();
		} else
			return DriverManager.getConnection(connectionString);
	}

	public final String getDbProvider() {
		return dbProvider;
	}

	public final String getSqlProvider() {
		return sqlProvider;
	}

	public final int getIdStep() {
		return idStep;
	}

	public final String getPluginPath() {
		return pluginPath;
	}

	public final boolean isShowException() {
		return showException;
	}

	public final boolean isUseCache() {
		return useCache;
	}

	public final boolean isDebug() {
		return debug;
	}

	public final boolean isUseInternalXslt() {
		return useInternalXslt;
	}

	public final int getMaxExcelRecords() {
		return maxExcelRecords;
	}

	public final RegsCollection getRegs() {
		return regs;
	}

	public final void setRegs(RegsCollection regs) {
		this.regs = regs;
	}

}
