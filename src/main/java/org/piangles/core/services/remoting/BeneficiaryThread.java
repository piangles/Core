package org.piangles.core.services.remoting;

import java.util.UUID;

/**
 * This thread can be created from RequestProcessingThread or in the Adapter classes
 * where processing of response needs to take place Asynchronously. It inherits 
 * the context (TraceId and SessionDetails) from the parent thread.
 * 
 * The Passion of the Christ
 * Jesus Christ: Those who live by the sword shall die by the sword
 */
public class BeneficiaryThread extends AbstractContextAwareThread
{
	private Runnable runnable = null;
	
	public BeneficiaryThread()
	{
		init();
	}
	
	public BeneficiaryThread(Runnable runnable)
	{
		init();
		this.runnable = runnable;
	}
	
	public void run()
	{
		if (runnable != null)
		{
			runnable.run();
		}
	}
	
	private void init()
	{
		SessionDetails sessionDetails = null;
		UUID traceId = null;

		Object currentThread = Thread.currentThread();
		if (currentThread instanceof Traceable)
		{
			traceId = ((Traceable)currentThread).getTraceId();
		}
		if (currentThread instanceof SessionAwareable)
		{
			sessionDetails = ((SessionAwareable)currentThread).getSessionDetails();
		}

		super.init(sessionDetails, traceId);
	}
}
