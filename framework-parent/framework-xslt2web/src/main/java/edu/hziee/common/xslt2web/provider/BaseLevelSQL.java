package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.configxml.LevelConfigItem;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;

public abstract class BaseLevelSQL {
	private int length;
	private String[] likeValues;
	private String[] exceptValues;
	private LevelConfigItem configItem;

	protected BaseLevelSQL() {
	}

	public final int getLength() {
		return length;
	}

	public final LevelConfigItem getConfigItem() {
		return configItem;
	}

	public void prepare(LevelConfigItem config) {
		configItem = config;
		length = config.getTotalLength();
		int level = config.getSubItem().size();
		likeValues = new String[level];
		exceptValues = new String[level];
		for (int i = 0; i < level; ++i) {
			likeValues[i] = getLikeValues(i);
			exceptValues[i] = getExceptValues(i);
		}
	}

	public abstract String getLikeValues(int level);

	public abstract String getExceptValues(int level);

	public IParamBuilder getQuerySQL(TypeCode type, String fieldName,
			String fieldValue) {
		int len = Math.min(fieldValue.length(), configItem.getTotalLength());
		int level = configItem.getLevel()[len];
		if (level == 0)
			return SQLParamBuilder.getSingleSQL(type, fieldName, "LIKE",
					likeValues[0]);
		else {
			String subValue = fieldValue.substring(0, configItem.getSubItem()
					.get(level - 1).getSubTotalLength());
			String likeValue = String.format(likeValues[level], subValue);
			String exceptValue = String.format(exceptValues[level], subValue);
			return CodeLikeParamBuilder.getLikeSQL(type, fieldName, likeValue,
					exceptValue);
		}
	}
}
