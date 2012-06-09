package edu.hziee.common.xslt2web.sys;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface ISessionStart {
	void start(ServletRequest request, ServletResponse response, SessionGlobal global);
}
