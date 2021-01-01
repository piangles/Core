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
