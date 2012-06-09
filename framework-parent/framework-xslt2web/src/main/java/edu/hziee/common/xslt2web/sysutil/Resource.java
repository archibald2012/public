package edu.hziee.common.xslt2web.sysutil;

public final class Resource {

	private Resource() {
	}

	public static final String JScriptSkeleton = "<script language='javascript'>\nfunction verify(procType)\n{\n    %s\n%s\n    Post(procType);\n    return true;\n}\n</script>\n";
	public static final String ErrorPageXml = "  <PageTitle>%s</PageTitle>\n  <ErrorTitle>%s</ErrorTitle>\n  <ErrorBody>%s</ErrorBody>\n  <RetURL>%s</RetURL>";
}
