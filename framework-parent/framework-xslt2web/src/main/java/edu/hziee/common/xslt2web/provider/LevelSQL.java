package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class LevelSQL extends BaseLevelSQL {

	@Override
	public String getLikeValues(int level) {
		return level == 0 ? StringUtil.padRight("", getConfigItem()
				.getSubItem().get(0).getLength(), '_') : "%s"
				+ StringUtil.padRight("", getConfigItem().getSubItem().get(
						level).getLength(), '_');
	}

	@Override
	public String getExceptValues(int level) {
		return level == 0 ? "" : "%s";
	}
}
