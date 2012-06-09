package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class UploadConfigItem {
	private Node configNode;
	private UploadKindType kind;
	private String serverPathField;
	private String contentField;
	private String mimeTypeField;
	private String sizeField;
	private int maxSize;
	private String fileExt;
	private String onUploadedEvent;

	public UploadConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final UploadKindType getKind() {
		return this.kind;
	}

	public final String getServerPathField() {
		return this.serverPathField;
	}

	public final String getContentField() {
		return this.contentField;
	}

	public final String getMimeTypeField() {
		return this.mimeTypeField;
	}

	public final String getSizeField() {
		return this.sizeField;
	}

	public final int getMaxSize() {
		return this.maxSize;
	}

	public final String getFileExt() {
		return this.fileExt;
	}

	public final String getOnUploadedEvent() {
		return this.onUploadedEvent;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String kindValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Kind"), "DB");
		this.kind = (UploadKindType) Enum.valueOf(UploadKindType.class,
				kindValue);
		this.serverPathField = attributes.getNamedItem("ServerPathField")
				.getTextContent();
		this.contentField = attributes.getNamedItem("ContentField")
				.getTextContent();
		this.mimeTypeField = attributes.getNamedItem("MimeTypeField")
				.getTextContent();
		this.sizeField = attributes.getNamedItem("SizeField").getTextContent();
		this.maxSize = XmlUtil.getNodeIntValue(attributes
				.getNamedItem("MaxSize"), 100);
		this.fileExt = XmlUtil.getNodeContent(attributes
				.getNamedItem("FileExt"));
		this.onUploadedEvent = XmlUtil.getNodeContent(attributes
				.getNamedItem("OnUploadedEvent"));
	}

	public static UploadConfigItem parse(Node node) {
		if (node == null)
			return null;
		UploadConfigItem result = new UploadConfigItem();
		result.readXml(node);
		return result;
	}
}