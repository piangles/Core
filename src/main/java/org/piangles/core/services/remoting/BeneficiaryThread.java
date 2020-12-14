package org.piangles.core.services.remoting;

import java.util.UUID;

/**
 * This thread can be created from RequestProcessingThread or in the Adapter classes
 * where processing of response needs to take place Asynchronously.
 * TODO: This needs to be pooled eventually for faster creation of thread or use Future in RequestProcessingThread 
 *
 * The Passion of the Christ
 * Jesus Christ: Those who live by the sword shall die by the sword
 */
public final class BeneficiaryThread extends Thread implements Traceable, SessionAwareable
{
	private SessionDetails sessionDetails = null;
	private UUID traceId = null;
	private Runnable runnable = null;
	
	public BeneficiaryThread(Runnable runnable)
	{
		this.runnable = runnable;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof Traceable)
		{
			traceId = ((Traceable)currentThread).getTraceId();
		}
		if (currentThread instanceof SessionAwareable)
		{
			sessionDetails = ((SessionAwareable)currentThread).getSessionDetails();
		}
	}
	
	public void run()
	{
		runnable.run();
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	Runnable getRunnable()
	{
		return runnable;
	}
}