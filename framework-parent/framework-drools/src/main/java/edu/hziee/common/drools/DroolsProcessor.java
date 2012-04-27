/**
 * 
 */
package edu.hziee.common.drools;

import java.util.List;

/**
 * @author Administrator
 * 
 */
public interface DroolsProcessor {

	int addKnowledgeResources(List<DroolsResource> resourceList);

	boolean addKnowledgeResource(DroolsResource resource);

	DroolsSession createDroolsSession();

	DroolsSession getCurrentDroolsSession();

	boolean isResourceType(String scriptType);
	
	boolean isConcurrency();
}
