package edu.hziee.common.xslt2web.sys;


public class ApplicationGlobal {
	public static final String APP_CONST = "ToolkitApp";
	private RegsCollection regsCollection;

	public ApplicationGlobal() {
	}
	
	public final RegsCollection getRegsCollection() {
		return regsCollection;
	}

	public final void setRegsCollection(RegsCollection regsCollection) {
		this.regsCollection = regsCollection;
	}
	
	public final void searchPlugin() {
		String pluginPath = AppSetting.getCurrent().getXmlPath();
		if (this.regsCollection != null)
			regsCollection.initialize(pluginPath);
	}
}
