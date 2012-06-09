package edu.hziee.common.xslt2web.sysutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

public final class WebUtil {
	private WebUtil() {
	}

	@SuppressWarnings("unchecked")
	public static String getSelfURL(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		int i = 0;
		for (String queryName : new EnumAdapter<String>(request
				.getParameterNames())) {
			if (!"returl".equalsIgnoreCase(queryName)) {
				url.append((i++ == 0) ? "?" : "&");
				url.append(queryName + "=" + request.getParameter(queryName));
			}
		}
		return url.toString();
	}

	public static String getRetURL(HttpServletRequest request) {
		return StringUtil.getDefaultStr(request.getParameter("RetURL"));
	}

	public static String encodeURL(String url) {
		try {
			return URLEncoder.encode(url, "UTF8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String decodeURL(String url) {
		try {
			return URLDecoder.decode(url, "UTF8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
