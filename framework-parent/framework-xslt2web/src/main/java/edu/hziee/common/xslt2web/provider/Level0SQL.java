package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class Level0SQL extends BaseLevelSQL {

	@Override
	public String getLikeValues(int level) {
		if (level == 0) {
			String temp = StringUtil.padRight("", getConfigItem().getSubItem()
					.get(0).getLength(), '_');
			return StringUtil.padRight(temp, getConfigItem().getTotalLength(),
					'0');
		} else {
			int len = getConfigItem().getTotalLength()
					- getConfigItem().getSubItem().get(level - 1)
							.getSubTotalLength();
			String temp = StringUtil.padRight("", getConfigItem().getSubItem()
					.get(level).getLength(), '_');
			return "%s" + StringUtil.padRight(temp, len, '0');
		}
	}

	@Override
	public String getExceptValues(int level) {
		if (level == 0)
			return "";
		else {
			int len = getConfigItem().getTotalLength()
					- getConfigItem().getSubItem().get(level - 1)
							.getSubTotalLength();
			return "%s" + StringUtil.padRight("", len, '0');
		}
	}
}
