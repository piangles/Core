/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.stream;

import java.util.Optional;
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

				Optional<O> output = processor.process(Optional.ofNullable(streamlet.getPayload()));
				processOutput(output);

				if (streamlet.isEndOfStreamMessage())
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
	
	protected void processOutput(Optional<O> output)
	{
		//By default ignore the output of the stream processor
	}
}
