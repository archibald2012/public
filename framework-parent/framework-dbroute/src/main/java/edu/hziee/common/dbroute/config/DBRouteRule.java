
package edu.hziee.common.dbroute.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于正则表达式的路由规则
 * 
 * @author Administrator
 * @version $Id: DBRouteRule.java 4 2012-01-10 11:51:54Z archie $
 */
public class DBRouteRule implements Serializable {
	private static final Logger logger = LoggerFactory
			.getLogger(DBRouteRule.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 292167084655270496L;

	private String dbName;
	private Map<String, Pattern> rules = new HashMap<String, Pattern>();

	public DBRouteRule(String dbName) {
		this.dbName = dbName;
	}

	public DBRouteRule addRule(String ruleKey, String rulePattern) {
		try {
			Pattern pattern = Pattern.compile(rulePattern);
			this.rules.put(ruleKey, pattern);
		} catch (PatternSyntaxException e) {
			logger.warn("DBRouteRule routing rule " + rulePattern
					+ " is inavailble to compile." + e, e);
		}
		return this;
	}

	/**
	 * 规则全匹配
	 * 
	 * @param items
	 * @return
	 */
	public boolean isMatched(Map<String, String> items) {
		if (items == null || items.isEmpty()) {
			return false;
		}

		boolean ret = true;

		for (Map.Entry<String, Pattern> e : rules.entrySet()) {

			String ruleKey = e.getKey();

			String itemValue = items.get(ruleKey);
			if (itemValue == null) {
				ret = false;
				break;
			}

			Pattern rulePattern = e.getValue();

			CharSequence cs = itemValue.subSequence(0, itemValue.length() - 1);
			Matcher m = rulePattern.matcher(cs);
			if (!m.matches()) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Map<String, Pattern> getRules() {
		return rules;
	}

}
