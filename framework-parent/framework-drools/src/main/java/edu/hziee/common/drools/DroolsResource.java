/**
 * 
 */
package edu.hziee.common.drools;

import org.drools.builder.ResourceType;

/**
 * @author Administrator
 * 
 */
public class DroolsResource {

	private String name;
	private ResourceType type;
	private String content;

	public DroolsResource(String name, ResourceType resourceType, String content) {
		this.name = name;
		this.type = resourceType;
		this.content = content;
	}
	
	public DroolsResource(String name, String type, String content){
		ResourceType resourceType = ResourceType.getResourceType(type);
		if(resourceType==null){
			throw new DroolsException("Unknown resource type " + type + " for name " + name);
		}
		
		this.name = name;
		this.type = resourceType;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static boolean isRuleType(String scriptType) {
		if (scriptType == null) {
			return false;
		}

		ResourceType resourceType = ResourceType.getResourceType(scriptType);
		if (resourceType == null) {
			return false;
		}

		return resourceType != ResourceType.BPMN2
				&& resourceType != ResourceType.DRF;
	}
}
