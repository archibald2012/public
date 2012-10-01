/**
 * 
 */
package edu.hziee.common.metrics.process;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.MetricsPublishWorker;
import edu.hziee.common.metrics.model.Publishable;

/**
 * @author Administrator
 * 
 */
public class DefaultProcessWorker extends Thread implements MetricsPublishWorker {
	private static final Logger	logger	= LoggerFactory.getLogger(DefaultProcessWorker.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsPublishWorker#enqueuePublishable(edu.hziee.common.metrics.model.Publishable)
	 */
	@Override
	public void enqueuePublishable(Publishable publishable) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.hziee.common.metrics.MetricsPublishWorker#enqueuePublishable(java.util.Collection)
	 */
	@Override
	public void enqueuePublishable(Collection<? extends Publishable> publishables) {
		// TODO Auto-generated method stub

	}

}
