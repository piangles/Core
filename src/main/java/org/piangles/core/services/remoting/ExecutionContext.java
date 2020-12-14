package org.piangles.core.services.remoting;

import java.util.Optional;

import org.piangles.core.stream.Stream;

public final class ExecutionContext
{
	private Optional<Stream> stream = null;
	
	private ExecutionContext(Stream stream)
	{
		this.stream = Optional.of(stream);
	}
	
	public Optional<Stream> getStream()
	{
		return stream;
	}
	
	public static final ExecutionContext get()
	{
		Stream stream = null;
		
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
