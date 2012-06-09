package edu.hziee.common.xslt2web.sys;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface IApplicationStart {
	void start(ServletRequest request, ServletResponse response, AppSetting setting, ApplicationGlobal global);
}
