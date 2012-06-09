package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import edu.hziee.common.xslt2web.configxml.MarcoConfigItem;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.WebDataListener;

public class TableRelation {
	private ArrayList<String> masterFields;
	private ArrayList<String> detailFields;
	private AbstractXmlTableResolver masterResolver;
	private AbstractXmlTableResolver detailResolver;
	private RelationType type;
	private BaseDataSet hostDataSet;
	private IWebData webData;
	private MarcoConfigItem filterSQL;
	private String order;
	private ExpressionDataListener dataListener;

	private TableRelation() {
		masterFields = new ArrayList<String>();
		detailFields = new ArrayList<String>();
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String masterField,
			String detailField, RelationType type, BaseDataSet hostDataSet,
			MarcoConfigItem filterSQL, String order) {
		this();
		this.masterFields.add(masterField);
		this.detailFields.add(detailField);
		this.masterResolver = masterResolver;
		this.detailResolver = detailResolver;
		setType(type);
		setHostData(hostDataSet);
		this.filterSQL = filterSQL;
		this.order = order;
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String masterField,
			String detailField) {
		this(masterResolver, detailResolver, masterField, detailField,
				RelationType.MasterRelation);
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String[] masterFields,
			String[] detailFields) {
		this(masterResolver, detailResolver, masterFields, detailFields,
				RelationType.MasterRelation);
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String masterField,
			String detailField, RelationType type) {
		this(masterResolver, detailResolver, masterField, detailField, type,
				null, null, "");
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String[] masterFields,
			String[] detailFields, RelationType type) {
		this(masterResolver, detailResolver, masterFields, detailFields, type,
				null, null, "");
	}

	public TableRelation(AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, String[] masterFields,
			String[] detailFields, RelationType type, BaseDataSet hostDataSet,
			MarcoConfigItem filterSQL, String order) {
		this();
		addStringArray(this.masterFields, masterFields);
		addStringArray(this.detailFields, detailFields);
		this.masterResolver = masterResolver;
		this.detailResolver = detailResolver;
		// Debug.Assert(masterFields.Length == detailFields.Length,
		// "晕倒！主从表Field的个数居然不一样，这样怎么确定关系。");
		setType(type);
		setHostData(hostDataSet);
		this.filterSQL = filterSQL;
		this.order = order;
	}

	private static void addStringArray(ArrayList<String> list, String[] strings) {
		for (String string : strings)
			list.add(string);
	}

	private void setHostData(BaseDataSet hostDataSet) {
		this.hostDataSet = hostDataSet;
		if (hostDataSet == null)
			return;
		if (hostDataSet instanceof IWebData) {
			webData = (IWebData) hostDataSet;
			dataListener = new WebDataListener(webData, hostDataSet);
		}
	}

	public final ArrayList<String> getMasterFields() {
		return masterFields;
	}

	public final ArrayList<String> getDetailFields() {
		return detailFields;
	}

	public final AbstractXmlTableResolver getMasterResolver() {
		return masterResolver;
	}

	public final AbstractXmlTableResolver getDetailResolver() {
		return detailResolver;
	}

	public final RelationType getType() {
		return type;
	}

	public final void setType(RelationType type) {
		this.type = type;
		if ((type.getValue() & RelationType.MasterValue.getValue()) == RelationType.MasterValue
				.getValue())
			detailResolver.addUpdatingRowListener(new UpdatingRowListener() {
				public void updatingRow(UpdatingEventArgs e) {
					setDetailFieldValue(e);
				}
			});
		else if ((type.getValue() & RelationType.DetailValue.getValue()) == RelationType.DetailValue
				.getValue())
			masterResolver.addUpdatingRowListener(new UpdatingRowListener() {
				public void updatingRow(UpdatingEventArgs e) {
					setMasterFieldValue(e);
				}
			});
	}

	public final BaseDataSet getHostDataSet() {
		return hostDataSet;
	}

	public final void setHostDataSet(BaseDataSet hostDataSet) {
		setHostData(hostDataSet);
	}

	public final MarcoConfigItem getFilterSQL() {
		return filterSQL;
	}

	public final void setFilterSQL(MarcoConfigItem filterSQL) {
		this.filterSQL = filterSQL;
	}

	public void fillDetailTable() {
		if ((type.getValue() & RelationType.OnlyFill.getValue()) != RelationType.OnlyFill
				.getValue())
			return;
		DataTable table = masterResolver.getHostTable();
		// Debug.Assert(table != null && table.Rows.Count == 1,
		// "主表不存在，或者记录数不为一条！");

		DataRow row = table.getRows().getItem(0);
		String filterSQL = this.filterSQL == null ? "" : this.filterSQL
				.toString(webData.getAppGlobal().getRegsCollection(),
						dataListener);
		if (detailFields.size() == 1)
			detailResolver.selectWithParam(filterSQL, order, detailFields
					.get(0), row.getItem(masterFields.get(0)));
		else {
			String[] detailFields = new String[this.detailFields.size()];
			this.detailFields.toArray(detailFields);
			Object[] masterValues = new Object[masterFields.size()];
			for (int i = 0; i < masterFields.size(); ++i)
				masterValues[i] = row.getItem(masterFields.get(i));
			detailResolver.selectWithParams(filterSQL, order, detailFields,
					masterValues);
		}
		detailResolver.addVirtualFields();
	}

	public final void addFillingResolver(HandledCollection handled) {
		if ((type.getValue() & RelationType.OnlyFill.getValue()) != RelationType.OnlyFill
				.getValue())
			handled.addResolvers(detailResolver);
	}

	public final void fillDetailTable(HandledCollection handled) {
		if (!handled.getItem(detailResolver))
			fillDetailTable();
	}

	private void setDetailFieldValue(UpdatingEventArgs e) {
		switch (e.getStatus()) {
		case Insert:
		case Update:
			DataRow masterRow = masterResolver.getHostTable().getRows()
					.getItem(0);
			if (detailFields.size() == 1)
				e.getRow().setItem(detailFields.get(0),
						masterRow.getItem(masterFields.get(0)));
			else {
				for (int i = 0; i < masterFields.size(); ++i)
					e.getRow().setItem(detailFields.get(i),
							masterRow.getItem(masterFields.get(i)));
			}
			break;
		}
	}

	private void setMasterFieldValue(UpdatingEventArgs e) {
		switch (e.getStatus()) {
		case Insert:
		case Update:
			DataRow detailRow = detailResolver.getHostTable().getRows()
					.getItem(0);
			;
			if (detailFields.size() == 1)
				e.getRow().setItem(masterFields.get(0),
						detailRow.getItem(detailFields.get(0)));
			else {
				for (int i = 0; i < masterFields.size(); ++i)
					e.getRow().setItem(masterFields.get(i),
							detailRow.getItem(detailFields.get(i)));
			}
			break;
		}
	}

}
