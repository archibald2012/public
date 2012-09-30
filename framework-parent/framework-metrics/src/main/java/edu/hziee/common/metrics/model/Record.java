/**
 * 
 */
package edu.hziee.common.metrics.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.metrics.util.SystemUtil;

/**
 * @author Administrator
 * 
 */
@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Record", propOrder = { "measurementList", "aggregationList", "usageList" })
public class Record implements Serializable {

	private static final long		serialVersionUID	= 1L;

	@XmlAttribute(name = "id", required = true)
	private String							id;

	@XmlAttribute(name = "domain", required = true)
	private String							domain;

	@XmlAttribute(name = "host", required = true)
	private String							host;

	@XmlAttribute(name = "application", required = true)
	private String							applicationName;

	@XmlAttribute(name = "framework", required = true)
	private String							frameworkName;

	@XmlAttribute(name = "version")
	private String							version;

	@XmlAttribute(name = "user")
	private String							user;

	@XmlAttribute(name = "pid")
	private String							pid;

	@XmlAttribute(name = "ranges")
	private String							aggregationRanges;

	@XmlElement(name = "measurement")
	private List<Measurement>		measurementList;

	@XmlElement(name = "aggregation")
	private List<Aggregation>		aggregationList;

	@XmlElement(name = "usage")
	private List<ResourceUsage>	usageList;

	@XmlTransient
	private Date								createdAt;

	public Record() {

	}

	public Record(String domain, String host, String applicationName, String frameworkName, String version, String user,
			String pid) {
		this.id = SystemUtil.createGuid();
		this.domain = domain;
		this.host = host;
		this.applicationName = applicationName;
		this.frameworkName = frameworkName;
		this.version = version;
		this.user = user;
		this.pid = pid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getFrameworkName() {
		return frameworkName;
	}

	public void setFrameworkName(String frameworkName) {
		this.frameworkName = frameworkName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAggregationRanges() {
		return aggregationRanges;
	}

	public void setAggregationRanges(String aggregationRanges) {
		this.aggregationRanges = aggregationRanges;
	}

	public List<Measurement> getMeasurementList() {
		if (measurementList == null) {
			measurementList = new ArrayList<Measurement>();
		}
		return measurementList;
	}

	public void setMeasurementList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
	}

	public List<Aggregation> getAggregationList() {
		if (aggregationList == null) {
			aggregationList = new ArrayList<Aggregation>();
		}
		return aggregationList;
	}

	public void setAggregationList(List<Aggregation> aggregationList) {
		this.aggregationList = aggregationList;
	}

	public List<ResourceUsage> getUsageList() {
		if (usageList == null) {
			usageList = new ArrayList<ResourceUsage>();
		}
		return usageList;
	}

	public void setUsageList(List<ResourceUsage> usageList) {
		this.usageList = usageList;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
