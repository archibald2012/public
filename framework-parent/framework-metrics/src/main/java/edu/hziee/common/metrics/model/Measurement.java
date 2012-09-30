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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.lang.NameValue;

/**
 * @author Administrator
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Measurement", propOrder = { "metricsList" })
public class Measurement implements Publishable  , Serializable {

	private static final long	serialVersionUID	= 1L;
	
	@XmlTransient
	private static final String	METRICS_EXCEPTION					= "exception";

	@XmlTransient
	private static final String	METRICS_EXCEPTION_MESSAGE	= "exceptionMessage";

	@XmlAttribute(name = "id", required = true)
	private String							id;

	@XmlAttribute(name = "parent")
	private String							parentId;

	@XmlTransient
	private String							recordId;

	@XmlAttribute(name = "correlation")
	private String							correlationId;

	@XmlAttribute(name = "requester")
	private String							correlationRequester;

	@XmlAttribute(name = "component", required = true)
	private String							componentName;

	@XmlAttribute(name = "function", required = true)
	private String							functionName;

	@XmlAttribute(name = "thread", required = true)
	private String							threadName;

	@XmlAttribute(name = "user", required = true)
	private String							user;

	@XmlAttribute(name = "timestamp", required = true)
	private Date								timestamp;

	@XmlAttribute(name = "duration", required = true)
	private Long								duration;

	@XmlAttribute(name = "units", required = true)
	private Integer							workUnits;

	@XmlAttribute(name = "order")
	private Integer							createOrder;

	@XmlAttribute(name = "fail")
	private Boolean							failStatus;

	@XmlElement(name = "metrcis")
	private List<NameValue>			metricsList;

	public Measurement() {

	}

	public Measurement(long duration, List<NameValue> metricsList, Throwable t) {
		this.duration = duration;
		this.metricsList = metricsList;
		this.threadName = Thread.currentThread().getName();

		if (t != null) {
			this.failStatus = true;
			addMetrics(METRICS_EXCEPTION, t.getClass().getName());
			addMetrics(METRICS_EXCEPTION_MESSAGE, t.getMessage());
		}
	}

	@Override
	public String getRecordId() {
		return recordId;
	}

	@Override
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getCorrelationRequester() {
		return correlationRequester;
	}

	public void setCorrelationRequester(String correlationRequester) {
		this.correlationRequester = correlationRequester;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getWorkUnits() {
		return workUnits;
	}

	public void setWorkUnits(Integer workUnits) {
		this.workUnits = workUnits;
	}

	public Integer getCreateOrder() {
		return createOrder;
	}

	public void setCreateOrder(Integer createOrder) {
		this.createOrder = createOrder;
	}

	public Boolean getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(Boolean failStatus) {
		this.failStatus = failStatus;
	}

	public List<NameValue> getMetricsList() {
		if (metricsList == null) {
			metricsList = new ArrayList<NameValue>();
		}
		return metricsList;
	}

	public void setMetricsList(List<NameValue> metricsList) {
		this.metricsList = metricsList;
	}

	public void addMetrics(String name, String value) {
		getMetricsList().add(new NameValue(name, value));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
