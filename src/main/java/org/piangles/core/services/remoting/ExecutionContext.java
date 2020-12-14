package org.piangles.core.services.remoting;

import org.piangles.core.stream.Stream;

public final class ExecutionContext
{
	private Stream<?> stream = null;
	
	private ExecutionContext(Stream<?> stream)
	{
		this.stream = stream;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Stream<T> getStream()
	{
		return (Stream<T>)stream;
	}
	
	public static final ExecutionContext get()
	{
		Stream<?> stream = null;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof BeneficiaryThread)
		{
			BeneficiaryThread bt = (BeneficiaryThread)currentThread;
			if (bt.getRunnable() instanceof StreamingRequestProcessor)
			{
				StreamingRequestProcessor srp = (StreamingRequestProcessor) bt.getRunnable();
				stream = srp.getStream();
			}
		}
		
		return new ExecutionContext(stream);
	}
}
