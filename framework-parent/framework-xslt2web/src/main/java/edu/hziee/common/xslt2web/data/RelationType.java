package edu.hziee.common.xslt2web.data;

public enum RelationType {
	OnlyFill(0x1), MasterValue(0x2), MasterRelation(0x3), DetailValue(0x4), DetailRelation(
			0x5);

	private int value;

	private RelationType(int value) {
		this.value = value;
	}

	public final int getValue() {
		return value;
	}
}