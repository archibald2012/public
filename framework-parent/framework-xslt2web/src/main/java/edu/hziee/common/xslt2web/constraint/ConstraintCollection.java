package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class ConstraintCollection {
	private ConstraintList firstConstraints;
	private ConstraintList constraints;
	private String tableName;
	private BaseDataSet dataSet;

	public ConstraintCollection(String tableName, BaseDataSet dataSet) {
		super();
		this.tableName = tableName;
		this.dataSet = dataSet;
		firstConstraints = new ConstraintList(this.dataSet);
		constraints = new ConstraintList(this.dataSet);
	}

	public void add(BaseConstraint constraint) {
		constraint.setTableName(tableName);
		if (constraint.isFirstCheck())
			firstConstraints.add(constraint);
		else
			constraints.add(constraint);
	}

	public final ConstraintList getFirstConstraints() {
		return firstConstraints;
	}

	public final ConstraintList getLaterConstraints() {
		return constraints;
	}

	public final String getTableName() {
		return tableName;
	}

	// / <summary>
	// / 将另一个约束集合的内容添加到本集合中
	// / </summary>
	// / <param name="constraints">包含要添加到集合的对象的约束集合</param>
	public void AddRange(ConstraintCollection constraints) {
		for (BaseConstraint constraint : constraints.getFirstConstraints())
			add(constraint);
		for (BaseConstraint constraint : constraints.getLaterConstraints())
			add(constraint);
	}

	public void checkFirst(DataSet postDataSet,
			ErrorObjectCollection errorObjects) {
		firstConstraints.check(postDataSet, tableName, errorObjects);
	}

	public void check(DataSet postDataSet, ErrorObjectCollection errorObjects) {
		constraints.check(postDataSet, tableName, errorObjects);
	}

	private final static int ScriptBlock = 4096;

	public String getJavaScript() {
		StringBuilder script = new StringBuilder(ScriptBlock);
		for (BaseConstraint constraint : firstConstraints) {
			String aScript = constraint.getJavaScript();
			if (!StringUtil.isEmpty(aScript))
				script.append(aScript);
		}
		for (BaseConstraint constraint : constraints) {
			String aScript = constraint.getJavaScript();
			if (!StringUtil.isEmpty(aScript))
				script.append(aScript);
		}
		return script.toString();
	}
}
