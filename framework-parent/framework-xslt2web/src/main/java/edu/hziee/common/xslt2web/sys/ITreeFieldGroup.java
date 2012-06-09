package edu.hziee.common.xslt2web.sys;

public interface ITreeFieldGroup {
	TreeFieldGroup getTreeFields();

	String getRootID();

	boolean isParentID();
	
	String getTableName();
}
