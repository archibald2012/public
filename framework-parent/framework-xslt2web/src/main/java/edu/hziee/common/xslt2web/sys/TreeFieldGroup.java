package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.TypeCode;

public class TreeFieldGroup {
	private String id;
	private String name;
	private String parentID;
	private String layer;
	private String isLeaf;
	private TypeCode idType;
	private String merge;
	private String active;

	public TreeFieldGroup(String id, String name, String parentID,
			String layer, String isLeaf, TypeCode idType) {
		super();
		this.id = id;
		this.name = name;
		this.parentID = parentID;
		this.layer = layer;
		this.isLeaf = isLeaf;
		this.idType = idType;
	}

	public TreeFieldGroup(String id, String name, String parentID,
			String layer, String isLeaf, TypeCode idType, String merge,
			String active) {
		this(id, name, parentID, layer, isLeaf, idType);
		this.merge = merge;
		this.active = active;
	}

	public final String getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final String getParentID() {
		return parentID;
	}

	public final String getLayer() {
		return layer;
	}

	public final String getIsLeaf() {
		return isLeaf;
	}

	public final String getMerge() {
		return merge;
	}

	public final String getActive() {
		return active;
	}

	public final TypeCode getIdType() {
		return idType;
	}

}
