/**
 * 
 */
package edu.hziee.common.lang;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Administrator
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NameValue")
public final class NameValue implements Serializable {
	
	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	@XmlAttribute(required = true)
	private String						name;

	@XmlAttribute(required = true)
	private String						value;

	public NameValue() {
	}

	public NameValue(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String toString() {
		return getValue();
	}

	/**
	 * @param list
	 * @param name
	 * @param value
	 */
	public static void addToList(List<NameValue> list, String name, String value) {
		list.add(new NameValue(name, value));
	}

	/**
	 * @param map
	 * @param name
	 * @param value
	 * @return
	 */
	public static NameValue putToMap(Map<String, NameValue> map, String name, String value) {
		return map.put(name, new NameValue(name, value));
	}
}
