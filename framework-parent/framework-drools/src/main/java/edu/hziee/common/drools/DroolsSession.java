/**
 * 
 */
package edu.hziee.common.drools;

import java.util.List;
import java.util.Map;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;

/**
 * @author Administrator
 *
 */
public interface DroolsSession {

	int addKnowledgeResources(List<DroolsResource> resourceList);
	
	boolean addKnowledgeResource(DroolsResource resource);
	
	FactHandle insert(Object object);
	
	void retract(FactHandle fact);
	
	void fireAllRules(String agendaGroup);
	
	void fireAllRules(String agendaGroup, Object object);
	
	void startProcess(String processId);
	
	void startProcess(String processId, Map<String, Object> parameters);
	
	QueryResults queryFacts(String queryName, Object... parameters);
	
	void setGlobal(String name, Object value);
	
	boolean isWorkflowType(String scriptType);
	
	void dispose();
	
	boolean isDisposed();
	
}
