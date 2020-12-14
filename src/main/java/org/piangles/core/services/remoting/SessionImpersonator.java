package org.piangles.core.services.remoting;

import java.util.UUID;

public class SessionImpersonator<R, E extends Exception> extends Thread implements SessionAwareable, Traceable
{
	//TODO Rename this to SessionImpersonator
	private ServiceTask task = null;
	private R response = null;
	private Exception expt = null;
	private SessionDetails sessionDetails = null;
	private UUID traceId = null;
	
	public SessionImpersonator(ServiceTask task)
	{
		Thread currentThread = Thread.currentThread();
		if (currentThread instanceof RequestProcessingThread)
		{
			RequestProcessingThread rpt = (RequestProcessingThread) currentThread;
			this.traceId = rpt.getTraceId();
			this.sessionDetails = new SessionDetails(rpt.getServiceName(), rpt.getPreApprovedSessionId());
		}
		this.task = task;
	}
	
	public R execute() throws E
	{
		try
		{
			start();
			join();
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		if (expt != null)
		{
			throw (E)expt;
		}
		return (R)response;
	}
	
	public void run()
	{
		try
		{
			response = (R)task.execute();
		}
		catch (Exception e)
		{
			expt = e;
		}
	}
	
	@Override
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}

	@Override
	public UUID getTraceId()
	{
		return traceId;
	}
}
