/**
 * 
 */
package edu.hziee.common.drools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultDroolsSession implements DroolsSession {

	private static final Logger			logger		= LoggerFactory.getLogger(DefaultDroolsSession.class);
	private DefaultDroolsProcessor		droolsProcessor;
	private StatefulKnowledgeSession	statefulSession;
	private ReentrantLock				sessionLock;
	private boolean						isDisposed	= false;

	public DefaultDroolsSession(DefaultDroolsProcessor processor) {
		this.droolsProcessor = processor;
		this.statefulSession = processor.createStatefulKnowledgeSession();
		if (droolsProcessor.isConcurrency()) {
			this.sessionLock = new ReentrantLock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#addKnowledgeResources(java.util .List)
	 */
	@Override
	public int addKnowledgeResources(List<DroolsResource> resourceList) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			return droolsProcessor.addKnowledgeResources(resourceList);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#addKnowledgeResource(edu.hziee. common.drools.DroolsResource)
	 */
	@Override
	public boolean addKnowledgeResource(DroolsResource resource) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			return droolsProcessor.addKnowledgeResource(resource);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#insert(java.lang.Object)
	 */
	@Override
	public FactHandle insert(Object object) {
		FactHandle fact = null;
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			if (object != null) {
				fact = statefulSession.insert(object);
			}
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
		return fact;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#retract(org.drools.runtime.rule .FactHandle)
	 */
	@Override
	public void retract(FactHandle fact) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			statefulSession.retract(fact);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#fireAllRules(java.lang.String)
	 */
	@Override
	public void fireAllRules(String agendaGroup) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			if (agendaGroup != null && agendaGroup.length() > 0) {
				statefulSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
			}
			statefulSession.fireAllRules();
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#fireAllRules(java.lang.String, java.lang.Object)
	 */
	@Override
	public void fireAllRules(String agendaGroup, Object object) {
		FactHandle fact = null;
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			if (agendaGroup != null && agendaGroup.length() > 0) {
				statefulSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
			}
			if (object != null) {
				fact = statefulSession.insert(object);
			}
			statefulSession.fireAllRules();
			if (fact != null) {
				statefulSession.retract(fact);
			}
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#startProcess(java.lang.String)
	 */
	@Override
	public void startProcess(String processId) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting workflow process " + processId);
			}
			statefulSession.startProcess(processId);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#startProcess(java.lang.String, java.util.Map)
	 */
	@Override
	public void startProcess(String processId, Map<String, Object> parameters) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting workflow process " + processId);
			}
			if (parameters != null) {
				statefulSession.startProcess(processId, parameters);
			} else {
				statefulSession.startProcess(processId);
			}
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#queryFacts(java.lang.String, java.lang.Object[])
	 */
	@Override
	public QueryResults queryFacts(String queryName, Object... parameters) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			return statefulSession.getQueryResults(queryName, parameters);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#setGlobal(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setGlobal(String name, Object value) {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			statefulSession.setGlobal(name, value);
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#isWorkflowType(java.lang.String)
	 */
	@Override
	public boolean isWorkflowType(String scriptType) {
		return DroolsResource.isRuleType(scriptType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#dispose()
	 */
	@Override
	public void dispose() {
		if (sessionLock != null) {
			sessionLock.lock();
		}
		try {
			statefulSession.dispose();
			droolsProcessor.resetDroolsSession();
			isDisposed = true;
		} finally {
			if (sessionLock != null) {
				sessionLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsSession#isDisposed()
	 */
	@Override
	public boolean isDisposed() {
		return this.isDisposed;
	}

}
