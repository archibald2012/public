package edu.hziee.common.xslt2web.easysearch;

import java.util.ArrayList;
import java.util.HashMap;

import edu.hziee.common.xslt2web.constraint.BaseConstraint;
import edu.hziee.common.xslt2web.constraint.ConstraintCollection;
import edu.hziee.common.xslt2web.data.AbstractXmlTableResolver;
import edu.hziee.common.xslt2web.data.BaseDataSet;

public class EasySearchFieldCollection extends ArrayList<BaseEasySearchField> {

	private static final long serialVersionUID = 1L;

	private HashMap<String, BaseEasySearchField> fieldTable = new HashMap<String, BaseEasySearchField>();

	public final boolean contains(String fieldName) {
		return fieldTable.containsKey(fieldName);
	}

	public final void addConstraints(ConstraintCollection constraints) {
		for (BaseEasySearchField item : this) {
			if (item.isAutoConstraint()) {
				BaseConstraint constraint = item.getConstraint();
				if (constraint != null)
					constraints.add(constraint);
			}
		}
	}

	public final BaseEasySearchField add(String fieldName, String displayName,
			String regName, BaseDataSet dataSet,
			AbstractXmlTableResolver resolver) {
		return add(fieldName, displayName, regName, dataSet, resolver, true);
	}

	public final BaseEasySearchField add(String fieldName, String displayName,
			String regName, BaseDataSet dataSet,
			AbstractXmlTableResolver resolver, boolean createConstraint) {
		BaseEasySearchField item = new EasySearchField(fieldName, displayName,
				regName, dataSet, createConstraint);
		add(item, resolver);
		return item;
	}

	public final void add(BaseEasySearchField item,
			AbstractXmlTableResolver resolver) {
		resolver.addNavigateDataListener(item);
		fieldTable.put(item.getFieldName(), item);
		this.add(item);
	}
}
