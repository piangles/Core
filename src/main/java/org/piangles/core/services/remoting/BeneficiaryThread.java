package org.piangles.core.services.remoting;

import java.util.UUID;

import org.piangles.core.stream.Stream;

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
	private Stream<?> stream;
	private Runnable runnable = null;
	
	BeneficiaryThread(Stream<?> stream, Runnable runnable)
	{
		this(runnable);
		this.stream = stream;
	}
	
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

	public Stream<?> getStream()
	{
		return stream;
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
}
