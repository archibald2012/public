package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;

public abstract class TreeXmlTableResolver extends XmlTableResolver implements
		ITreeFieldGroup {

	public TreeXmlTableResolver(BaseDataSet hostDataSet) {
		super(hostDataSet);
	}

	public String getRootID() {
		return "-1";
	}

	public abstract TreeFieldGroup getTreeFields();

	public boolean isParentID() {
		return true;
	}

	@Override
	protected void onUpdatingRow(UpdatingEventArgs e) {
		super.onUpdatingRow(e);

		switch (e.getStatus()) {
		case Insert:
			if ("".equals(e.getRow().getItem(getTreeFields().getParentID())
					.toString()))
				e.getRow().setItem(getTreeFields().getParentID(), -1);
			break;
		case Update:
			break;
		}
	}

	@Override
	public void setDefaultValue(DataRow row) {
		super.setDefaultValue(row);

		Object pID = getRequest().getParameter("PID");
		if (pID != null)
			row.setItem(getTreeFields().getParentID(), pID);
	}

}
