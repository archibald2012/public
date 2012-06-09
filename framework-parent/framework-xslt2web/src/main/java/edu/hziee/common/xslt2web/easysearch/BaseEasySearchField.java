package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.constraint.VariousConstraint;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataNavEventArgs;
import edu.hziee.common.xslt2web.data.NavigateDataListener;
import edu.hziee.common.xslt2web.sys.EventArgs;

public abstract class BaseEasySearchField implements NavigateDataListener {
    private String fieldName;
    private String displayName;
    private boolean autoConstraint;
    private BaseDataSet dataSet;
    
	public BaseEasySearchField(String fieldName, String displayName,
			BaseDataSet dataSet) {
		super();
		this.fieldName = fieldName;
		this.displayName = displayName;
		this.dataSet = dataSet;
	}

	public final boolean isAutoConstraint() {
		return autoConstraint;
	}

	public final void setAutoConstraint(boolean autoConstraint) {
		this.autoConstraint = autoConstraint;
	}

	public final String getFieldName() {
		return fieldName;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final BaseDataSet getDataSet() {
		return dataSet;
	}

	public abstract VariousConstraint getConstraint();
	
	public abstract void beginNavigateData(EventArgs e);
	public abstract void endNavigateData(EventArgs e);
	public abstract void navigatingData(DataNavEventArgs e);
}
