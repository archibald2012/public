/**
 * 
 */
package edu.hziee.common.drools;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class DefaultDroolsProcessor implements DroolsProcessor {

	private static final Logger			logger			= LoggerFactory.getLogger(DefaultDroolsProcessor.class);

	private KnowledgeBase				knowledgeBase;
	private ThreadLocal<DroolsSession>	localDroolsSession;
	private ReentrantLock				processorLock	= null;
	private boolean						concurrency		= true;

	public DefaultDroolsProcessor() {
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		processorLock = new ReentrantLock();

		localDroolsSession = new ThreadLocal<DroolsSession>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsProcessor#addKnowledgeResources(java.util.List)
	 */
	@Override
	public int addKnowledgeResources(List<DroolsResource> resourceList) {
		int count = 0;
		if (processorLock != null) {
			processorLock.lock();
		}
		try {
			KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			for (DroolsResource resource : resourceList) {
				try {
					if (resource.getType() != null && resource.getContent() != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Adding resource name " + resource.getName() + " and type "
									+ resource.getType());
						}
						knowledgeBuilder.add(ResourceFactory.newByteArrayResource(resource.getContent().getBytes()),
								resource.getType());
					}
				} catch (Exception e) {
					logger.error("failed to add resource " + resource.getName() + " to knowledge builder.", e);
				}

				KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
				if (errors.size() > 0) {
					for (KnowledgeBuilderError error : errors) {
						logger.error(error.toString());
					}
					throw new DroolsException("Failed to parse resource " + resource.getName() + " with type "
							+ resource.getType());
				} else {
					count++;
				}
			}

			Collection<KnowledgePackage> knowledgePackages = knowledgeBuilder.getKnowledgePackages();

			// replace old packages.
			for (KnowledgePackage knowledgePackage : knowledgePackages) {
				if (knowledgeBase.getKnowledgePackage(knowledgePackage.getName()) != null) {
					knowledgeBase.removeKnowledgePackage(knowledgePackage.getName());
					if (logger.isDebugEnabled()) {
						logger.debug("Replacing old knowledgePackage " + knowledgePackage.getName());
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Adding new knowledgePackage " + knowledgePackage.getName());
				}
			}
			knowledgeBase.addKnowledgePackages(knowledgePackages);

		} finally {
			if (processorLock != null) {
				processorLock.unlock();
			}
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsProcessor#addKnowledgeResource(edu.hziee.common.drools.DroolsResource)
	 */
	@Override
	public boolean addKnowledgeResource(DroolsResource resource) {
		boolean isAdded = false;

		if (processorLock != null) {
			processorLock.lock();
		}
		try {
			KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

			try {
				if (resource.getType() != null && resource.getContent() != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Adding resource name " + resource.getName() + " and type " + resource.getType());
					}
					knowledgeBuilder.add(ResourceFactory.newByteArrayResource(resource.getContent().getBytes()),
							resource.getType());
				}
			} catch (Exception e) {
				logger.error("failed to add resource " + resource.getName() + " to knowledge builder.", e);
			}

			KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error : errors) {
					logger.error(error.toString());
				}
				throw new DroolsException("Failed to parse resource " + resource.getName() + " with type "
						+ resource.getType());
			} else {
				isAdded = true;
			}

			Collection<KnowledgePackage> knowledgePackages = knowledgeBuilder.getKnowledgePackages();

			// replace old packages.
			for (KnowledgePackage knowledgePackage : knowledgePackages) {
				if (knowledgeBase.getKnowledgePackage(knowledgePackage.getName()) != null) {
					knowledgeBase.removeKnowledgePackage(knowledgePackage.getName());
					if (logger.isDebugEnabled()) {
						logger.debug("Replacing old knowledgePackage " + knowledgePackage.getName());
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Adding new knowledgePackage " + knowledgePackage.getName());
				}
			}
			knowledgeBase.addKnowledgePackages(knowledgePackages);

		} finally {
			if (processorLock != null) {
				processorLock.unlock();
			}
		}
		return isAdded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsProcessor#createDroolsSession()
	 */
	@Override
	public DroolsSession createDroolsSession() {
		DroolsSession session = null;

		if (processorLock != null) {
			processorLock.lock();
		}
		try {
			session = localDroolsSession.get();
			if (session != null) {
				session.dispose();
				localDroolsSession.remove();
			}
			session = new DefaultDroolsSession(this);
			localDroolsSession.set(session);
			return session;
		} finally {
			if (processorLock != null) {
				processorLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsProcessor#getCurrentDroolsSession()
	 */
	@Override
	public DroolsSession getCurrentDroolsSession() {
		return localDroolsSession.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.drools.DroolsProcessor#isResourceType(java.lang.String)
	 */
	@Override
	public boolean isResourceType(String scriptType) {
		return ResourceType.getResourceType(scriptType) != null;
	}

	protected StatefulKnowledgeSession createStatefulKnowledgeSession() {
		return knowledgeBase.newStatefulKnowledgeSession();
	}

	protected KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	protected void resetDroolsSession() {
		if (processorLock != null) {
			processorLock.lock();
		}
		try {
			localDroolsSession.remove();
		} finally {
			if (processorLock != null) {
				processorLock.unlock();
			}
		}
	}

	@Override
	public boolean isConcurrency() {
		return this.concurrency;
	}

}
