package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.data.UpdateKindType;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class UpdatingConfigItem extends MarcoConfigItem {
	private UpdateKindType updateKind;

	public UpdatingConfigItem() {
	}

	public final UpdateKindType getUpdateKind() {
		return this.updateKind;
	}

	public void readXml(Node node) {
		super.readXml(node);

		NamedNodeMap attributes = node.getAttributes();
		String updateKindValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("UpdateKind"), "Insert");
		this.updateKind = (UpdateKindType) Enum.valueOf(UpdateKindType.class,
				updateKindValue);
	}

	public static UpdatingConfigItem parse(Node node) {
		if (node == null)
			return null;
		UpdatingConfigItem result = new UpdatingConfigItem();
		result.readXml(node);
		return result;
	}
}