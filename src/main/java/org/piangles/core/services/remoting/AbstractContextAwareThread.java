package org.piangles.core.services.remoting;

import java.util.UUID;

/**
 */
public abstract class AbstractContextAwareThread extends Thread implements Traceable, SessionAwareable
{
	private SessionDetails sessionDetails = null;
	private UUID traceId = null;
	
	protected final void init(SessionDetails sessionDetails, UUID traceId)
	{
		this.sessionDetails = sessionDetails;
		this.traceId = traceId;
	}
	
	public final UUID getTraceId()
	{
		return traceId;
	}
	
	public final SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	public abstract void run();
}
