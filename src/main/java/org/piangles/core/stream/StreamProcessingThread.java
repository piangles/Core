package org.piangles.core.stream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.piangles.core.services.remoting.BeneficiaryThread;

public class StreamProcessingThread<I, O> extends BeneficiaryThread
{
	private BlockingQueue<Streamlet<I>> blockingQueue = null;
	private StreamProcessor<I,O> processor = null;
	
	public StreamProcessingThread(StreamProcessor<I,O> processor)
	{
		this.processor = processor;
		blockingQueue =  new LinkedBlockingQueue<Streamlet<I>>(); 
	}
	
	@Override
	public final void run()
	{
		Streamlet<I> streamlet = null;
		while(true)
		{
			try
			{
				streamlet = blockingQueue.take();
				
				if (!streamlet.isEndOfStreamMessage())
				{
					O output = processor.process(streamlet.getPayload());
					processOutput(output);
				}
				else//It is EndOfStreamMessage
				{
					break;
				}
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception trying to take from blocking queue because of: " + e.getMessage());
				e.printStackTrace(System.err);

				break;
			}
		}
	}
	
	public final BlockingQueue<Streamlet<I>> getBlockingQueue()
	{
		return blockingQueue;
	}
	
	protected void processOutput(O output)
	{
		//By default ignore the output of the stream processor
	}
}
