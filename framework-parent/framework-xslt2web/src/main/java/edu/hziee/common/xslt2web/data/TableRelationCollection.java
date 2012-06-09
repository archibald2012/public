package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

public class TableRelationCollection extends ArrayList<TableRelation> {
	private static final long serialVersionUID = 1L;

	public final void fillDetailTable() {
		for (TableRelation relation : this)
			relation.fillDetailTable();
	}

	public final void fillDetailTable(HandledCollection handled) {
		for (TableRelation relation : this)
			relation.fillDetailTable(handled);
	}

	public final void addFillingResolver(HandledCollection handled) {
		for (TableRelation relation : this)
			relation.addFillingResolver(handled);
	}
}
